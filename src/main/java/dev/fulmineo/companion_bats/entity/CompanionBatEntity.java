package dev.fulmineo.companion_bats.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;

import org.jetbrains.annotations.Nullable;

import dev.fulmineo.companion_bats.CompanionBats;
import dev.fulmineo.companion_bats.data.ClientDataManager;
import dev.fulmineo.companion_bats.data.CompanionBatAbilities;
import dev.fulmineo.companion_bats.data.CompanionBatAbilityType;
import dev.fulmineo.companion_bats.data.CompanionBatClass;
import dev.fulmineo.companion_bats.data.CompanionBatClassLevel;
import dev.fulmineo.companion_bats.data.CompanionBatCombatLevel;
import dev.fulmineo.companion_bats.data.EntityData;
import dev.fulmineo.companion_bats.data.ServerDataManager;
import dev.fulmineo.companion_bats.entity.ai.control.CompanionBatMoveControl;
import dev.fulmineo.companion_bats.entity.ai.goal.CompanionBatAttackWithOwnerGoal;
import dev.fulmineo.companion_bats.entity.ai.goal.CompanionBatFollowOwnerGoal;
import dev.fulmineo.companion_bats.entity.ai.goal.CompanionBatPickUpItemGoal;
import dev.fulmineo.companion_bats.entity.ai.goal.CompanionBatRangedAttackGoal;
import dev.fulmineo.companion_bats.entity.ai.goal.CompanionBatRoostGoal;
import dev.fulmineo.companion_bats.entity.ai.goal.CompanionBatTargetSelectorGoal;
import dev.fulmineo.companion_bats.entity.ai.goal.CompanionBatThrowPotionGoal;
import dev.fulmineo.companion_bats.entity.ai.goal.CompanionBatTrackOwnerAttackerGoal;
import dev.fulmineo.companion_bats.entity.ai.goal.CompanionBatTransferItemsToOwnerGoal;
import dev.fulmineo.companion_bats.item.CompanionBatAccessoryItem;
import dev.fulmineo.companion_bats.item.CompanionBatArmorItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Pair;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.EntityView;
import net.minecraft.world.World;

public class CompanionBatEntity extends TameableEntity {
	private static final TrackedData<Byte> BAT_FLAGS;
	private static final TrackedData<Byte> COMBO_PARTICLE_LEVEL;
	public CompanionBatAbilities abilities = new CompanionBatAbilities();
	private Map<String, Integer> classesExp = new HashMap<>();
	private int regenTicks = CompanionBats.CONFIG.regenTicks;

	// Constants

	private static final UUID BAT_EQUIPMENT_ARMOR_BONUS_ID = UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F296");
	private static final UUID BAT_ARMOR_BONUS_ID = UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F297");
	private static final UUID BAT_ATTACK_BONUS_ID = UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F298");
	private static final UUID BAT_SPEED_BONUS_ID = UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F299");

	public static final Predicate<ItemStack> IS_TAMING_ITEM;
	public static final Predicate<ItemStack> IS_FOOD_ITEM;
	public static final Predicate<ItemEntity> IS_REVIVE_ITEM_ENTITY;
	public BlockPos hangingPosition;
	public BlockPos fleeingPosition;
	public BlockPos diggingPosition;
	public int emergencyPotionTicks;
	public int effectPotionTicks;
	public int roostTicks;
	public int rangedAttackTicks = CompanionBats.CONFIG.dynamiteTicks;

	private String currentClass;
	private int exp = 0;
	private int classExp = 0;
	private int level = -1;
	private int classLevel = -1;
	private boolean hasTeleport;
	private boolean hasPotionGoal;
	private boolean hasRangedAttackGoal;
	private boolean hasNaturalRegeneration;
	private boolean hasFlameEater;
	private boolean hasAttractFlames;
	private int comboAttackResetTicks = CompanionBats.CONFIG.comboAttackResetTicks;
	private int comboLevel = 0;
	private int teleportTicks = CompanionBats.CONFIG.teleportTicks;
	private int effectTicks = 1;
	private Byte guardMode = 0;
	private boolean isSneakAttacking;
	private boolean guaranteedSneakAttack;
	private Entity teleportTarget;
	private NbtCompound itemNbt;
	private byte itemType;

