package dev.fulmineo.companion_bats.entity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;

import dev.fulmineo.companion_bats.entity.ai.goal.*;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierMap;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.entity.ai.goal.HurtByTargetGoal;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.monster.CreeperEntity;
import net.minecraft.entity.passive.horse.AbstractHorseEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.FlyingPathNavigator;
import net.minecraft.pathfinding.PathNavigator;
import net.minecraft.pathfinding.PathNodeType;

import dev.fulmineo.companion_bats.CompanionBats;
import dev.fulmineo.companion_bats.entity.CompanionBatLevels.CompanionBatClassLevel;
import dev.fulmineo.companion_bats.entity.ai.control.CompanionBatMoveControl;
import dev.fulmineo.companion_bats.CompanionBatAbilities;
import dev.fulmineo.companion_bats.CompanionBatAbility;
import dev.fulmineo.companion_bats.item.CompanionBatAccessoryItem;
import dev.fulmineo.companion_bats.item.CompanionBatArmorItem;
import dev.fulmineo.companion_bats.CompanionBatClass;
import dev.fulmineo.companion_bats.nbt.EntityData;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;

public class CompanionBatEntity extends TameableEntity {
	private static final DataParameter<Byte> BAT_FLAGS;
	private static final DataParameter<Byte> COMBO_PARTICLE_LEVEL;
	public CompanionBatAbilities abilities = new CompanionBatAbilities();
	private Map<CompanionBatClass, Integer> classesExp = new HashMap<>();
	private int healTicks = HEAL_TICKS;

	// Constants

	private static final UUID BAT_EQUIPMENT_ARMOR_BONUS_ID = UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F296");
	private static final UUID BAT_ARMOR_BONUS_ID = UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F297");
	private static final UUID BAT_ATTACK_BONUS_ID = UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F298");
	private static final UUID BAT_SPEED_BONUS_ID = UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F299");

	// Configurable values
	// TODO: Add configuration for these values and levels

	public static final float BASE_HEALTH = 6.0F;
	public static final float BASE_ATTACK = 2.0F;
	public static final float BASE_SPEED = 0.35F;

	private static final int EXP_GAIN = 1;
	public static final int EXPERIENCE_PIE_GAIN = 100;

	private static final int ROOST_START_TICKS = 200;
	private static final int HEAL_TICKS = 600;
	private static final int EMERGENCY_POTION_TICKS = 4800;
	private static final int EFFECT_POTION_TICKS = 1800;
	private static final int COMBO_ATTACK_RESET_TICKS = 200;
	private static final int TELEPORT_TICKS = 7;
	private static final int RANGED_ATTACK_TICKS = 100;
	private static int EFFECT_TICKS = 320;

	public static final Predicate<ItemStack> IS_FOOD_ITEM;
	public static final Predicate<ItemEntity> IS_FOOD_ITEM_ENTITY;
	public BlockPos hangingPosition;
	public BlockPos fleeingPosition;
	public BlockPos diggingPosition;
	public int emergencyPotionTicks;
	public int effectPotionTicks;
	public int roostTicks;

	private CompanionBatClass currentClass;
	private int exp = 0;
	private int classExp = 0;
	private int currentLevel = -1;
	private int classLevel = -1;
	private boolean hasFireResistance;
	private boolean hasTeleport;
	private boolean hasAdventurerAura;
	private boolean hasMinerAura;
	private boolean hasPotionGoal;
	private boolean hasNaturalRegeneration;
	private int comboAttackResetTicks = COMBO_ATTACK_RESET_TICKS;
	private int comboLevel = 0;
	private int teleportTicks = TELEPORT_TICKS;
	private int effectTicks = 1;
	private boolean isSneakAttacking;
	private boolean guaranteedSneakAttack;
	private Entity teleportTarget;

	public CompanionBatEntity(EntityType<? extends TameableEntity> entityType, World world) {
		super(entityType, world);
		this.moveControl = new CompanionBatMoveControl(this, 10);
		this.setPathfindingMalus(PathNodeType.DANGER_FIRE, -1.0F);
		this.setPathfindingMalus(PathNodeType.DAMAGE_FIRE, -1.0F);
		this.setRoosting(false);
		this.setInSittingPose(false);
	}

	protected void defineSynchedData() {
		super.defineSynchedData();
		this.entityData.define(BAT_FLAGS, (byte) 0);
		this.entityData.define(COMBO_PARTICLE_LEVEL, (byte) 0);
	}

	public void addAdditionalSaveData(CompoundTag tag) {
		super.addAdditionalSaveData(tag);
		EntityData entityData = new EntityData(tag);
		entityData.putExp(this.getExp());
		entityData.putGuardMode(this.getGuardMode());
		this.writeExpToTag(entityData);
		this.writePotionTicks(entityData);
	}

	public void readAdditionalSaveData(CompoundTag tag) {
		EntityData entityData = new EntityData(tag);
		this.setCurrentLevel(entityData.getExp());
		this.setLevelAttributes(this.currentLevel);
		super.readAdditionalSaveData(tag);
		this.setAccessoryAbility();
		this.setArmorAndClass();
		this.setClassesExp(entityData);
		this.abilities.setFromNbt(entityData);
		this.setAbilitiesEffects(true);
		this.setPotionTicks(entityData);
	}

	protected float getSoundVolume() {
		return 0.1F;
	}

	protected float getVoicePitch() {
		return super.getVoicePitch() * 0.95F;
	}

