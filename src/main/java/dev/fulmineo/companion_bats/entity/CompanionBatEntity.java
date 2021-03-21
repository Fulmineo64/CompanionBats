package dev.fulmineo.companion_bats.entity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;

import org.jetbrains.annotations.Nullable;

import dev.fulmineo.companion_bats.CompanionBats;
import dev.fulmineo.companion_bats.entity.CompanionBatLevels.CompanionBatClassLevel;
import dev.fulmineo.companion_bats.entity.ai.control.CompanionBatMoveControl;
import dev.fulmineo.companion_bats.entity.ai.goal.CompanionBatAttackWithOwnerGoal;
import dev.fulmineo.companion_bats.entity.ai.goal.CompanionBatFollowOwnerGoal;
import dev.fulmineo.companion_bats.entity.ai.goal.CompanionBatPickUpItemGoal;
import dev.fulmineo.companion_bats.entity.ai.goal.CompanionBatRoostGoal;
import dev.fulmineo.companion_bats.entity.ai.goal.CompanionBatTargetSelectorGoal;
import dev.fulmineo.companion_bats.entity.ai.goal.CompanionBatThrowPotionGoal;
import dev.fulmineo.companion_bats.entity.ai.goal.CompanionBatTrackOwnerAttackerGoal;
import dev.fulmineo.companion_bats.entity.ai.goal.CompanionBatTransferItemsToOwnerGoal;
import dev.fulmineo.companion_bats.CompanionBatAbilities;
import dev.fulmineo.companion_bats.CompanionBatAbility;
import dev.fulmineo.companion_bats.item.CompanionBatAccessoryItem;
import dev.fulmineo.companion_bats.item.CompanionBatArmorItem;
import dev.fulmineo.companion_bats.CompanionBatClass;
import dev.fulmineo.companion_bats.item.CompanionBatItem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
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
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CompanionBatEntity extends TameableEntity {
	private static final TrackedData<Byte> BAT_FLAGS;
	public CompanionBatAbilities abilities = new CompanionBatAbilities();
	private Map<CompanionBatClass, Integer> classesExp = new HashMap<>();
	private int healTicks;

	// Constants

	private static final UUID BAT_EQUIPMENT_ARMOR_BONUS_ID = UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F296");
	private static final UUID BAT_ARMOR_BONUS_ID = UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F297");
	private static final UUID BAT_ATTACK_BONUS_ID = UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F298");
	private static final UUID BAT_SPEED_BONUS_ID = UUID.fromString("556E1665-8B10-40C8-8F9D-CF9B1667F299");

	// Configurable values
	// TODO: Add configuration for these values and levels

	private static final float BASE_HEALTH = 6.0F;
	private static final float BASE_ATTACK = 2.0F;
	private static final float BASE_SPEED = 0.35F;

	private static final int EXP_GAIN = 1;
	public static final int EXPERIENCE_PIE_GAIN = 100;

	private static final int ROOST_START_TICKS = 200;
	private static final int HEAL_TICKS = 600;
	private static final int EMERGENCY_POTION_TICKS = 5000;
	private static final int EFFECT_POTION_TICKS = 2000;
	private static final int COMBO_ATTACK_RESET_TICKS = 600;
	private static final int TELEPORT_TICKS = 100;
	private static final int RETRY_TELEPORT_TICKS = 10;
	private static final int ADVENTURER_AURA_TICKS = 420;

	public static final Predicate<ItemStack> IS_FOOD_ITEM;
	public static final Predicate<ItemEntity> IS_FOOD_ITEM_ENTITY;
	public BlockPos hangingPosition;
	public BlockPos fleeingPosition;
	public BlockPos diggingPosition;
	public int emergencyPotionTicks;
	public int effectPotionTicks;

	private CompanionBatClass currentClass;
	private int exp = 0;
	private int classExp = 0;
	private int level = -1;
	private int classLevel = -1;
	private boolean hasTeleport;
	private boolean hasAdventurerAura;
	private boolean hasPotionGoal;
	private int comboAttackResetTicks = COMBO_ATTACK_RESET_TICKS;
	private int comboLevel = 0;
	private int teleportTicks = TELEPORT_TICKS;
	private int adventurerAuraTicks = 1;

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
	}

	public void writeCustomDataToTag(CompoundTag tag) {
		super.writeCustomDataToTag(tag);
		tag.putInt("exp", this.getExp());
		this.writeExpToTag(tag);
		this.writePotionTicks(tag);
	}

	public void readCustomDataFromTag(CompoundTag tag) {
		super.readCustomDataFromTag(tag);
		this.setLevel(tag.getInt("exp"));
		this.setLevelAttributes(this.level);
		this.setAccessoryAbility();
		this.setBatClass();
		this.setClasses(tag);
		this.abilities.setFromNbt(tag);
		this.setAbilitiesEffects(true);
		this.setPotionTicks(tag);
	}

	protected float getSoundVolume() {
		return 0.1F;
	}

	protected float getSoundPitch() {
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
			.add(EntityAttributes.GENERIC_MAX_HEALTH, BASE_HEALTH)
			.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, BASE_ATTACK)
			.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, BASE_SPEED);
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
			this.healTicks = HEAL_TICKS;
		} else {
			this.dataTracker.set(BAT_FLAGS, (byte) (b & -2));
			this.hangingPosition = null;
		}
	}

	public void tick() {
		super.tick();
		if (this.isRoosting()) {
			this.setVelocity(Vec3d.ZERO);
			this.setPos(this.getX(), (double) MathHelper.floor(this.getY()) + 1.0D - (double) this.getHeight(), this.getZ());
			this.comboAttackResetTicks = 0;
		} else {
			this.setVelocity(this.getVelocity().multiply(1.0D, 0.6D, 1.0D));

			if (this.comboAttackResetTicks > 0) {
				this.comboAttackResetTicks--;
				if (this.comboAttackResetTicks == 0) {
					this.comboLevel = 0;
				}
			}

			if (this.hasTeleport) {
				this.teleportTicks--;
				if (this.teleportTicks == 0) {
					LivingEntity target = this.getTarget();
					if (target != null && this.squaredDistanceTo(target) <= this.abilities.getValue(CompanionBatAbility.TELEPORT) && this.teleportTo(target)) {
						this.teleportTicks = TELEPORT_TICKS;
					} else {
						this.teleportTicks = RETRY_TELEPORT_TICKS;
					}
				}
			}

			if (this.hasAdventurerAura){
				this.adventurerAuraTicks--;
				if (this.adventurerAuraTicks == 0) {
					this.adventurerAuraTicks = ADVENTURER_AURA_TICKS > 200 ? ADVENTURER_AURA_TICKS - 200 : ADVENTURER_AURA_TICKS;
					LivingEntity owner = this.getOwner();
					if (owner != null){
						owner.addStatusEffect(new StatusEffectInstance(StatusEffects.LUCK, ADVENTURER_AURA_TICKS, 0, false, false));
					}
				}
			}
		}
	}

	public void onDeath(DamageSource source) {
		if (!this.returnToPlayerInventory()) super.onDeath(source);
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

	public boolean damage(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source)) {
			return false;
		} else {
			if (!this.world.isClient) {
				if (this.isRoosting()) {
					this.setRoosting(false);
				}
			}

			CompanionBats.info("health before damage "+this.getHealth());

			if (!source.isUnblockable() && this.abilities.has(CompanionBatAbility.BLOCK_ATTACK)) {
				int roll = this.world.random.nextInt(100);
				if (roll < this.abilities.getValue(CompanionBatAbility.BLOCK_ATTACK)) {
					if (this.abilities.has(CompanionBatAbility.COUNTER_ATTACK) && source.getAttacker() instanceof LivingEntity) {
						LivingEntity target = (LivingEntity) source.getAttacker();
						LivingEntity owner = this.getOwner();
						if (target != owner && this.canAttackWithOwner(target, this.getOwner()) && this.isWithinDistanceToAttack(target)){
							this.world.playSound(null, this.getBlockPos(), SoundEvents.BLOCK_ANVIL_LAND , SoundCategory.PLAYERS, 0.15F, this.getSoundPitch() + 2F);
							float targetHealth = target.getHealth();
							target.damage(source, ((float)this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE) + amount) * this.abilities.getValue(CompanionBatAbility.COUNTER_ATTACK) / 4);
							this.onAttack(target, targetHealth - target.getHealth());
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
		if (!(target instanceof CreeperEntity)) {
			if (target instanceof WolfEntity) {
				WolfEntity wolfEntity = (WolfEntity) target;
				return !wolfEntity.isTamed() || wolfEntity.getOwner() != owner;
			} else if (target instanceof CompanionBatEntity) {
				CompanionBatEntity companionBatEntity = (CompanionBatEntity) target;
				return companionBatEntity.getOwner() != owner;
			} else if (target instanceof PlayerEntity && owner instanceof PlayerEntity && !((PlayerEntity) owner).shouldDamagePlayer((PlayerEntity) target)) {
				return false;
			} else if (target instanceof HorseBaseEntity && ((HorseBaseEntity) target).isTame()) {
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
		if (this.isRoosting()) {
			if (this.getTarget() != null) {
				this.setRoosting(false);
			}
			this.healTicks--;
			if (this.hangingPosition == null || !this.world.getBlockState(this.hangingPosition).isSolidBlock(this.world, this.hangingPosition)) {
				this.setRoosting(false);
				if (!this.isSilent()) {
					this.world.syncWorldEvent((PlayerEntity) null, 1025, this.getBlockPos(), 0);
				}
			} else if (this.healTicks <= 0) {
				this.healTicks = HEAL_TICKS;
				if (this.isInjured()) {
					int val = Math.max(1, (int) (this.getMaxHealth() * 10 / 100));
					this.heal(val);
				}
			}
		}
	}

	public CompanionBatEntity createChild(ServerWorld serverWorld, PassiveEntity passiveEntity) {
		return null;
	}

	public boolean tryAttack(Entity target) {
		float targetHealth = target instanceof LivingEntity ? ((LivingEntity) target).getHealth() : 0;
		boolean bl = target.damage(DamageSource.mob(this), this.getAttackDamage(target));
		if (bl) {
			this.dealDamage(this, target);
			float damageDealt = targetHealth - (target instanceof LivingEntity ? ((LivingEntity) target).getHealth() : 0);
			this.onAttack(target, damageDealt);
		}
		return bl;
	}

	private float getAttackDamage(Entity target) {
		float attackDamage = (float) ((int) this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE));
		if (this.abilities.has(CompanionBatAbility.SNEAK_ATTACK) && target instanceof LivingEntity && this.isBehind((LivingEntity) target)) {
			this.world.playSound(null, this.getBlockPos(), SoundEvents.ENTITY_PLAYER_ATTACK_CRIT , SoundCategory.PLAYERS, 1F, 1.5F);
			attackDamage += attackDamage * this.abilities.getValue(CompanionBatAbility.SNEAK_ATTACK) / 4;
		}
		return attackDamage;
	}

	private boolean isBehind(LivingEntity target) {
		return target.getHorizontalFacing().equals(this.getHorizontalFacing());
	}

	private void onAttack(Entity target, float damageDealt) {
		CompanionBats.info("damage dealt " + damageDealt);
		if (damageDealt > 0) {
			this.gainExp(EXP_GAIN);
			if (this.abilities.has(CompanionBatAbility.LIFESTEAL)) {
				this.heal(damageDealt * this.abilities.getValue(CompanionBatAbility.LIFESTEAL) / 100);
			}
			if (this.abilities.has(CompanionBatAbility.BURN)) {
				target.setOnFireFor(this.abilities.getValue(CompanionBatAbility.BURN));
			}
			if (target instanceof LivingEntity){
				LivingEntity livingTarget = (LivingEntity)target;
				if (this.abilities.has(CompanionBatAbility.SLOWNESS)) {
					livingTarget.addStatusEffect(new StatusEffectInstance(StatusEffects.SLOWNESS, 60, this.abilities.getValue(CompanionBatAbility.SLOWNESS)));
				}
				if (this.abilities.has(CompanionBatAbility.WEAKNESS)) {
					livingTarget.addStatusEffect(new StatusEffectInstance(StatusEffects.WEAKNESS, 60, this.abilities.getValue(CompanionBatAbility.WEAKNESS)));
				}
				if (this.abilities.has(CompanionBatAbility.WITHER)) {
					livingTarget.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 80, this.abilities.getValue(CompanionBatAbility.WITHER)));
				}
			}
			if (this.abilities.has(CompanionBatAbility.COMBO_ATTACK)) {
				this.increaseComboLevel();
			}
		}
	}

	private void increaseComboLevel() {
		this.comboAttackResetTicks = COMBO_ATTACK_RESET_TICKS;

		int comboAttackLevel = this.abilities.getValue(CompanionBatAbility.COMBO_ATTACK);
		this.comboLevel++;
		CompanionBats.info("Increased combo level to: "+this.comboLevel);

		if (comboAttackLevel >= 3 && this.comboLevel % 15 == 0) {
			this.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 100, (int)(this.comboLevel / 33) + 2));
		} else if (comboAttackLevel >= 2 && this.comboLevel % 10 == 0) {
			this.addStatusEffect(new StatusEffectInstance(StatusEffects.STRENGTH, 100, (int)(this.comboLevel / 33)));
		} else if (this.comboLevel % 5 == 0) {
			this.addStatusEffect(new StatusEffectInstance(StatusEffects.SPEED, 100, (int)(this.comboLevel / 33)));
		}

		if (this.comboLevel == 100){
			this.comboLevel = 0;
		}
	}

	@Override
	public ActionResult interactMob(PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (this.world.isClient) {
			return this.canEat(itemStack) ? ActionResult.CONSUME : ActionResult.PASS;
		} else {
			boolean res = this.healWithItem(itemStack);
			if (res) {
				if (!player.abilities.creativeMode) {
					itemStack.decrement(1);
				}
				this.world.sendEntityStatus(this, (byte)8);
				return ActionResult.SUCCESS;
			} else if (IS_FOOD_ITEM.test(itemStack) && player == this.getOwner()){
				ItemStack fluteStack = this.getFluteItemStack();
				if (fluteStack == null){
					this.remove();
					player.giveItemStack(this.toItem());
					return ActionResult.SUCCESS;
				}
			}
		}
		return ActionResult.PASS;
	}

	public boolean healWithItem(ItemStack stack) {
		if (!this.canEat(stack)) return false;
		if (stack.getItem() == CompanionBats.EXPERIENCE_PIE){
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
		if (stack.getItem() == Items.PUMPKIN_PIE || stack.getItem() == CompanionBats.EXPERIENCE_PIE) {
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

	protected EntityNavigation createNavigation(World world) {
		BirdNavigation birdNavigation = new BirdNavigation(this, world);
		birdNavigation.setCanPathThroughDoors(false);
		birdNavigation.setCanSwim(true);
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

	public boolean returnToPlayerInventory() {
		ServerPlayerEntity player = (ServerPlayerEntity) this.getOwner();
		if (player != null) {
			PlayerInventory inventory = player.inventory;
			ImmutableList<DefaultedList<ItemStack>> mainAndOffhand = ImmutableList.of(inventory.main, inventory.offHand);
			Iterator<DefaultedList<ItemStack>> iterator = mainAndOffhand.iterator();
			while (iterator.hasNext()) {
				DefaultedList<ItemStack> defaultedList = (DefaultedList<ItemStack>) iterator.next();
				for (int i = 0; i < defaultedList.size(); ++i) {
					if (defaultedList.get(i).getItem() == CompanionBats.BAT_FLUTE_ITEM && defaultedList.get(i).getTag().getUuid("entityUuid").equals(this.getUuid())) {
						this.remove();
						defaultedList.set(i, this.toItem());
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
			PlayerInventory inventory = player.inventory;
			ImmutableList<DefaultedList<ItemStack>> mainAndOffhand = ImmutableList.of(inventory.main, inventory.offHand);
			Iterator<DefaultedList<ItemStack>> iterator = mainAndOffhand.iterator();
			while (iterator.hasNext()) {
				DefaultedList<ItemStack> defaultedList = (DefaultedList<ItemStack>) iterator.next();
				for (int i = 0; i < defaultedList.size(); ++i) {
					if (defaultedList.get(i).getItem() == CompanionBats.BAT_FLUTE_ITEM && defaultedList.get(i).getTag().getUuid("entityUuid").equals(this.getUuid())) {
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
		if (this.exp < CompanionBatLevels.LEVELS[CompanionBatLevels.LEVELS.length - 1].totalExpNeeded) {
			this.addExp(expToAdd);
		}
		if (this.currentClass != null) {
			CompanionBatClassLevel[] classLevels = CompanionBatLevels.CLASS_LEVELS.get(this.currentClass);
			if (this.classExp < classLevels[classLevels.length - 1].totalExpNeeded) {
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
		ItemStack headStack = this.getEquippedStack(EquipmentSlot.HEAD);
		if (headStack.getItem() instanceof CompanionBatAccessoryItem) {
			this.abilities.addFromAccessory((CompanionBatAccessoryItem) headStack.getItem());
		}
	}

	private void setBatClass() {
		ItemStack chestStack = this.getEquippedStack(EquipmentSlot.CHEST);
		if (chestStack.getItem() instanceof CompanionBatArmorItem) {
			CompanionBatArmorItem armor = (CompanionBatArmorItem) chestStack.getItem();
			this.currentClass = armor.getBatClass();
		}
	}

	private void setClasses(CompoundTag entityData) {
		for (CompanionBatClass cls : CompanionBatClass.values()) {
			this.classesExp.put(cls, entityData.getInt(cls.getExpTagName()));
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
			this.goalSelector.add(2, new CompanionBatPickUpItemGoal(this, 1.0D, 16.0F));
			this.goalSelector.add(3, new CompanionBatFollowOwnerGoal(this, 1.0D, 2.5F, 24.0F));
			this.goalSelector.add(4, new CompanionBatTransferItemsToOwnerGoal(this, 2.5F));
			this.goalSelector.add(5, new CompanionBatRoostGoal(this, 0.75F, 4.0F, ROOST_START_TICKS));
			if (!this.abilities.has(CompanionBatAbility.CANNOT_ATTACK)){
				this.goalSelector.add(1, new MeleeAttackGoal(this, 1.0D, true));
				this.targetSelector.add(1, new CompanionBatTrackOwnerAttackerGoal(this));
				this.targetSelector.add(2, new CompanionBatAttackWithOwnerGoal(this));
				this.targetSelector.add(3, (new RevengeGoal(this, new Class[0])).setGroupRevenge());
				if (this.abilities.has(CompanionBatAbility.ATTACK_EVERYONE)){
					this.targetSelector.add(4, new CompanionBatTargetSelectorGoal(this, CompanionBatAbility.ATTACK_EVERYONE));
				} else if (this.abilities.has(CompanionBatAbility.ATTACK_HOSTILES)){
					this.targetSelector.add(4, new CompanionBatTargetSelectorGoal(this, CompanionBatAbility.ATTACK_HOSTILES));
				} else if (this.abilities.has(CompanionBatAbility.ATTACK_PASSIVE)){
					this.targetSelector.add(4, new CompanionBatTargetSelectorGoal(this, CompanionBatAbility.ATTACK_PASSIVE));
				}
			}
		}
		if (this.abilities.has(CompanionBatAbility.EMERGENCY_POTION) || this.abilities.has(CompanionBatAbility.EFFECT_POTION)) {
			if (!this.hasPotionGoal){
				this.goalSelector.add(6, new CompanionBatThrowPotionGoal(this, 3.0F, EMERGENCY_POTION_TICKS, EFFECT_POTION_TICKS));
				this.hasPotionGoal = true;
			}
		}
		if (this.abilities.has(CompanionBatAbility.INCREASED_ARMOR)) {
			EntityAttributeInstance attr = this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR);
			if (!firstTime) attr.removeModifier(BAT_ARMOR_BONUS_ID);
			attr.addTemporaryModifier(new EntityAttributeModifier(BAT_ARMOR_BONUS_ID, "Ability armor bonus", this.abilities.getValue(CompanionBatAbility.INCREASED_ARMOR), EntityAttributeModifier.Operation.ADDITION));
		}
		if (this.abilities.has(CompanionBatAbility.INCREASED_ATTACK)) {
			EntityAttributeInstance attr = this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
			if (!firstTime) attr.removeModifier(BAT_ATTACK_BONUS_ID);
			attr.addTemporaryModifier(new EntityAttributeModifier(BAT_ATTACK_BONUS_ID, "Ability attack bonus", (double) (attr.getBaseValue() * this.abilities.getValue(CompanionBatAbility.INCREASED_ATTACK) / 100), EntityAttributeModifier.Operation.ADDITION));
		}
		if (this.abilities.has(CompanionBatAbility.INCREASED_SPEED)) {
			EntityAttributeInstance attr = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
			if (!firstTime) attr.removeModifier(BAT_SPEED_BONUS_ID);
			attr.addTemporaryModifier(new EntityAttributeModifier(BAT_SPEED_BONUS_ID, "Ability speed bonus", (double) (attr.getBaseValue() * this.abilities.getValue(CompanionBatAbility.INCREASED_SPEED) / 100), EntityAttributeModifier.Operation.ADDITION));
		}
		if (this.abilities.has(CompanionBatAbility.FIRE_RESISTANCE)) {
			this.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 1000000000, 1, false, false));
		}
		if (this.abilities.has(CompanionBatAbility.TELEPORT)) {
			this.hasTeleport = true;
		}
		if (this.abilities.has(CompanionBatAbility.ADVENTURER_AURA)) {
			this.hasAdventurerAura = true;
		}
		if (this.abilities.has(CompanionBatAbility.LOOTING)) {
			ItemStack stack = new ItemStack(Items.STICK);
			stack.addEnchantment(Enchantments.LOOTING, this.abilities.getValue(CompanionBatAbility.LOOTING));
			this.equipStack(EquipmentSlot.MAINHAND, stack);
		}
	}

	private boolean teleportTo(Entity entity) {
		Direction looking = entity.getHorizontalFacing().getOpposite();
		return this.teleportTo(entity.getX() + looking.getOffsetX() * 0.5, entity.getEyeY(), entity.getZ() + looking.getOffsetZ() * 0.5);
	}

	private boolean teleportTo(double x, double y, double z) {
		BlockPos.Mutable mutable = new BlockPos.Mutable(x, y, z);

		while (mutable.getY() > 0 && !this.world.getBlockState(mutable).getMaterial().blocksMovement()) {
			mutable.move(Direction.DOWN);
		}

		BlockState blockState = this.world.getBlockState(mutable);
		boolean bl = blockState.getMaterial().blocksMovement();
		if (bl) {
			boolean bl3 = this.teleport(x, y, z, true);
			if (bl3 && !this.isSilent()) {
				this.world.playSound((PlayerEntity) null, this.prevX, this.prevY, this.prevZ, SoundEvents.ENTITY_ENDERMAN_TELEPORT, this.getSoundCategory(), 0.3F, this.getSoundPitch() + 1.0F);
			}
			return bl3;
		} else {
			return false;
		}
	}

	private void tryLevelUp() {
		if (CompanionBatLevels.LEVELS.length > this.level + 1 && CompanionBatLevels.LEVELS[this.level + 1].totalExpNeeded <= this.exp) {
			this.level++;
			this.notifyLevelUp(this.level);
			this.setLevelAttributes(this.level);
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

	private void setLevel(int exp) {
		this.exp = exp;
		this.level = CompanionBatLevels.getLevelByExp((int) this.exp);
	}

	protected void setLevelAttributes(int level) {
		this.getAttributes().getCustomInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(getLevelHealth(level));
		this.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(getLevelAttack(level));
		this.getAttributes().getCustomInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(getLevelSpeed(level));
	}

	protected void notifyLevelUp(int level) {
		if (level > 0) {
			MutableText message = new TranslatableText("entity.companion_bats.bat.level_up", this.hasCustomName() ? this.getCustomName() : new TranslatableText("entity.companion_bats.bat.your_bat"), level + 1).append("\n");
			if (CompanionBatLevels.LEVELS[level].healthBonus > CompanionBatLevels.LEVELS[level - 1].healthBonus) {
				message.append(new LiteralText("+").formatted(Formatting.GOLD)).append(" ");
				message.append(new TranslatableText("entity.companion_bats.bat.level_up_health", (int) (CompanionBatLevels.LEVELS[level].healthBonus - CompanionBatLevels.LEVELS[level - 1].healthBonus))).append(" ");
			}
			if (CompanionBatLevels.LEVELS[level].attackBonus > CompanionBatLevels.LEVELS[level - 1].attackBonus) {
				message.append(new LiteralText("+").formatted(Formatting.GOLD)).append(" ");
				message.append(new TranslatableText("entity.companion_bats.bat.level_up_attack", (int) (CompanionBatLevels.LEVELS[level].attackBonus - CompanionBatLevels.LEVELS[level - 1].attackBonus))).append(" ");
			}
			if (CompanionBatLevels.LEVELS[level].speedBonus > CompanionBatLevels.LEVELS[level - 1].speedBonus) {
				message.append(new LiteralText("+").formatted(Formatting.GOLD)).append(" ");
				message.append(new TranslatableText("entity.companion_bats.bat.level_up_speed", Math.round(100 - ((BASE_SPEED + CompanionBatLevels.LEVELS[level - 1].speedBonus) / (BASE_SPEED + CompanionBatLevels.LEVELS[level].speedBonus) * 100)))).append(" ");
			}
			((PlayerEntity) this.getOwner()).sendMessage(message, false);
		}
	}

	protected void notifyClassLevelUp(int classLevel, CompanionBatClassLevel[] classLevels) {
		MutableText message = new TranslatableText("entity.companion_bats.bat.class_level_up", this.hasCustomName() ? this.getCustomName() : new TranslatableText("entity.companion_bats.bat.your_bat"), this.currentClass.toString(), classLevel + 1);
		if (classLevels[classLevel].ability != null){
			message.append("\n");
			String obtainedOrLevelUp = "ability_obtained";
			if (classLevels[classLevel].permanent){
				message.append(new TranslatableText("entity.companion_bats.bat.permanent_ability").formatted(Formatting.LIGHT_PURPLE)).append(" ");
			} else {
				message.append(new TranslatableText("entity.companion_bats.bat.ability").formatted(Formatting.GREEN)).append(" ");
				if (this.abilities.has(classLevels[classLevel].ability)){
					obtainedOrLevelUp = "ability_level_up";
				}
			}
			message.append(new TranslatableText("entity.companion_bats.bat."+obtainedOrLevelUp, classLevels[classLevel].ability.toTranslatedText()));
		}
		((PlayerEntity) this.getOwner()).sendMessage(message, false);
	}

	private void writeExpToTag(CompoundTag entityData) {
		entityData.putInt("exp", this.getExp());
		for (Map.Entry<CompanionBatClass, Integer> entry : this.classesExp.entrySet()) {
			if (entry.getValue() > 0) {
				entityData.putInt(entry.getKey().getExpTagName(), entry.getValue());
			}
		}
		if (this.currentClass != null) {
			entityData.putInt(this.currentClass.getExpTagName(), this.getClassExp());
		}
	}

	private void writePotionTicks(CompoundTag entityData) {
		entityData.putInt("emergencyPotionTicks", this.emergencyPotionTicks);
		entityData.putInt("effectPotionTicks", this.effectPotionTicks);
	}

	private void setPotionTicks(CompoundTag entityData) {
		if (entityData.contains("emergencyPotionTicks")){
			this.emergencyPotionTicks = Math.max(60, entityData.getInt("emergencyPotionTicks"));
			this.effectPotionTicks = Math.max(60, entityData.getInt("effectPotionTicks"));
		} else {
			this.emergencyPotionTicks = EMERGENCY_POTION_TICKS;
			this.effectPotionTicks = EFFECT_POTION_TICKS;
		}
	}

	protected ItemStack toItem() {
		ItemStack batItemStack = new ItemStack(CompanionBats.BAT_ITEM);
		if (this.hasCustomName()) {
			batItemStack.setCustomName(this.getCustomName());
		}
		// Set companion bat item durability realtive to the bat health
		float percentage = 1 - (this.getHealth() / this.getMaxHealth());
		batItemStack.setDamage(Math.round(percentage * batItemStack.getMaxDamage()));
		CompoundTag entityData = CompanionBatItem.createEntityData(batItemStack);
		entityData.putFloat("health", this.getHealth());
		this.writeExpToTag(entityData);
		entityData.put("accessory", this.getAccessory().toTag(new CompoundTag()));
		entityData.put("armor", this.getArmorType().toTag(new CompoundTag()));
		entityData.put("bundle", this.getBundle().toTag(new CompoundTag()));
		this.writePotionTicks(entityData);
		return batItemStack;
	}

	public void fromItem(PlayerEntity owner, CompoundTag entityData) {
		this.setOwner(owner);
		this.setLevel(entityData.getInt("exp"));
		this.setLevelAttributes(this.level);
		this.equipAccessory(ItemStack.fromTag(entityData.getCompound("accessory")));
		this.equipArmor(ItemStack.fromTag(entityData.getCompound("armor")));
		this.equipBundle(ItemStack.fromTag(entityData.getCompound("bundle")));
		this.setHealth(entityData.getFloat("health"));
		this.setAccessoryAbility();
		this.setBatClass();
		this.setClasses(entityData);
		this.abilities.setFromNbt(entityData);
		this.setAbilitiesEffects(true);
		this.setPotionTicks(entityData);
	}

	private void equipAccessory(ItemStack stack) {
		this.equipStack(EquipmentSlot.HEAD, stack);
	}

	private void equipArmor(ItemStack stack) {
		this.equipStack(EquipmentSlot.CHEST, stack);
		this.setEquipmentDropChance(EquipmentSlot.CHEST, 0.0F);
		if (!this.world.isClient) {
			this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).removeModifier(BAT_EQUIPMENT_ARMOR_BONUS_ID);
			if (stack.getItem() instanceof CompanionBatArmorItem) {
				CompanionBatArmorItem armor = (CompanionBatArmorItem) stack.getItem();
				this.currentClass = armor.getBatClass();
				int armorToAdd = armor.getProtectionAmount();
				if (armorToAdd != 0) {
					this.getAttributeInstance(EntityAttributes.GENERIC_ARMOR).addTemporaryModifier(new EntityAttributeModifier(BAT_EQUIPMENT_ARMOR_BONUS_ID, "Equipment armor bonus", (double) armorToAdd, EntityAttributeModifier.Operation.ADDITION));
				}
			}
		}
	}

	private void equipBundle(ItemStack stack) {
		this.equipStack(EquipmentSlot.FEET, stack);
		this.setEquipmentDropChance(EquipmentSlot.FEET, 0.0F);
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

	public boolean canEat(ItemStack stack){
		if (stack.getItem() == CompanionBats.EXPERIENCE_PIE){
			if (this.currentClass != null) {
				CompanionBatClassLevel[] classLevels = CompanionBatLevels.CLASS_LEVELS.get(this.currentClass);
				if (this.classExp < classLevels[classLevels.length - 1].totalExpNeeded) return true;
			}
			return this.isInjured() || (this.exp < CompanionBatLevels.LEVELS[CompanionBatLevels.LEVELS.length - 1].totalExpNeeded);
		} else {
			return this.isInjured() && IS_FOOD_ITEM.test(stack);
		}
	}

	public static int getClassLevelByTag(CompoundTag entityData, CompanionBatClass batClass) {
		int exp = entityData.getInt(batClass.getExpTagName());
		CompanionBatClassLevel[] classLevels = CompanionBatLevels.CLASS_LEVELS.get(batClass);
		for (int i = classLevels.length - 1; i >= 0; i--) {
			if (classLevels[i].totalExpNeeded <= exp) {
				return i;
			}
		}
		return classLevels.length - 1;
	}

	public static void setDefaultEntityData(CompoundTag tag) {
		tag.putFloat("health", BASE_HEALTH);
		tag.putInt("exp", 0);
	}

	static {
		BAT_FLAGS = DataTracker.registerData(CompanionBatEntity.class, TrackedDataHandlerRegistry.BYTE);
		IS_FOOD_ITEM = (itemStack) -> itemStack.getItem() == Items.PUMPKIN_PIE || itemStack.getItem() == CompanionBats.EXPERIENCE_PIE;
		IS_FOOD_ITEM_ENTITY = (itemEntity) -> IS_FOOD_ITEM.test(itemEntity.getStack());
	}
}