	public CompanionBatEntity(EntityType<? extends TameableEntity> entityType, World world) {
		super(entityType, world);
		this.moveControl = new CompanionBatMoveControl(this, 10);
		this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, -1.0F);
		this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, -1.0F);
		this.setRoosting(false);
		this.setSitting(false);
	}

	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(BAT_FLAGS, (byte) 0);
		this.dataTracker.startTracking(COMBO_PARTICLE_LEVEL, (byte) 0);
	}

	public void writeCustomDataToNbt(NbtCompound tag) {
		super.writeCustomDataToNbt(tag);
		EntityData entityData = new EntityData(tag);
		entityData.putExp(this.getExp());
		entityData.putGuardMode(this.getGuardMode());
		this.writeExpToTag(entityData);
		this.writePotionTicks(entityData);
		this.writeItemData(tag);
	}

	public void readCustomDataFromNbt(NbtCompound tag) {
		EntityData entityData = new EntityData(tag);
		entityData.migrate();
		this.setLevel(entityData.getExp());
		this.setLevelAttributes(this.level);
		super.readCustomDataFromNbt(tag);
		this.setAccessoryAbility();
		this.setArmorAndClass();
		this.setClassesExp(entityData);
		this.abilities.setFromNbt(ServerDataManager.classes, entityData);
		this.setAbilitiesEffects(true);
		this.setPotionTicks(entityData);
		this.setGuardMode(entityData.getGuardMode());
		this.setItemData(tag);
	}

	protected float getSoundVolume() {
		return 0.1F;
	}

	public float getSoundPitch() {
		return super.getSoundPitch() * 0.95F;
	}

	@Nullable
	public SoundEvent getAmbientSound() {
		return this.isRoosting() && this.random.nextInt(4) != 0 ? null : SoundEvents.ENTITY_BAT_AMBIENT;
	}

	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_BAT_HURT;
	}

	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_BAT_DEATH;
	}

	public boolean isPushable() {
		return false;
	}

	protected void pushAway(Entity entity) {
	}

	protected void tickCramming() {
	}

	public static DefaultAttributeContainer.Builder createMobAttributes() {
		return MobEntity.createMobAttributes()
			.add(EntityAttributes.GENERIC_MAX_HEALTH, CompanionBats.CONFIG.baseHealth)
			.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, CompanionBats.CONFIG.baseAttack)
			.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, CompanionBats.CONFIG.baseSpeed)
			.add(EntityAttributes.GENERIC_FOLLOW_RANGE, 24.0D);
	}

	/**
	 * Returns whether this bat is hanging upside-down under a block.
	 */
	public boolean isRoosting() {
		return ((Byte) this.dataTracker.get(BAT_FLAGS) & 1) != 0;
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
		byte b = (Byte) this.dataTracker.get(BAT_FLAGS);
		if (roosting) {
			this.dataTracker.set(BAT_FLAGS, (byte) (b | 1));
		} else {
			this.dataTracker.set(BAT_FLAGS, (byte) (b & -2));
			this.hangingPosition = null;
		}
	}

	public void startRoosting(){
		this.roostTicks = 1;
	}

	public void tick() {
		super.tick();
		if (this.world.isClient){
			Byte comboParticleLevel = this.dataTracker.get(COMBO_PARTICLE_LEVEL);
			if (comboParticleLevel > 0) {
				switch (comboParticleLevel){
					case 1: {
						if (this.world.getTime() % 50 == 0) {
							this.world.addParticle(ParticleTypes.GLOW, this.getParticleX(0.6D), this.getRandomBodyY(), this.getParticleZ(0.6D), 0.0D, 0.0D, 0.0D);
						}
						break;
					}
					case 2: {
						if (this.world.getTime() % 25 == 0) {
							this.world.addParticle(ParticleTypes.GLOW, this.getParticleX(0.6D), this.getRandomBodyY(), this.getParticleZ(0.6D), 0.0D, 0.0D, 0.0D);
						}
						break;
					}
					case 3: {
						if (this.world.getTime() % 10 == 0) {
							this.world.addParticle(ParticleTypes.GLOW, this.getParticleX(0.6D), this.getRandomBodyY(), this.getParticleZ(0.6D), 0.0D, 0.0D, 0.0D);
						}
						break;
					}
					case 4: {
						if (this.world.getTime() % 10 == 0) {
							this.world.addParticle(ParticleTypes.GLOW, this.getParticleX(0.6D), this.getRandomBodyY(), this.getParticleZ(0.6D), 0.0D, 0.0D, 0.0D);
							this.world.addParticle(ParticleTypes.ELECTRIC_SPARK, this.getParticleX(0.6D), this.getRandomBodyY(), this.getParticleZ(0.6D), 0.0D, 0.0D, 0.0D);
						}
						break;
					}
					case 5: {
						this.world.addParticle(ParticleTypes.ELECTRIC_SPARK, this.getParticleX(0.6D), this.getRandomBodyY(), this.getParticleZ(0.6D), 0.0D, 0.0D, 0.0D);
					}
				}
			}
		} else {
			if (this.isRoosting()) {
				this.setVelocity(Vec3d.ZERO);
				this.setPos(this.getX(), (double) MathHelper.floor(this.getY()) + 1.0D - (double) this.getHeight(), this.getZ());
				this.comboAttackResetTicks = 0;
			} else {
				this.setVelocity(this.getVelocity().multiply(1.0D, 0.6D, 1.0D));

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
					this.effectTicks = CompanionBats.CONFIG.statusEffectTicks> 200 ? CompanionBats.CONFIG.statusEffectTicks- 200 : CompanionBats.CONFIG.statusEffectTicks;
					this.abilities.applyActiveEffects(this, this.effectTicks + 20);
					this.abilities.applyAuraEffects(this, CompanionBats.CONFIG.statusEffectTicks);
				}
			}

			if (this.hasAttractFlames) {
				int fireTicks = this.getFireTicks();
				LivingEntity owner = this.getOwner();
				if (owner != null && owner.isOnFire()) {
					fireTicks += owner.getFireTicks();
					owner.setFireTicks(0);
				}
				if (this.world.getTime() % 10 == 0) {
					int offset = CompanionBats.CONFIG.attractFlamesBoxSize / 2;
					BlockPos pos = this.getBlockPos().add(Direction.UP.getVector().multiply(offset)).add(Direction.NORTH.getVector().multiply(offset)).add(Direction.EAST.getVector().multiply(offset));
					for (int y = 0; y < CompanionBats.CONFIG.attractFlamesBoxSize; y++) {
						for (int z = 0; z < CompanionBats.CONFIG.attractFlamesBoxSize; z++) {
							for (int x = 0; x < CompanionBats.CONFIG.attractFlamesBoxSize; x++) {
								BlockState state = this.world.getBlockState(pos);
								if (state.isOf(Blocks.FIRE) || state.isOf(Blocks.SOUL_FIRE)) {
									this.world.removeBlock(pos, false);
									fireTicks += 60;
								}
								pos = pos.west();
							}
							pos = pos.south().east(CompanionBats.CONFIG.attractFlamesBoxSize);
						}
						pos = pos.down().north(CompanionBats.CONFIG.attractFlamesBoxSize);
					}
				}
				this.setFireTicks(fireTicks);
			}

			if (this.hasFlameEater && this.isOnFire()) {
				int level = this.abilities.get(CompanionBatAbilityType.FLAME_EATER);
				int currentDuration = 0;
				StatusEffectInstance currentStatus = this.getStatusEffect(StatusEffects.STRENGTH);
				if (currentStatus != null && currentStatus.getAmplifier() == level -1) currentDuration = currentStatus.getDuration();
				this.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, currentDuration + this.getFireTicks(), level - 1, false, true));
				this.setFireTicks(0);
			}
		}
	}

	public void onDeath(DamageSource source) {
		if (!this.returnToPlayerInventory()) {
			ItemStack stack = this.toItemStack();
			this.dropStack(stack);
			this.discard();
		}
	}

	protected boolean canClimb() {
		return false;
	}

	public boolean handleFallDamage(float fallDistance, float damageMultiplier) {
		return false;
	}

	protected void fall(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition) {
	}

	public boolean canAvoidTraps() {
		return true;
	}

	public boolean isInvulnerableTo(DamageSource damageSource) {
		return super.isInvulnerableTo(damageSource) || ((damageSource == this.getDamageSources().magic() || damageSource == this.getDamageSources().wither()) && this.abilities.hasAbility(CompanionBatAbilityType.MAGIC_PROTECTION));
	}

	public boolean damage(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source)) {
			return false;
		} else {
			if (!this.world.isClient) {
				if (this.isRoosting()) {
					this.setRoosting(false);
				}
			}

			if (!source.isIndirect() && source != this.getDamageSources().lava() && this.abilities.hasAbility(CompanionBatAbilityType.BLOCK_ATTACK)) {
				int roll = this.world.random.nextInt(100);
				if (roll < this.abilities.getValue(CompanionBatAbilityType.BLOCK_ATTACK)) {
					if (this.abilities.hasAbility(CompanionBatAbilityType.COUNTER_ATTACK) && source.getAttacker() instanceof LivingEntity) {
						LivingEntity target = (LivingEntity) source.getAttacker();
						LivingEntity owner = this.getOwner();
						if (target != owner && this.canAttackWithOwner(target, this.getOwner()) && this.isWithinDistanceToAttack(target)){
							this.world.playSound(null, this.getBlockPos(), SoundEvents.BLOCK_ANVIL_LAND , SoundCategory.PLAYERS, 0.15F, this.getSoundPitch() + 2F);
							float targetHealth = target.getHealth();
							target.damage(source, ((float)this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE) + amount) * this.abilities.getValue(CompanionBatAbilityType.COUNTER_ATTACK) / 4);
							this.applyOnHitEffects(target, targetHealth, target.getHealth());
							return false;
						}
					}
					this.world.playSound(null, this.getBlockPos(), SoundEvents.ITEM_SHIELD_BLOCK, SoundCategory.PLAYERS, 0.2F, this.getSoundPitch());
					return false;
				}
			}

			return super.damage(source, amount);
		}
	}

	public boolean canAttackWithOwner(LivingEntity target, LivingEntity owner) {
		return canAttackWithOwnerStatic(target, owner);
	}

	public static boolean canAttackWithOwnerStatic(LivingEntity target, LivingEntity owner) {
		if (target != owner && !(target instanceof CreeperEntity)) {
			if (target instanceof WolfEntity) {
				WolfEntity wolfEntity = (WolfEntity) target;
				return !wolfEntity.isTamed() || wolfEntity.getOwner() != owner;
			} else if (target instanceof CompanionBatEntity) {
				CompanionBatEntity companionBatEntity = (CompanionBatEntity) target;
				return companionBatEntity.getOwner() != owner;
			} else if (target instanceof PlayerEntity && owner instanceof PlayerEntity && !((PlayerEntity) owner).shouldDamagePlayer((PlayerEntity) target)) {
				return false;
			} else if (target instanceof AbstractHorseEntity && ((AbstractHorseEntity) target).isTame()) {
				return false;
			} else {
				return !(target instanceof TameableEntity) || !((TameableEntity) target).isTamed();
			}
		} else {
			return false;
		}
	}

	@Override
	protected void mobTick() {
		if (!this.world.isClient){
			if (this.isRoosting()) {
				if (this.getTarget() != null) {
					this.setRoosting(false);
				}
				if (this.hangingPosition == null || !this.world.getBlockState(this.hangingPosition).isSolidBlock(this.world, this.hangingPosition)) {
					this.setRoosting(false);
					if (!this.isSilent()) {
						this.world.syncWorldEvent((PlayerEntity) null, 1025, this.getBlockPos(), 0);
					}
				}
			}
			if (this.isRoosting() || this.hasNaturalRegeneration) {
				this.regenTicks--;
				if (this.regenTicks <= 0) {
					this.regenTicks = CompanionBats.CONFIG.regenTicks;
					if (this.isInjured()) {
						int val = Math.max(1, (int) (this.getMaxHealth() * 10 / 100));
						this.heal(val);
					}
				}
			}
		}
	}

	public CompanionBatEntity createChild(ServerWorld serverWorld, PassiveEntity passiveEntity) {
		return null;
	}

	public void dynamiteAttack(Entity target) {
		Box trajectoryBox = new Box(this.getX(), this.getY(), this.getZ(), target.getX(), target.getY(), target.getZ()).expand(2);
		if (!trajectoryBox.contains(this.getOwner().getPos())) {
			Vec3d vec3d = target.getVelocity();
			double d = target.getX() + vec3d.x - this.getX();
			double e = target.getEyeY() - 1.100000023841858D - this.getY();
			double f = target.getZ() + vec3d.z - this.getZ();
			double g = Math.sqrt(d * d + f * f);

			DynamiteEntity dynamite = new DynamiteEntity(this.world, this);
			dynamite.setPitch(dynamite.getPitch() + 20.0F);
			dynamite.setPower(this.abilities.getValue(CompanionBatAbilityType.DYNAMITE));
			dynamite.setVelocity(d, e + g * 0.2D, f, 0.75F, 8.0F);

			if (!this.isSilent()) {
				this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.PLAYERS, 1.0F, 1F + this.world.random.nextFloat() * 0.4F);
			}

			this.world.spawnEntity(dynamite);
			this.setVelocity(d * -0.4, 0, f * -0.4);
			this.rangedAttackTicks = CompanionBats.CONFIG.dynamiteTicks;
		}
	}

	public void tridentAttack(Entity target) {
		Box trajectoryBox = new Box(this.getX(), this.getY(), this.getZ(), target.getX(), target.getY(), target.getZ()).expand(1);
		if (!trajectoryBox.contains(this.getOwner().getPos())) {
			CompanionBatTridentEntity tridentEntity = new CompanionBatTridentEntity(this.world, this, new ItemStack(Items.TRIDENT));
			double d = target.getX() - tridentEntity.getX();
			double e = target.getBodyY(0.3333333333333333D) - tridentEntity.getY();
			double f = target.getZ() - tridentEntity.getZ();
			double g = Math.sqrt(d * d + f * f);
			tridentEntity.setVelocity(d, e + g * 0.20000000298023224D, f, 1.6F, 2.0F);
			this.world.spawnEntity(tridentEntity);
			if (!this.isSilent()) {
				this.playSound(SoundEvents.ENTITY_DROWNED_SHOOT, 1.0F, 1.0F / (this.getRandom().nextFloat() * 0.4F + 0.8F));
			}
			this.rangedAttackTicks = CompanionBats.CONFIG.tridentTicks;
		}
	}

	public boolean tryAttack(Entity target) {
		float targetHealth = target instanceof LivingEntity ? ((LivingEntity) target).getHealth() : 0;
		boolean bl = target.damage(this.getDamageSources().mobAttack(this), this.getAttackDamage(target));
		if (bl) {
			if (this.isSneakAttacking) {
				this.world.playSound(null, this.getBlockPos(), SoundEvents.ENTITY_PLAYER_ATTACK_CRIT , SoundCategory.PLAYERS, 0.5F, this.getSoundPitch() + 2.0F);
			}
			this.applyDamageEffects(this, target);
			this.applyOnHitEffects(target, targetHealth, (target instanceof LivingEntity ? ((LivingEntity) target).getHealth() : 0));
		}
		this.isSneakAttacking = false;
		return bl;
	}

	public float getAttackDamage(Entity target) {
		float attackDamage = (float) ((int) this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE));
		if (this.abilities.hasAbility(CompanionBatAbilityType.SNEAK_ATTACK) && target instanceof LivingEntity && this.isBehind((LivingEntity) target)) {
			attackDamage += attackDamage * this.abilities.getValue(CompanionBatAbilityType.SNEAK_ATTACK) / 4;
			this.isSneakAttacking = true;
		}
		return attackDamage;
	}

	private boolean isBehind(LivingEntity target) {
		return this.guaranteedSneakAttack || target.getHorizontalFacing().equals(this.getHorizontalFacing());
	}

	public void applyOnHitEffects(Entity target, float healthBefore, float healthAfter) {
		float damageDealt = healthBefore - healthAfter;
		if (damageDealt > 0) {
			this.gainExp(CompanionBats.CONFIG.expGain);
			if (this.abilities.hasAbility(CompanionBatAbilityType.LIFESTEAL)) {
				this.heal(damageDealt * this.abilities.getValue(CompanionBatAbilityType.LIFESTEAL) / 100);
			}
			if (this.abilities.hasAbility(CompanionBatAbilityType.BURN)) {
				target.setOnFireFor(this.abilities.getValue(CompanionBatAbilityType.BURN));
			}
			if (target instanceof LivingEntity) this.abilities.applyOnHitEffects((LivingEntity)target);
			if (this.abilities.hasAbility(CompanionBatAbilityType.COMBO_ATTACK)) {
				this.increaseComboLevel();
			}
			if (healthAfter <= 0 && this.hasTeleport){
				this.teleportTicks = 1;
			}
		}
	}

	private void increaseComboLevel() {
		this.comboAttackResetTicks = CompanionBats.CONFIG.comboAttackResetTicks;

		int comboAttackLevel = this.abilities.getValue(CompanionBatAbilityType.COMBO_ATTACK);
		this.setComboLevel(this.comboLevel+1);

		if (this.comboLevel > 5) {
			this.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 50, (int)(this.comboLevel / 25), false, false));
		}
		if (comboAttackLevel >= 2 && this.comboLevel > 10) {
			this.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 50, (int)(this.comboLevel / 30), false, false));
		}
		if (comboAttackLevel >= 3 && this.comboLevel > 15) {
			this.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 50, 0, false, false));
		}
		if (this.comboLevel > 45) {
			this.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 100, 0, false, false));
			if (this.comboLevel == 50){
				this.heal(this.getMaxHealth());
				BlockPos blockPos = this.getTarget().getBlockPos();
				int abilityLevel = this.abilities.get(CompanionBatAbilityType.COMBO_ATTACK);
				for (int i = 0; i < abilityLevel; i++){
					LightningEntity lightningEntity = (LightningEntity)EntityType.LIGHTNING_BOLT.create(this.world);
					lightningEntity.refreshPositionAfterTeleport(Vec3d.ofBottomCenter(blockPos));
					lightningEntity.setCosmetic(true);
					this.world.spawnEntity(lightningEntity);

					if (i == 0){
						List<Entity> list = this.world.getOtherEntities(this, new Box(blockPos.getX() - 3.0D, blockPos.getY() - 3.0D, blockPos.getZ() - 3.0D, blockPos.getX() + 3.0D, blockPos.getY() + 6.0D + 3.0D, blockPos.getZ() + 3.0D), entity -> entity.isAlive() && !(entity instanceof LightningEntity) && entity != this && entity != this.getOwner());
						Iterator<Entity> iterator = list.iterator();

						while(iterator.hasNext()) {
							Entity entity = (Entity)iterator.next();
							entity.onStruckByLightning((ServerWorld)this.world, lightningEntity);
							entity.damage(this.getDamageSources().lightningBolt(), (6.0F + (float)this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE)) * abilityLevel);
						}
					}
				}
				this.setComboLevel(0);
			}
		}
	}

	@Override
	public ActionResult interactMob(PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (this.world.isClient) {
			return (itemStack.isOf(Items.MILK_BUCKET) || this.canEat(itemStack, true)) ? ActionResult.CONSUME : ActionResult.PASS;
		} else {
			if (itemStack.isOf(Items.MILK_BUCKET)) {
				boolean ok = this.clearStatusEffects();
				if (ok) {
					this.world.sendEntityStatus(this, (byte)8);
					if (!player.getAbilities().creativeMode) {
						player.setStackInHand(hand, new ItemStack(Items.BUCKET));
					}
					return ActionResult.SUCCESS;
				}
				return ActionResult.PASS;
			}
			boolean res = this.healWithItem(itemStack);
			if (res) {
				if (!player.getAbilities().creativeMode) {
					itemStack.decrement(1);
				}
				this.world.sendEntityStatus(this, (byte)8);
				return ActionResult.SUCCESS;
			} else if (IS_FOOD_ITEM.test(itemStack) && player == this.getOwner()){
				ItemStack fluteStack = this.getFluteItemStack();
				if (fluteStack == null){
					player.giveItemStack(this.toItemStack());
					this.discard();
					return ActionResult.SUCCESS;
				}
			}
		}
		return ActionResult.PASS;
	}

	private void setComboLevel(int level){
		this.comboLevel = level;
		this.dataTracker.set(COMBO_PARTICLE_LEVEL, level == 0 ? 0 : (byte)((this.comboLevel / 10) + 1));
	}

	public boolean healWithItem(ItemStack stack) {
		if (!this.canEat(stack, false)) return false;
		if (stack.isOf(CompanionBats.EXPERIENCE_PIE)){
			this.gainExp(CompanionBats.CONFIG.experiencePieGain);
		}
		float amount = getItemHealAmount(stack);
		if (amount > 0) {
			this.heal(amount);
			return true;
		}
		return false;
	}

	public static float getItemHealAmount(ItemStack stack) {
		if (stack.isOf(Items.PUMPKIN_PIE) || stack.isOf(CompanionBats.EXPERIENCE_PIE)) {
			return 6.0F;
		} else if (stack.isOf(Items.GLOW_BERRIES)) {
			return 2.0F;
		}
		return 0;
	}

	protected boolean shouldSwimInFluids() {
		return false;
	}

	protected EntityNavigation createNavigation(World world) {
		BirdNavigation birdNavigation = new BirdNavigation(this, world);
		birdNavigation.setCanPathThroughDoors(false);
		birdNavigation.setCanSwim(false);
		birdNavigation.setCanEnterOpenDoors(true);
		return birdNavigation;
	}

	protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
		return dimensions.height / 2.0F;
	}

	@Environment(EnvType.CLIENT)
	public void handleStatus(byte status) {
		if (status == 8) {
			for (int i = 0; i < 3; i++) {
				double d = this.random.nextGaussian() * 0.01D;
				double e = this.random.nextGaussian() * 0.01D;
				double f = this.random.nextGaussian() * 0.01D;
				this.world.addParticle(ParticleTypes.HAPPY_VILLAGER, this.getParticleX(0.5D), this.getRandomBodyY(), this.getParticleZ(1.0D), d, e, f);
			}
		} else {
			super.handleStatus(status);
		}
	}

	public float calculateMovementSpeed(float movementSpeed) {
		if (this.isSubmergedIn(FluidTags.LAVA)) {
			return movementSpeed * (0.33F * (this.abilities.getValue(CompanionBatAbilityType.SWIM)+1));
		} else if (this.isSubmergedIn(FluidTags.WATER)) {
			return movementSpeed * (0.5F * (this.abilities.getValue(CompanionBatAbilityType.SWIM)+1));
		} else {
			return movementSpeed;
		}
	}

	public boolean returnToPlayerInventory() {
		if (this.world.isClient) return false;
		ServerPlayerEntity player = (ServerPlayerEntity) this.getOwner();
		if (player != null) {
			PlayerInventory inventory = player.getInventory();
			ImmutableList<DefaultedList<ItemStack>> mainAndOffhand = ImmutableList.of(inventory.main, inventory.offHand);
			Iterator<DefaultedList<ItemStack>> iterator = mainAndOffhand.iterator();
			while (iterator.hasNext()) {
				DefaultedList<ItemStack> defaultedList = (DefaultedList<ItemStack>) iterator.next();
				for (int i = 0; i < defaultedList.size(); ++i) {
					if (defaultedList.get(i).getItem() == CompanionBats.BAT_FLUTE_ITEM && defaultedList.get(i).getNbt().getUuid("EntityUUID").equals(this.getUuid())) {
						defaultedList.set(i, this.toItemStack());
						this.discard();
						world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_SLIME_ATTACK, SoundCategory.AMBIENT, 1F, 1F);
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
			PlayerInventory inventory = player.getInventory();
			ImmutableList<DefaultedList<ItemStack>> mainAndOffhand = ImmutableList.of(inventory.main, inventory.offHand);
			Iterator<DefaultedList<ItemStack>> iterator = mainAndOffhand.iterator();
			while (iterator.hasNext()) {
				DefaultedList<ItemStack> defaultedList = (DefaultedList<ItemStack>) iterator.next();
				for (int i = 0; i < defaultedList.size(); ++i) {
					if (defaultedList.get(i).getItem() == CompanionBats.BAT_FLUTE_ITEM && defaultedList.get(i).getNbt().getUuid("EntityUUID").equals(this.getUuid())) {
						return defaultedList.get(i);
					}
				}
			}
		}
		return null;
	}

	private boolean isWithinDistanceToAttack(LivingEntity entity) {
		return this.squaredDistanceTo(entity) < (double)(this.getWidth() * 2.0F * this.getWidth() * 2.0F + entity.getWidth());
	}

	public int getExp() {
		return this.exp;
	}

	private void setExp(int exp) {
		if (exp > ServerDataManager.combatLevels[ServerDataManager.combatLevels.length - 1].totalExp) {
			exp = ServerDataManager.combatLevels[ServerDataManager.combatLevels.length - 1].totalExp;
		}
		this.exp = exp;
		this.tryLevelUp();
	}

	private void addExp(int expToAdd) {
		this.setExp(this.exp + expToAdd);
	}

	private void gainExp(int expToAdd) {
		if (this.exp != ServerDataManager.combatLevels[ServerDataManager.combatLevels.length - 1].totalExp) {
			this.addExp(expToAdd);
		}
		if (this.currentClass != null) {
			CompanionBatClassLevel[] classLevels = ServerDataManager.classes.get(this.currentClass).levels;
			if (this.classExp != classLevels[classLevels.length - 1].totalExp) {
				this.addClassExp(expToAdd);
			}
		}
	}

	private int getClassExp() {
		return this.classExp;
	}

	private void setClassExp(int classExp) {
		CompanionBatClassLevel[] classLevels = ServerDataManager.classes.get(this.currentClass).levels;
		if (classExp > classLevels[classLevels.length - 1].totalExp) {
			classExp = classLevels[classLevels.length - 1].totalExp;
		}
		this.classExp = classExp;
		this.tryClassLevelUp();
	}

	private void addClassExp(int expToAdd) {
		this.setClassExp(this.classExp + expToAdd);
	}

	private void setAccessoryAbility(){
		ItemStack headStack = this.getEquippedStack(EquipmentSlot.HEAD);
		if (headStack.getItem() instanceof CompanionBatAccessoryItem) {
			this.abilities.addFromAccessory((CompanionBatAccessoryItem) headStack.getItem());
		}
	}

	private void setArmorAndClass() {
		ItemStack chestStack = this.getEquippedStack(EquipmentSlot.CHEST);
		if (chestStack.getItem() instanceof CompanionBatArmorItem) {
			CompanionBatArmorItem armor = (CompanionBatArmorItem) chestStack.getItem();
			this.currentClass = armor.getClassName();
			int armorToAdd = armor.getProtectionAmount();
			if (armorToAdd != 0) {
				this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).addTemporaryModifier(new EntityAttributeModifier(BAT_EQUIPMENT_ARMOR_BONUS_ID, "Equipment armor bonus", (double) armorToAdd, EntityAttributeModifier.Operation.ADDITION));
			}
		}
	}

	private void setClassesExp(EntityData entityData) {
		this.classesExp = entityData.getClassesExp();
		if (this.currentClass != null) {
			Integer exp = this.classesExp.get(this.currentClass);
			this.classExp = exp == null ? 0 : exp;
			this.classLevel = CompanionBatClassLevel.getClassLevelByExp(ServerDataManager.classes.get(this.currentClass).levels, (int) this.classExp);
		}
	}

	private void setCurrentClassLevelAbilities() {
		if (this.currentClass == null) return;
		CompanionBatClassLevel[] classLevels = ServerDataManager.classes.get(this.currentClass).levels;
		this.abilities.addFromClassLevel(classLevels[this.classLevel]);
	}

	private void setAbilitiesEffects(boolean firstTime) {
		if (firstTime){
			this.goalSelector.add(3, new CompanionBatPickUpItemGoal(this, 1.0D, 16.0F));
			this.goalSelector.add(4, new CompanionBatFollowOwnerGoal(this, 1.0D, 2.5F, 24.0F));
			this.goalSelector.add(5, new CompanionBatTransferItemsToOwnerGoal(this, 2.5F));
			this.goalSelector.add(6, new CompanionBatRoostGoal(this, 0.75F, 4.0F, CompanionBats.CONFIG.roostStartTicks));
			if (!this.abilities.hasAbility(CompanionBatAbilityType.CANNOT_ATTACK)){
				this.goalSelector.add(2, new MeleeAttackGoal(this, 1.0D, true));
				this.targetSelector.add(1, new CompanionBatTrackOwnerAttackerGoal(this));
				this.targetSelector.add(2, new CompanionBatAttackWithOwnerGoal(this));
				this.targetSelector.add(3, (new RevengeGoal(this, new Class[0])).setGroupRevenge());
				if (this.abilities.hasAbility(CompanionBatAbilityType.ATTACK_EVERYONE)){
					this.targetSelector.add(4, new CompanionBatTargetSelectorGoal(this, CompanionBatAbilityType.ATTACK_EVERYONE));
				} else if (this.abilities.hasAbility(CompanionBatAbilityType.ATTACK_HOSTILES)){
					this.targetSelector.add(4, new CompanionBatTargetSelectorGoal(this, CompanionBatAbilityType.ATTACK_HOSTILES));
				} else if (this.abilities.hasAbility(CompanionBatAbilityType.ATTACK_PASSIVE)){
					this.targetSelector.add(4, new CompanionBatTargetSelectorGoal(this, CompanionBatAbilityType.ATTACK_PASSIVE));
				}
			}
		}
		if (!this.hasPotionGoal && (this.abilities.hasAbility(CompanionBatAbilityType.EMERGENCY_POTION) || this.abilities.hasAbility(CompanionBatAbilityType.EFFECT_POTION))) {
			this.goalSelector.add(7, new CompanionBatThrowPotionGoal(this, 3.0F, CompanionBats.CONFIG.emergencyPotionTicks, CompanionBats.CONFIG.effectPotionTicks));
			this.hasPotionGoal = true;
		}
		if (!this.hasRangedAttackGoal && !this.abilities.hasAbility(CompanionBatAbilityType.CANNOT_ATTACK)){
			if (this.abilities.hasAbility(CompanionBatAbilityType.DYNAMITE)){
				this.goalSelector.add(1, new CompanionBatRangedAttackGoal(this, 5.0F, 9.0F, this::dynamiteAttack));
				this.hasRangedAttackGoal = true;
			} else if (this.abilities.hasAbility(CompanionBatAbilityType.TRIDENT)) {
				this.goalSelector.add(1, new CompanionBatRangedAttackGoal(this, 1.0F, 12.0F, this::tridentAttack));
				this.hasRangedAttackGoal = true;
			}
		}
		if (this.abilities.hasAbility(CompanionBatAbilityType.INCREASED_ARMOR)) {
			EntityAttributeInstance attr = this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR);
			if (!firstTime) attr.removeModifier(BAT_ARMOR_BONUS_ID);
			attr.addTemporaryModifier(new EntityAttributeModifier(BAT_ARMOR_BONUS_ID, "Ability armor bonus", this.abilities.getValue(CompanionBatAbilityType.INCREASED_ARMOR), EntityAttributeModifier.Operation.ADDITION));
		}
		if (this.abilities.hasAbility(CompanionBatAbilityType.INCREASED_ATTACK)) {
			EntityAttributeInstance attr = this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
			if (!firstTime) attr.removeModifier(BAT_ATTACK_BONUS_ID);
			attr.addTemporaryModifier(new EntityAttributeModifier(BAT_ATTACK_BONUS_ID, "Ability attack bonus", (double) (attr.getBaseValue() * this.abilities.getValue(CompanionBatAbilityType.INCREASED_ATTACK) / 100), EntityAttributeModifier.Operation.ADDITION));
		}
		if (this.abilities.hasAbility(CompanionBatAbilityType.INCREASED_SPEED)) {
			EntityAttributeInstance attr = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
			if (!firstTime) attr.removeModifier(BAT_SPEED_BONUS_ID);
			attr.addTemporaryModifier(new EntityAttributeModifier(BAT_SPEED_BONUS_ID, "Ability speed bonus", (double) (attr.getBaseValue() * this.abilities.getValue(CompanionBatAbilityType.INCREASED_SPEED) / 100), EntityAttributeModifier.Operation.ADDITION));
		}
		if (this.abilities.hasAbility(CompanionBatAbilityType.TELEPORT)) {
			this.hasTeleport = true;
		}
		if (this.abilities.hasAbility(CompanionBatAbilityType.NATURAL_REGENERATION)) {
			this.hasNaturalRegeneration = true;
		}
		if (this.abilities.hasAbility(CompanionBatAbilityType.FLAME_EATER)) {
			this.hasFlameEater = true;
		}
		if (this.abilities.hasAbility(CompanionBatAbilityType.ATTRACT_FLAMES)) {
			this.hasAttractFlames = true;
		}
		if (this.abilities.hasStatusEffect(StatusEffects.FIRE_RESISTANCE)) {
			this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, 0F);
			this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, 0F);
		}
		if (this.abilities.hasAbility(CompanionBatAbilityType.LOOTING)) {
			ItemStack stack = new ItemStack(Items.STICK);
			stack.addEnchantment(Enchantments.LOOTING, this.abilities.getValue(CompanionBatAbilityType.LOOTING));
			this.equipStack(EquipmentSlot.MAINHAND, stack);
			this.setEquipmentDropChance(EquipmentSlot.MAINHAND, 0.0F);
		}
	}

	public void scheduleTeleport(Entity target){
		if (!this.hasTeleport) return;
		this.teleportTarget = target;
	}

	private boolean tryTeleportTo(Entity target) {
		return target != null && target.isAlive() && this.squaredDistanceTo(target) <= this.abilities.getValue(CompanionBatAbilityType.TELEPORT) && this.teleportTo(target);
	}

	private boolean teleportTo(Entity entity) {
		Direction looking = entity.getHorizontalFacing().getOpposite();
		boolean success = this.teleport(entity.getX() + looking.getOffsetX(), entity.getEyeY(), entity.getZ() + looking.getOffsetZ(), true);
		if (success) {
			if (!this.isSilent()) this.world.playSound((PlayerEntity) null, this.prevX, this.prevY, this.prevZ, SoundEvents.ENTITY_ENDERMAN_TELEPORT, this.getSoundCategory(), 0.25F, this.getSoundPitch() + 1.0F);
			if (entity instanceof LivingEntity && this.canAttackWithOwner((LivingEntity) entity, this.getOwner())){
				this.guaranteedSneakAttack = true;
				this.tryAttack(entity);
				this.guaranteedSneakAttack = false;
			}
			this.teleportTicks = CompanionBats.CONFIG.teleportTicks;
		}
		return success;
	}

	private void tryLevelUp() {
		if (ServerDataManager.combatLevels.length > this.level + 1 && ServerDataManager.combatLevels[this.level + 1].totalExp <= this.exp) {
			this.level++;
			this.notifyLevelUp(this.level);
			this.setLevelAttributes(this.level);
			this.heal(this.getMaxHealth());
		}
	}

	private void tryClassLevelUp() {
		CompanionBatClassLevel[] classLevels = ServerDataManager.classes.get(this.currentClass).levels;
		if (classLevels.length > this.classLevel + 1 && classLevels[this.classLevel + 1].totalExp <= this.classExp) {
			this.classLevel++;
			this.notifyClassLevelUp(this.classLevel, classLevels);
			this.setCurrentClassLevelAbilities();
			this.setAbilitiesEffects(false);
		}
	}

	private void setLevel(int exp) {
		this.exp = exp;
		this.level = CompanionBatCombatLevel.getLevelByExp(ServerDataManager.combatLevels, (int) this.exp);
	}

	protected void setLevelAttributes(int level) {
		this.getAttributes().getCustomInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(CompanionBatCombatLevel.getLevelHealth(CompanionBats.CONFIG.baseHealth, ServerDataManager.combatLevels, level));
		this.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(CompanionBatCombatLevel.getLevelAttack(CompanionBats.CONFIG.baseAttack, ServerDataManager.combatLevels, level));
		this.getAttributes().getCustomInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(CompanionBatCombatLevel.getLevelSpeed(CompanionBats.CONFIG.baseSpeed, ServerDataManager.combatLevels, level));
	}

	protected void notifyLevelUp(int level) {
		if (level > 0) {
			MutableText message = Text.translatable("entity.companion_bats.bat.level_up", this.hasCustomName() ? this.getCustomName() : Text.translatable("entity.companion_bats.bat.your_bat"), level + 1).append("\n");
			if (ServerDataManager.combatLevels[level].healthBonus > ServerDataManager.combatLevels[level - 1].healthBonus) {
				message.append(Text.literal("+").formatted(Formatting.GOLD)).append(" ");
				message.append(Text.translatable("entity.companion_bats.bat.level_up_health", (int) (ServerDataManager.combatLevels[level].healthBonus - ServerDataManager.combatLevels[level - 1].healthBonus))).append(" ");
			}
			if (ServerDataManager.combatLevels[level].attackBonus > ServerDataManager.combatLevels[level - 1].attackBonus) {
				message.append(Text.literal("+").formatted(Formatting.GOLD)).append(" ");
				message.append(Text.translatable("entity.companion_bats.bat.level_up_attack", (int) (ServerDataManager.combatLevels[level].attackBonus - ServerDataManager.combatLevels[level - 1].attackBonus))).append(" ");
			}
			if (ServerDataManager.combatLevels[level].speedBonus > ServerDataManager.combatLevels[level - 1].speedBonus) {
				message.append(Text.literal("+").formatted(Formatting.GOLD)).append(" ");
				message.append(Text.translatable("entity.companion_bats.bat.level_up_speed", Math.round(100 - ((CompanionBats.CONFIG.baseSpeed + ServerDataManager.combatLevels[level - 1].speedBonus) / (CompanionBats.CONFIG.baseSpeed + ServerDataManager.combatLevels[level].speedBonus) * 100)))).append(" ");
			}
			((PlayerEntity) this.getOwner()).sendMessage(message, false);
		}
	}

	protected void notifyClassLevelUp(int classLevel, CompanionBatClassLevel[] classLevels) {
		CompanionBatClass cls = ClientDataManager.classes.get(this.currentClass);
		MutableText message = Text.translatable(
			"entity.companion_bats.bat.class_level_up",
			this.hasCustomName() ? this.getCustomName() : Text.translatable("entity.companion_bats.bat.your_bat"),
			cls != null && cls.label != null ? Text.literal(cls.label) :  Text.translatable("class." + this.currentClass.replace(":", ".")),
			classLevel + 1
		);
		if (classLevels[classLevel].ability != null){
			CompanionBatAbilities ability = new CompanionBatAbilities();
			ability.addFromClassLevel(classLevels[classLevel]);
			Pair<MutableText, Integer> pair = ability.toTranslatedList().get(0);
			message.append("\n");
			String obtainedOrLevelUp = "ability_obtained";
			if (classLevels[classLevel].permanent){
				message.append(Text.translatable("entity.companion_bats.bat.permanent_ability").formatted(Formatting.LIGHT_PURPLE)).append(" ");
			} else {
				message.append(Text.translatable("entity.companion_bats.bat.ability").formatted(Formatting.GREEN)).append(" ");
				if (this.abilities.has(classLevels[classLevel].ability)) obtainedOrLevelUp = "ability_level_up";
			}
			message.append(Text.translatable("entity.companion_bats.bat."+obtainedOrLevelUp, pair.getLeft()));
		}
		((PlayerEntity) this.getOwner()).sendMessage(message, false);
	}

	private void writeExpToTag(EntityData entityData) {
		entityData.putExp(this.getExp());
		entityData.putClassesExp(this.classesExp);
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
			this.emergencyPotionTicks = CompanionBats.CONFIG.emergencyPotionTicks;
			this.effectPotionTicks = CompanionBats.CONFIG.effectPotionTicks;
		}
	}

	public ItemStack toItemStack() {
		ItemStack batItemStack = new ItemStack(this.itemType == 1 ? CompanionBats.NETHERITE_BAT_ITEM : CompanionBats.BAT_ITEM);
		NbtCompound nbt = batItemStack.getOrCreateNbt();
		if (this.itemNbt != null) {
			for (String key : this.itemNbt.getKeys()) {
				nbt.put(key, this.itemNbt.get(key));
			}
		}
		batItemStack.setNbt(nbt);
		if (this.hasCustomName()) {
			batItemStack.setCustomName(this.getCustomName());
		}
		// Set companion bat item durability relative to the bat health
		batItemStack.setDamage(Math.round((1 - (this.getHealth() / this.getMaxHealth())) * 100));

		EntityData.fromCompanionBatEntity(this).toStack(batItemStack);
		return batItemStack;
	}

	public static CompanionBatEntity spawnFromItemStack(ServerWorld world, ItemStack itemStack, PlayerEntity player){
		Vec3d pos = player.getPos();
		EntityData entityData = new EntityData(itemStack);
		entityData.putOwner(player);
		return (CompanionBatEntity)CompanionBats.COMPANION_BAT.spawnFromItemStack(world, itemStack, player, new BlockPos((int)pos.x, (int)Math.ceil(pos.y), (int)pos.z), SpawnReason.SPAWN_EGG, false, false);
	}

	public ItemStack getAccessory() {
		return this.getEquippedStack(EquipmentSlot.HEAD);
	}

	public ItemStack getArmorType() {
		return this.getEquippedStack(EquipmentSlot.CHEST);
	}

	public ItemStack getBundle() {
		return this.getEquippedStack(EquipmentSlot.FEET);
	}

	public boolean canEat(ItemStack stack, boolean clientSide){
		if (stack.isOf(CompanionBats.EXPERIENCE_PIE)){
			if (this.currentClass != null) {
				CompanionBatClassLevel[] classLevels = (clientSide ? ClientDataManager.classes : ServerDataManager.classes).get(this.currentClass).levels;
				if (this.classExp < classLevels[classLevels.length - 1].totalExp) return true;
			}
			CompanionBatCombatLevel[] levels = (clientSide ? ClientDataManager.combatLevels : ServerDataManager.combatLevels);
			return this.isInjured() || (this.exp < levels[levels.length - 1].totalExp);
		} else {
			return this.isInjured() && IS_FOOD_ITEM.test(stack);
		}
	}

	public Byte getGuardMode(){
		return this.guardMode;
	}

	public void setGuardMode(Byte mode){
		this.guardMode = mode;
	}

	public void setTarget(@Nullable LivingEntity target) {
		this.scheduleTeleport(target);
		super.setTarget(target);
	}

	private void writeItemData(NbtCompound nbt) {
		NbtCompound itemData = new NbtCompound();
		itemData.put("Nbt", this.itemNbt);
		if (this.itemType > 0) itemData.putInt("Type", this.itemType);
		nbt.put("Item", itemData);
	}

	private void setItemData(NbtCompound nbt) {
		if (nbt.contains("Item")) {
			NbtCompound itemData = nbt.getCompound("Item");
			this.itemNbt = itemData.getCompound("Nbt");
			this.itemType = itemData.getByte("Type");
		}
	}

	public void setItemDataFromStack(ItemStack itemStack) {
		this.itemNbt = new NbtCompound();
		NbtCompound stackNbt = itemStack.getNbt();
		if (stackNbt.contains("Enchantments")) this.itemNbt.put("Enchantments", stackNbt.get("Enchantments"));
		if (itemStack.getItem() == CompanionBats.NETHERITE_BAT_ITEM) this.itemType = 1;
	}

	public EntityView method_48926() {
        return this.getWorld();
    }

	public static List<CompanionBatEntity> getPlayerBats(ServerPlayerEntity player) {
		List<CompanionBatEntity> entities = new ArrayList<CompanionBatEntity>();
		if (player != null) {
			PlayerInventory inventory = player.getInventory();
			ImmutableList<DefaultedList<ItemStack>> mainAndOffhand = ImmutableList.of(inventory.main, inventory.offHand);
			Iterator<DefaultedList<ItemStack>> iterator = mainAndOffhand.iterator();
			while (iterator.hasNext()) {
				DefaultedList<ItemStack> defaultedList = (DefaultedList<ItemStack>) iterator.next();
				for (int i = 0; i < defaultedList.size(); ++i) {
					if (defaultedList.get(i).getItem() == CompanionBats.BAT_FLUTE_ITEM) {
						ServerWorld serverWorld = (ServerWorld)player.world;
						Entity entity = serverWorld.getEntity(defaultedList.get(i).getNbt().getUuid("EntityUUID"));
						if (entity != null){
							entities.add((CompanionBatEntity)entity);
						}
					}
				}
			}
		}
		return entities;
	}

	static {
		BAT_FLAGS = DataTracker.registerData(CompanionBatEntity.class, TrackedDataHandlerRegistry.BYTE);
		COMBO_PARTICLE_LEVEL = DataTracker.registerData(CompanionBatEntity.class, TrackedDataHandlerRegistry.BYTE);
		IS_TAMING_ITEM = (itemStack) -> itemStack.isOf(Items.PUMPKIN_PIE) || itemStack.isOf(CompanionBats.EXPERIENCE_PIE);
		IS_FOOD_ITEM = (itemStack) -> IS_TAMING_ITEM.test(itemStack) || itemStack.isOf(Items.GLOW_BERRIES);
		IS_REVIVE_ITEM_ENTITY = (itemEntity) -> IS_TAMING_ITEM.test(itemEntity.getStack());
	}
}