	@Nullable
	public SoundEvent getAmbientSound() {
		return this.isRoosting() && this.random.nextInt(4) != 0 ? null : SoundEvents.BAT_AMBIENT;
	}

	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.BAT_HURT;
	}

	protected SoundEvent getDeathSound() {
		return SoundEvents.BAT_DEATH;
	}

	public boolean isPushable() {
		return false;
	}

	protected void doPush(Entity entity) {
	}

	protected void pushEntities() {
	}

	public static AttributeModifierMap.MutableAttribute createMobAttributes() {
		return MobEntity.createMobAttributes()
			.add(Attributes.MAX_HEALTH, BASE_HEALTH)
			.add(Attributes.ATTACK_DAMAGE, BASE_ATTACK)
			.add(Attributes.MOVEMENT_SPEED, BASE_SPEED)
			.add(Attributes.FOLLOW_RANGE, 24.0D);
	}

	/**
	 * Returns whether this bat is hanging upside-down under a block.
	 */
	public boolean isRoosting() {
		return (this.entityData.get(BAT_FLAGS) & 1) != 0;
	}

	public boolean isInjured() {
		return this.getHealth() < this.getMaxHealth();
	}

	public boolean isAboutToRoost() {
		return this.hangingPosition != null;
	}

	public boolean isFleeing() {
		return this.fleeingPosition != null;
	}

	public boolean isDigging() {
		return this.diggingPosition != null;
	}

	public void setRoosting(boolean roosting) {
		byte b = this.entityData.get(BAT_FLAGS);
		if (roosting) {
			this.entityData.set(BAT_FLAGS, (byte) (b | 1));
		} else {
			this.entityData.set(BAT_FLAGS, (byte) (b & -2));
			this.hangingPosition = null;
		}
	}

	/*public void startRoosting(){
		this.roostTicks = 1;
	}*/

	public void tick() {
		super.tick();
		if (this.level.isClientSide){
			Byte comboParticleLevel = this.entityData.get(COMBO_PARTICLE_LEVEL);
			if (comboParticleLevel > 0) {
				switch (comboParticleLevel){
					case 1: {
						if (this.level.getGameTime() % 50 == 0) {
							this.level.addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(0.6D), this.getRandomY(), this.getRandomZ(0.6D), 0.0D, 0.0D, 0.0D);
						}
						break;
					}
					case 2: {
						if (this.level.getGameTime() % 25 == 0) {
							this.level.addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(0.6D), this.getRandomY(), this.getRandomZ(0.6D), 0.0D, 0.0D, 0.0D);
						}
						break;
					}
					case 3: {
						if (this.level.getGameTime() % 10 == 0) {
							this.level.addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(0.6D), this.getRandomY(), this.getRandomZ(0.6D), 0.0D, 0.0D, 0.0D);
						}
						break;
					}
					case 4: {
						if (this.level.getGameTime() % 10 == 0) {
							this.level.addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(0.6D), this.getRandomY(), this.getRandomZ(0.6D), 0.0D, 0.0D, 0.0D);
							this.level.addParticle(ParticleTypes.FIREWORK, this.getRandomX(0.6D), this.getRandomY(), this.getRandomZ(0.6D), 0.0D, 0.0D, 0.0D);
						}
						break;
					}
					case 5: {
						if (this.level.getGameTime() % 2 == 0) {
							this.level.addParticle(ParticleTypes.FIREWORK, this.getRandomX(0.6D), this.getRandomY(), this.getRandomZ(0.6D), 0.0D, 0.0D, 0.0D);
						}
					}
				}
			}
		} else {
			if (this.isRoosting()) {
				this.setDeltaMovement(Vector3d.ZERO);
				this.setPos(this.getX(), (double) MathHelper.floor(this.getY()) + 1.0D - (double) this.getBbHeight(), this.getZ());
				this.comboAttackResetTicks = 0;
			} else {
				this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, 0.6D, 1.0D));

				if (this.comboAttackResetTicks > 0) {
					this.comboAttackResetTicks--;
					if (this.comboAttackResetTicks == 0) {
						this.setComboLevel(0);
					}
				}

				if (this.teleportTicks > 0) this.teleportTicks--;
				if (this.teleportTicks == 0 && this.teleportTarget != null) {
					this.tryTeleportTo(this.teleportTarget);
					this.teleportTarget = null;
				}

				this.effectTicks--;
				if (this.effectTicks == 0){
					this.effectTicks = EFFECT_TICKS > 200 ? EFFECT_TICKS - 200 : EFFECT_TICKS;
					if (this.hasFireResistance) {
						this.addEffect(new EffectInstance(Effects.FIRE_RESISTANCE, this.effectTicks + 20, 0, false, false));
					}

					if (this.hasAdventurerAura || this.hasMinerAura){
						LivingEntity owner = this.getOwner();
						if (owner != null){
							if (this.hasAdventurerAura) owner.addEffect(new EffectInstance(Effects.LUCK, EFFECT_TICKS, 0, false, false));
							if (this.hasMinerAura) owner.addEffect(new EffectInstance(Effects.DIG_SPEED, EFFECT_TICKS, 0, false, false));
						}
					}
				}
			}
		}
	}

	public void die(DamageSource source) {
		if (!this.returnToPlayerInventory()) super.die(source);
	}

	protected boolean isMovementNoisy() {
		return false;
	}

	public boolean causeFallDamage(float fallDistance, float damageMultiplier) {
		return false;
	}

	protected void checkFallDamage(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition) {
	}

	public boolean isIgnoringBlockTriggers() {
		return true;
	}

	public boolean hurt(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source)) {
			return false;
		} else {
			if (!this.level.isClientSide) {
				if (this.isRoosting()) {
					this.setRoosting(false);
				}
			}

			if (!source.isBypassArmor() && source != DamageSource.LAVA && this.abilities.has(CompanionBatAbility.BLOCK_ATTACK)) {
				int roll = this.level.random.nextInt(100);
				if (roll < this.abilities.getValue(CompanionBatAbility.BLOCK_ATTACK)) {
					if (this.abilities.has(CompanionBatAbility.COUNTER_ATTACK) && source.getEntity() instanceof LivingEntity) {
						LivingEntity target = (LivingEntity) source.getEntity();
						LivingEntity owner = this.getOwner();
						if (target != owner && this.canAttackWithOwner(target, this.getOwner()) && this.isWithinDistanceToAttack(target)){
							this.level.playSound(null, this.blockPosition(), SoundEvents.ANVIL_LAND , SoundCategory.PLAYERS, 0.15F, this.getVoicePitch() + 2F);
							float targetHealth = target.getHealth();
							target.hurt(source, ((float)this.getAttributeValue(Attributes.ATTACK_DAMAGE) + amount) * this.abilities.getValue(CompanionBatAbility.COUNTER_ATTACK) / 4);
							this.onAttack(target, targetHealth, target.getHealth());
							return false;
						}
					}
					this.level.playSound(null, this.blockPosition(), SoundEvents.SHIELD_BLOCK, SoundCategory.PLAYERS, 0.2F, this.getVoicePitch());
					return false;
				}
			}

			return super.hurt(source, amount);
		}
	}

	public boolean canAttackWithOwner(LivingEntity target, LivingEntity owner) {
		return canAttackWithOwnerStatic(target, owner);
	}

	public static boolean canAttackWithOwnerStatic(LivingEntity target, LivingEntity owner) {
		if (target != owner && !(target instanceof CreeperEntity)) {
			if (target instanceof WolfEntity) {
				WolfEntity wolfEntity = (WolfEntity) target;
				return !wolfEntity.isTame() || wolfEntity.getOwner() != owner;
			} else if (target instanceof CompanionBatEntity) {
				CompanionBatEntity companionBatEntity = (CompanionBatEntity) target;
				return companionBatEntity.getOwner() != owner;
			} else if (target instanceof PlayerEntity && owner instanceof PlayerEntity && !((PlayerEntity) owner).canHarmPlayer((PlayerEntity) target)) {
				return false;
			} else if (target instanceof AbstractHorseEntity && ((AbstractHorseEntity) target).isTamed()) {
				return false;
			} else {
				return !(target instanceof TameableEntity) || !((TameableEntity) target).isTame();
			}
		} else {
			return false;
		}
	}

	@Override
	protected void customServerAiStep() {
		if (!this.level.isClientSide){
			if (this.isRoosting()) {
				if (this.getTarget() != null) {
					this.setRoosting(false);
				}
				if (this.hangingPosition == null || !this.level.getBlockState(this.hangingPosition).isRedstoneConductor(this.level, this.hangingPosition)) {
					this.setRoosting(false);
					if (!this.isSilent()) {
						this.level.levelEvent((PlayerEntity) null, 1025, this.blockPosition(), 0);
					}
				}
			}
			if (this.isRoosting() || this.hasNaturalRegeneration) {
				this.healTicks--;
				if (this.healTicks <= 0) {
					this.healTicks = HEAL_TICKS;
					if (this.isInjured()) {
						int val = Math.max(1, (int) (this.getMaxHealth() * 10 / 100));
						this.heal(val);
					}
				}
			}
		}
	}

	public CompanionBatEntity getBreedOffspring(ServerWorld serverWorld, AgeableEntity passiveEntity) {
		return null;
	}

	public boolean tryRangedAttack(Entity target) {
		AxisAlignedBB trajectoryBox = new AxisAlignedBB(this.getX(), this.getY(), this.getZ(), target.getX(), target.getY(), target.getZ()).inflate(2);
		if (!trajectoryBox.contains(this.getOwner().position())) {
			Vector3d vec3d = target.getDeltaMovement();
			double d = target.getX() + vec3d.x - this.getX();
			double e = target.getEyeY() - 1.100000023841858D - this.getY();
			double f = target.getZ() + vec3d.z - this.getZ();
			double g = MathHelper.sqrt(d * d + f * f);

			DynamiteEntity dynamite = new DynamiteEntity(this.level, this);
			dynamite.xRot += 20.0F;
			dynamite.setPower(this.abilities.getValue(CompanionBatAbility.DYNAMITE));
			dynamite.shoot(d, e + g * 0.2D, f, 0.75F, 8.0F);

			if (!this.isSilent()) {
				this.level.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.TNT_PRIMED, SoundCategory.PLAYERS, 1.0F, 1F + this.level.random.nextFloat() * 0.4F);
			}

			this.level.addFreshEntity(dynamite);
			this.setDeltaMovement(d * -0.4, 0, f * -0.4);
			return true;
		}
		return false;
	}

	public boolean doHurtTarget(Entity target) {
		float targetHealth = target instanceof LivingEntity ? ((LivingEntity) target).getHealth() : 0;
		boolean bl = target.hurt(DamageSource.mobAttack(this), this.getAttackDamage(target));
		if (bl) {
			if (this.isSneakAttacking) {
				this.level.playSound(null, this.blockPosition(), SoundEvents.PLAYER_ATTACK_CRIT , SoundCategory.PLAYERS, 0.5F, this.getVoicePitch() + 2.0F);
			}
			this.doEnchantDamageEffects(this, target);
			this.onAttack(target, targetHealth, (target instanceof LivingEntity ? ((LivingEntity) target).getHealth() : 0));
		}
		this.isSneakAttacking = false;
		return bl;
	}

	private float getAttackDamage(Entity target) {
		float attackDamage = (float) ((int) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
		if (this.abilities.has(CompanionBatAbility.SNEAK_ATTACK) && target instanceof LivingEntity && this.isBehind((LivingEntity) target)) {
			attackDamage += attackDamage * this.abilities.getValue(CompanionBatAbility.SNEAK_ATTACK) / 4;
			this.isSneakAttacking = true;
		}
		return attackDamage;
	}

	private boolean isBehind(LivingEntity target) {
		return this.guaranteedSneakAttack || target.getDirection().equals(this.getDirection());
	}

	private void onAttack(Entity target, float healthBefore, float healthAfter) {
		float damageDealt = healthBefore - healthAfter;
		if (damageDealt > 0) {
			this.gainExp(EXP_GAIN);
			if (this.abilities.has(CompanionBatAbility.LIFESTEAL)) {
				this.heal(damageDealt * this.abilities.getValue(CompanionBatAbility.LIFESTEAL) / 100);
			}
			if (this.abilities.has(CompanionBatAbility.BURN)) {
				target.setSecondsOnFire(this.abilities.getValue(CompanionBatAbility.BURN));
			}
			if (target instanceof LivingEntity){
				LivingEntity livingTarget = (LivingEntity)target;
				if (this.abilities.has(CompanionBatAbility.SLOWNESS)) {
					livingTarget.addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 60, this.abilities.getValue(CompanionBatAbility.SLOWNESS)));
				}
				if (this.abilities.has(CompanionBatAbility.WEAKNESS)) {
					livingTarget.addEffect(new EffectInstance(Effects.WEAKNESS, 60, this.abilities.getValue(CompanionBatAbility.WEAKNESS)));
				}
				if (this.abilities.has(CompanionBatAbility.WITHER)) {
					livingTarget.addEffect(new EffectInstance(Effects.WITHER, 60, this.abilities.getValue(CompanionBatAbility.WITHER)));
				}
			}
			if (this.abilities.has(CompanionBatAbility.COMBO_ATTACK)) {
				this.increaseComboLevel();
			}
			if (healthAfter <= 0 && this.hasTeleport){
				this.teleportTicks = 1;
			}
		}
	}

	private void increaseComboLevel() {
		this.comboAttackResetTicks = COMBO_ATTACK_RESET_TICKS;

		int comboAttackLevel = this.abilities.getValue(CompanionBatAbility.COMBO_ATTACK);
		this.setComboLevel(this.comboLevel+1);

		if (this.comboLevel > 5) {
			this.addEffect(new EffectInstance(Effects.MOVEMENT_SPEED, 50, (int)(this.comboLevel / 25), false, false));
		}
		if (comboAttackLevel >= 2 && this.comboLevel > 10) {
			this.addEffect(new EffectInstance(Effects.DAMAGE_BOOST, 50, (int)(this.comboLevel / 30), false, false));
		}
		if (comboAttackLevel >= 3 && this.comboLevel > 15) {
			this.addEffect(new EffectInstance(Effects.REGENERATION, 50, 0, false, false));
		}
		if (this.comboLevel > 45) {
			this.addEffect(new EffectInstance(Effects.DAMAGE_RESISTANCE, 100, 0, false, false));
			if (this.comboLevel == 50){
				this.heal(this.getMaxHealth());
				BlockPos blockPos = this.getTarget().blockPosition();
				int abilityLevel = this.abilities.get(CompanionBatAbility.COMBO_ATTACK);
				for (int i = 0; i < abilityLevel; i++){
					LightningBoltEntity lightningEntity = (LightningBoltEntity)EntityType.LIGHTNING_BOLT.create(this.level);
					lightningEntity.moveTo(Vector3d.atBottomCenterOf(blockPos));
					lightningEntity.setVisualOnly(true);
					this.level.addFreshEntity(lightningEntity);

					if (i == 0){
						List<Entity> list = this.level.getEntities(this, new AxisAlignedBB(blockPos.getX() - 3.0D, blockPos.getY() - 3.0D, blockPos.getZ() - 3.0D, blockPos.getX() + 3.0D, blockPos.getY() + 6.0D + 3.0D, blockPos.getZ() + 3.0D), entity -> entity.isAlive() && !(entity instanceof LightningBoltEntity) && entity != this && entity != this.getOwner());
						Iterator<Entity> iterator = list.iterator();

						while(iterator.hasNext()) {
							Entity entity = (Entity)iterator.next();
							if (!net.minecraftforge.event.ForgeEventFactory.onEntityStruckByLightning(entity, lightningEntity))
							entity.thunderHit((ServerWorld)this.level, lightningEntity);
							entity.hurt(DamageSource.LIGHTNING_BOLT, (6.0F + (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE)) * abilityLevel);
						}
					}
				}
				this.setComboLevel(0);
			}
		}
	}

	@Override
	public ActionResultType mobInteract(PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getItemInHand(hand);
		if (this.level.isClientSide) {
			return this.canEat(itemStack) ? ActionResultType.CONSUME : ActionResultType.PASS;
		} else {
			boolean res = this.healWithItem(itemStack);
			if (res) {
				if (!player.abilities.instabuild) {
					itemStack.shrink(1);
				}
				this.level.broadcastEntityEvent(this, (byte)8);
				return ActionResultType.SUCCESS;
			} else if (IS_FOOD_ITEM.test(itemStack) && player == this.getOwner()){
				ItemStack fluteStack = this.getFluteItemStack();
				if (fluteStack == null){
					player.addItem(this.toItemStack());
					this.remove();
					return ActionResultType.SUCCESS;
				}
			}
		}
		return ActionResultType.PASS;
	}

	private void setComboLevel(int level){
		this.comboLevel = level;
		this.entityData.set(COMBO_PARTICLE_LEVEL, level == 0 ? 0 : (byte)((this.comboLevel / 10) + 1));
	}

	public boolean healWithItem(ItemStack stack) {
		if (!this.canEat(stack)) return false;
		if (stack.getItem() == CompanionBats.EXPERIENCE_PIE.get()){
			this.gainExp(EXPERIENCE_PIE_GAIN);
		}
		float amount = getItemHealAmount(stack);
		if (amount > 0) {
			this.heal(amount);
			return true;
		}
		return false;
	}

	public static float getItemHealAmount(ItemStack stack) {
		if (stack.getItem() == Items.PUMPKIN_PIE || stack.getItem() == CompanionBats.EXPERIENCE_PIE.get()) {
			return 6.0F;
		}
		return 0;
	}

	public static float getMaxLevelHealth() {
		return BASE_HEALTH + CompanionBatLevels.getLevelHealth(CompanionBatLevels.LEVELS.length - 1);
	}

	public static float getLevelHealth(int level) {
		return BASE_HEALTH + CompanionBatLevels.getLevelHealth(level);
	}

	public static float getLevelAttack(int level) {
		return BASE_ATTACK + CompanionBatLevels.getLevelAttack(level);
	}

	public static float getLevelSpeed(int level) {
		return BASE_SPEED + CompanionBatLevels.getLevelSpeed(level);
	}

	protected PathNavigator createNavigation(World world) {
		FlyingPathNavigator birdNavigation = new FlyingPathNavigator(this, world);
		birdNavigation.setCanOpenDoors(false);
		birdNavigation.setCanFloat(true);
		birdNavigation.setCanPassDoors(true);
		return birdNavigation;
	}

	protected float getStandingEyeHeight(Pose pose, EntitySize dimensions) {
		return dimensions.height / 2.0F;
	}

	@OnlyIn(Dist.CLIENT)
	public void handleEntityEvent(byte status) {
		if (status == 8) {
			for (int i = 0; i < 3; i++) {
				double d = this.random.nextGaussian() * 0.01D;
				double e = this.random.nextGaussian() * 0.01D;
				double f = this.random.nextGaussian() * 0.01D;
				this.level.addParticle(ParticleTypes.HAPPY_VILLAGER, this.getRandomX(0.5D), this.getRandomY(), this.getRandomZ(1.0D), d, e, f);
			}
		} else {
			super.handleEntityEvent(status);
		}
	}

	public boolean returnToPlayerInventory() {
		if (this.level.isClientSide) return false;
		ServerPlayerEntity player = (ServerPlayerEntity) this.getOwner();
		if (player != null) {
			PlayerInventory inventory = player.inventory;
			ImmutableList<NonNullList<ItemStack>> mainAndOffhand = ImmutableList.of(inventory.items, inventory.offhand);
			Iterator<NonNullList<ItemStack>> iterator = mainAndOffhand.iterator();
			while (iterator.hasNext()) {
				NonNullList<ItemStack> defaultedList = (NonNullList<ItemStack>) iterator.next();
				for (int i = 0; i < defaultedList.size(); ++i) {
					if (defaultedList.get(i).getItem() == CompanionBats.BAT_FLUTE_ITEM.get() && defaultedList.get(i).getTag().getUUID("EntityUUID").equals(this.getUUID())) {
						defaultedList.set(i, this.toItemStack());
						this.remove();
						level.playSound(null, player.blockPosition(), SoundEvents.SLIME_ATTACK, SoundCategory.AMBIENT, 1F, 1F);
						return true;
					}
				}
			}
		}
		return false;
	}

	private ItemStack getFluteItemStack(){
		ServerPlayerEntity player = (ServerPlayerEntity) this.getOwner();
		if (player != null) {
			PlayerInventory inventory = player.inventory;
			ImmutableList<NonNullList<ItemStack>> mainAndOffhand = ImmutableList.of(inventory.items, inventory.offhand);
			Iterator<NonNullList<ItemStack>> iterator = mainAndOffhand.iterator();
			while (iterator.hasNext()) {
				NonNullList<ItemStack> defaultedList = (NonNullList<ItemStack>) iterator.next();
				for (int i = 0; i < defaultedList.size(); ++i) {
					if (defaultedList.get(i).getItem() == CompanionBats.BAT_FLUTE_ITEM.get() && defaultedList.get(i).getTag().getUUID("EntityUUID").equals(this.getUUID())) {
						return defaultedList.get(i);
					}
				}
			}
		}
		return null;
	}

	private boolean isWithinDistanceToAttack(LivingEntity entity) {
		return this.distanceToSqr(entity) < (double)(this.getBbWidth() * 2.0F * this.getBbWidth() * 2.0F + entity.getBbWidth());
	}

	public int getExp() {
		return this.exp;
	}

	private void setExp(int exp) {
		if (exp > CompanionBatLevels.LEVELS[CompanionBatLevels.LEVELS.length - 1].totalExpNeeded) {
			exp = CompanionBatLevels.LEVELS[CompanionBatLevels.LEVELS.length - 1].totalExpNeeded;
		}
		this.exp = exp;
		this.tryLevelUp();
	}

	private void addExp(int expToAdd) {
		this.setExp(this.exp + expToAdd);
	}

	private void gainExp(int expToAdd) {
		if (this.exp != CompanionBatLevels.LEVELS[CompanionBatLevels.LEVELS.length - 1].totalExpNeeded) {
			this.addExp(expToAdd);
		}
		if (this.currentClass != null) {
			CompanionBatClassLevel[] classLevels = CompanionBatLevels.CLASS_LEVELS.get(this.currentClass);
			if (this.classExp != classLevels[classLevels.length - 1].totalExpNeeded) {
				this.addClassExp(expToAdd);
			}
		}
	}

	private int getClassExp() {
		return this.classExp;
	}

	private void setClassExp(int classExp) {
		CompanionBatClassLevel[] classLevels = CompanionBatLevels.CLASS_LEVELS.get(this.currentClass);
		if (classExp > classLevels[classLevels.length - 1].totalExpNeeded) {
			classExp = classLevels[classLevels.length - 1].totalExpNeeded;
		}
		this.classExp = classExp;
		this.tryClassLevelUp();
	}

	private void addClassExp(int expToAdd) {
		this.setClassExp(this.classExp + expToAdd);
	}

	private void setAccessoryAbility(){
		ItemStack headStack = this.getItemBySlot(EquipmentSlotType.HEAD);
		if (headStack.getItem() instanceof CompanionBatAccessoryItem) {
			this.abilities.addFromAccessory((CompanionBatAccessoryItem) headStack.getItem());
		}
	}

	private void setArmorAndClass() {
		ItemStack chestStack = this.getItemBySlot(EquipmentSlotType.CHEST);
		if (chestStack.getItem() instanceof CompanionBatArmorItem) {
			CompanionBatArmorItem armor = (CompanionBatArmorItem) chestStack.getItem();
			this.currentClass = armor.getBatClass();
			this.currentClass = armor.getBatClass();
			int armorToAdd = armor.getProtectionAmount();
			if (armorToAdd != 0) {
				this.getAttribute(Attributes.ARMOR).addTransientModifier(new AttributeModifier(BAT_EQUIPMENT_ARMOR_BONUS_ID, "Equipment armor bonus", (double) armorToAdd, AttributeModifier.Operation.ADDITION));
			}
		}
	}

	private void setClassesExp(EntityData entityData) {
		for (CompanionBatClass cls : CompanionBatClass.values()) {
			this.classesExp.put(cls, entityData.getClassExp(cls));
		}
		if (this.currentClass != null) {
			this.classExp = this.classesExp.get(this.currentClass);
			this.classLevel = CompanionBatLevels.getClassLevelByExp(this.currentClass, (int) this.classExp);
		}
	}

	private void setCurrentClassLevelAbilities() {
		if (this.currentClass == null) {
			return;
		}
		CompanionBatClassLevel[] classLevels = CompanionBatLevels.CLASS_LEVELS.get(this.currentClass);
		this.abilities.addFromClassLevel(classLevels[this.classLevel]);
	}

	private void setAbilitiesEffects(boolean firstTime) {
		if (firstTime){
			this.goalSelector.addGoal(3, new CompanionBatPickUpItemGoal(this, 1.0D, 16.0F));
			this.goalSelector.addGoal(4, new CompanionBatFollowOwnerGoal(this, 1.0D, 2.5F, 24.0F));
			this.goalSelector.addGoal(5, new CompanionBatTransferItemsToOwnerGoal(this, 2.5F));
			this.goalSelector.addGoal(6, new CompanionBatRoostGoal(this, 0.75F, 4.0F, ROOST_START_TICKS));
			if (!this.abilities.has(CompanionBatAbility.CANNOT_ATTACK)){
				if (this.abilities.has(CompanionBatAbility.DYNAMITE)){
					this.goalSelector.addGoal(1, new CompanionBatRangedAttackGoal(this, 5.0F, 9.0F, RANGED_ATTACK_TICKS));
				}
				this.goalSelector.addGoal(2, new CompanionBatMeleeAttackGoal(this, 1.0D, true));
				this.targetSelector.addGoal(1, new CompanionBatTrackOwnerAttackerGoal(this));
				this.targetSelector.addGoal(2, new CompanionBatAttackWithOwnerGoal(this));
				this.targetSelector.addGoal(3, (new HurtByTargetGoal(this, new Class[0])).setAlertOthers());
				if (this.abilities.has(CompanionBatAbility.ATTACK_EVERYONE)){
					this.targetSelector.addGoal(4, new CompanionBatTargetSelectorGoal(this, CompanionBatAbility.ATTACK_EVERYONE));
				} else if (this.abilities.has(CompanionBatAbility.ATTACK_HOSTILES)){
					this.targetSelector.addGoal(4, new CompanionBatTargetSelectorGoal(this, CompanionBatAbility.ATTACK_HOSTILES));
				} else if (this.abilities.has(CompanionBatAbility.ATTACK_PASSIVE)){
					this.targetSelector.addGoal(4, new CompanionBatTargetSelectorGoal(this, CompanionBatAbility.ATTACK_PASSIVE));
				}
			}
		}
		if (this.abilities.has(CompanionBatAbility.EMERGENCY_POTION) || this.abilities.has(CompanionBatAbility.EFFECT_POTION)) {
			if (!this.hasPotionGoal){
				this.goalSelector.addGoal(7, new CompanionBatThrowPotionGoal(this, 3.0F, EMERGENCY_POTION_TICKS, EFFECT_POTION_TICKS));
				this.hasPotionGoal = true;
			}
		}
		if (this.abilities.has(CompanionBatAbility.INCREASED_ARMOR)) {
			ModifiableAttributeInstance attr = this.getAttribute(Attributes.ARMOR);
			if (!firstTime) attr.removeModifier(BAT_ARMOR_BONUS_ID);
			attr.addTransientModifier(new AttributeModifier(BAT_ARMOR_BONUS_ID, "Ability armor bonus", this.abilities.getValue(CompanionBatAbility.INCREASED_ARMOR), AttributeModifier.Operation.ADDITION));
		}
		if (this.abilities.has(CompanionBatAbility.INCREASED_ATTACK)) {
			ModifiableAttributeInstance attr = this.getAttribute(Attributes.ATTACK_DAMAGE);
			if (!firstTime) attr.removeModifier(BAT_ATTACK_BONUS_ID);
			attr.addTransientModifier(new AttributeModifier(BAT_ATTACK_BONUS_ID, "Ability attack bonus", (double) (attr.getBaseValue() * this.abilities.getValue(CompanionBatAbility.INCREASED_ATTACK) / 100), AttributeModifier.Operation.ADDITION));
		}
		if (this.abilities.has(CompanionBatAbility.INCREASED_SPEED)) {
			ModifiableAttributeInstance attr = this.getAttribute(Attributes.MOVEMENT_SPEED);
			if (!firstTime) attr.removeModifier(BAT_SPEED_BONUS_ID);
			attr.addTransientModifier(new AttributeModifier(BAT_SPEED_BONUS_ID, "Ability speed bonus", (double) (attr.getBaseValue() * this.abilities.getValue(CompanionBatAbility.INCREASED_SPEED) / 100), AttributeModifier.Operation.ADDITION));
		}
		if (this.abilities.has(CompanionBatAbility.FIRE_RESISTANCE)) {
			this.hasFireResistance = true;
		}
		if (this.abilities.has(CompanionBatAbility.TELEPORT)) {
			this.hasTeleport = true;
		}
		if (this.abilities.has(CompanionBatAbility.ADVENTURER_AURA)) {
			this.hasAdventurerAura = true;
		}
		if (this.abilities.has(CompanionBatAbility.DESTROYER_AURA)) {
			this.hasMinerAura = true;
		}
		if (this.abilities.has(CompanionBatAbility.NATURAL_REGENERATION)) {
			this.hasNaturalRegeneration = true;
		}
		if (this.abilities.has(CompanionBatAbility.LOOTING)) {
			ItemStack stack = new ItemStack(Items.STICK);
			stack.enchant(Enchantments.MOB_LOOTING, this.abilities.getValue(CompanionBatAbility.LOOTING));
			this.setItemSlot(EquipmentSlotType.MAINHAND, stack);
			this.setDropChance(EquipmentSlotType.MAINHAND, 0.0F);
		}
	}

	public void scheduleTeleport(Entity target){
		if (!this.hasTeleport) return;
		this.teleportTarget = target;
	}

	private boolean tryTeleportTo(Entity target) {
		return target != null && target.isAlive() && this.distanceToSqr(target) <= this.abilities.getValue(CompanionBatAbility.TELEPORT) && this.teleportTo(target);
	}

	private boolean teleportTo(Entity entity) {
		Direction looking = entity.getDirection().getOpposite();
		boolean success = this.randomTeleport(entity.getX() + looking.getStepX(), entity.getEyeY(), entity.getZ() + looking.getStepZ(), true);
		if (success) {
			if (!this.isSilent()) this.level.playSound((PlayerEntity) null, this.xo, this.yo, this.zo, SoundEvents.ENDERMAN_TELEPORT, this.getSoundSource(), 0.25F, this.getVoicePitch() + 1.0F);
			if (entity instanceof LivingEntity && this.canAttackWithOwner((LivingEntity) entity, this.getOwner())){
				this.guaranteedSneakAttack = true;
				this.doHurtTarget(entity);
				this.guaranteedSneakAttack = false;
			}
			this.teleportTicks = TELEPORT_TICKS;
		}
		return success;
	}

	private void tryLevelUp() {
		if (CompanionBatLevels.LEVELS.length > this.currentLevel + 1 && CompanionBatLevels.LEVELS[this.currentLevel + 1].totalExpNeeded <= this.exp) {
			this.currentLevel++;
			this.notifyLevelUp(this.currentLevel);
			this.setLevelAttributes(this.currentLevel);
			this.heal(this.getMaxHealth());
		}
	}

	private void tryClassLevelUp() {
		CompanionBatClassLevel[] classLevels = CompanionBatLevels.CLASS_LEVELS.get(this.currentClass);
		if (classLevels.length > this.classLevel + 1 && classLevels[this.classLevel + 1].totalExpNeeded <= this.classExp) {
			this.classLevel++;
			this.notifyClassLevelUp(this.classLevel, classLevels);
			this.setCurrentClassLevelAbilities();
			this.setAbilitiesEffects(false);
		}
	}

	private void setCurrentLevel(int exp) {
		this.exp = exp;
		this.currentLevel = CompanionBatLevels.getLevelByExp((int) this.exp);
	}

	protected void setLevelAttributes(int level) {
		this.getAttributes().getInstance(Attributes.MAX_HEALTH).setBaseValue(getLevelHealth(level));
		this.getAttributes().getInstance(Attributes.ATTACK_DAMAGE).setBaseValue(getLevelAttack(level));
		this.getAttributes().getInstance(Attributes.MOVEMENT_SPEED).setBaseValue(getLevelSpeed(level));
	}

	protected void notifyLevelUp(int level) {
		if (level > 0) {
			IFormattableTextComponent message = new TranslationTextComponent("entity.companion_bats.bat.level_up", this.hasCustomName() ? this.getCustomName() : new TranslationTextComponent("entity.companion_bats.bat.your_bat"), level + 1).append("\n");
			if (CompanionBatLevels.LEVELS[level].healthBonus > CompanionBatLevels.LEVELS[level - 1].healthBonus) {
				message.append(new StringTextComponent("+").withStyle(ChatFormatting.GOLD)).append(" ");
				message.append(new TranslationTextComponent("entity.companion_bats.bat.level_up_health", (int) (CompanionBatLevels.LEVELS[level].healthBonus - CompanionBatLevels.LEVELS[level - 1].healthBonus))).append(" ");
			}
			if (CompanionBatLevels.LEVELS[level].attackBonus > CompanionBatLevels.LEVELS[level - 1].attackBonus) {
				message.append(new StringTextComponent("+").withStyle(ChatFormatting.GOLD)).append(" ");
				message.append(new TranslationTextComponent("entity.companion_bats.bat.level_up_attack", (int) (CompanionBatLevels.LEVELS[level].attackBonus - CompanionBatLevels.LEVELS[level - 1].attackBonus))).append(" ");
			}
			if (CompanionBatLevels.LEVELS[level].speedBonus > CompanionBatLevels.LEVELS[level - 1].speedBonus) {
				message.append(new StringTextComponent("+").withStyle(ChatFormatting.GOLD)).append(" ");
				message.append(new TranslationTextComponent("entity.companion_bats.bat.level_up_speed", Math.round(100 - ((BASE_SPEED + CompanionBatLevels.LEVELS[level - 1].speedBonus) / (BASE_SPEED + CompanionBatLevels.LEVELS[level].speedBonus) * 100)))).append(" ");
			}
			((PlayerEntity) this.getOwner()).displayClientMessage(message, false);
		}
	}

	protected void notifyClassLevelUp(int classLevel, CompanionBatClassLevel[] classLevels) {
		IFormattableTextComponent message = new TranslationTextComponent("entity.companion_bats.bat.class_level_up", this.hasCustomName() ? this.getCustomName() : new TranslationTextComponent("entity.companion_bats.bat.your_bat"), this.currentClass.toString(), classLevel + 1);
		if (classLevels[classLevel].ability != null){
			message.append("\n");
			String obtainedOrLevelUp = "ability_obtained";
			if (classLevels[classLevel].permanent){
				message.append(new TranslationTextComponent("entity.companion_bats.bat.permanent_ability").withStyle(ChatFormatting.LIGHT_PURPLE)).append(" ");
			} else {
				message.append(new TranslationTextComponent("entity.companion_bats.bat.ability").withStyle(ChatFormatting.GREEN)).append(" ");
				if (this.abilities.has(classLevels[classLevel].ability)){
					obtainedOrLevelUp = "ability_level_up";
				}
			}
			message.append(new TranslationTextComponent("entity.companion_bats.bat."+obtainedOrLevelUp, classLevels[classLevel].ability.toTranslatedText()));
		}
		((PlayerEntity) this.getOwner()).displayClientMessage(message, false);
	}

	private void writeExpToTag(EntityData entityData) {
		entityData.putExp(this.getExp());
		for (Map.Entry<CompanionBatClass, Integer> entry : this.classesExp.entrySet()) {
			if (entry.getValue() > 0) {
				entityData.putClassExp(entry.getKey(), entry.getValue());
			}
		}
		if (this.currentClass != null) {
			entityData.putClassExp(this.currentClass, this.getClassExp());
		}
	}

	private void writePotionTicks(EntityData entityData) {
		entityData.putEmergencyPotionTicks(this.emergencyPotionTicks);
		entityData.putEffectPotionTicks(this.effectPotionTicks);
	}

	private void setPotionTicks(EntityData entityData) {
		if (entityData.hasPotionTicks()){
			this.emergencyPotionTicks = Math.max(60, entityData.getEmergencyPotionTicks());
			this.effectPotionTicks = Math.max(60, entityData.getEffectPotionTicks());
		} else {
			this.emergencyPotionTicks = EMERGENCY_POTION_TICKS;
			this.effectPotionTicks = EFFECT_POTION_TICKS;
		}
	}

	public ItemStack toItemStack() {
		ItemStack batItemStack = new ItemStack(CompanionBats.BAT_ITEM.get());
		if (this.hasCustomName()) {
			batItemStack.setHoverName(this.getCustomName());
		}
		// Set companion bat item durability relative to the bat health
		float percentage = 1 - (this.getHealth() / this.getMaxHealth());
		batItemStack.setDamageValue(Math.round(percentage * batItemStack.getMaxDamage()));

		EntityData.fromCompanionBatEntity(this).toStack(batItemStack);
		return batItemStack;
	}

	public static CompanionBatEntity spawnFromItemStack(ServerWorld world, ItemStack itemStack, PlayerEntity player){
		Vector3d pos = player.position();
		EntityData entityData = new EntityData(itemStack);
		entityData.putOwner(player);
		return (CompanionBatEntity)CompanionBats.COMPANION_BAT.get().spawn(world, itemStack, player, new BlockPos(pos.x, Math.ceil(pos.y), pos.z), SpawnReason.SPAWN_EGG, false, false);
	}

	public ItemStack getAccessory() {
		return this.getItemBySlot(EquipmentSlotType.HEAD);
	}

	public ItemStack getArmorType() {
		return this.getItemBySlot(EquipmentSlotType.CHEST);
	}

	public ItemStack getBundle() {
		return this.getItemBySlot(EquipmentSlotType.FEET);
	}

	public boolean canEat(ItemStack stack){
		if (stack.getItem() == CompanionBats.EXPERIENCE_PIE.get()){
			if (this.currentClass != null) {
				CompanionBatClassLevel[] classLevels = CompanionBatLevels.CLASS_LEVELS.get(this.currentClass);
				if (this.classExp < classLevels[classLevels.length - 1].totalExpNeeded) return true;
			}
			return this.isInjured() || (this.exp < CompanionBatLevels.LEVELS[CompanionBatLevels.LEVELS.length - 1].totalExpNeeded);
		} else {
			return this.isInjured() && IS_FOOD_ITEM.test(stack);
		}
	}

	public Byte getGuardMode(){
		return 1;
	}

	public void setTarget(@Nullable LivingEntity target) {
		this.scheduleTeleport(target);
		super.setTarget(target);
	}

	static {
		BAT_FLAGS = EntityDataManager.defineId(CompanionBatEntity.class, DataSerializers.BYTE);
		COMBO_PARTICLE_LEVEL = EntityDataManager.defineId(CompanionBatEntity.class, DataSerializers.BYTE);
		IS_FOOD_ITEM = (itemStack) -> itemStack.getItem() == Items.PUMPKIN_PIE || itemStack.getItem() == CompanionBats.EXPERIENCE_PIE.get();
		IS_FOOD_ITEM_ENTITY = (itemEntity) -> IS_FOOD_ITEM.test(itemEntity.getItem());
	}
}