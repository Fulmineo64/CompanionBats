package dev.fulmineo.companion_bats.entity;

import java.util.Iterator;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;

import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;

import dev.fulmineo.companion_bats.CompanionBats;
import dev.fulmineo.companion_bats.entity.ai.control.CompanionBatMoveControl;
import dev.fulmineo.companion_bats.entity.ai.goal.CompanionBatFollowOwnerGoal;
import dev.fulmineo.companion_bats.entity.ai.goal.CompanionBatPickUpItemGoal;
import dev.fulmineo.companion_bats.item.CompanionBatItem;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.AttackWithOwnerGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.PounceAtTargetGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.TrackOwnerAttackerGoal;
import net.minecraft.entity.ai.pathing.BirdNavigation;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.MutableText;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class CompanionBatEntity extends TameableEntity {
    private static final TrackedData<Byte> BAT_FLAGS;

    public static final CompanionBatLevel[] LEVELS;
    public static final Predicate<ItemEntity> IS_FOOD_ITEM_ENTITY;

    protected int exp = 0;
    protected int level = -1;

    public CompanionBatEntity(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
        this.moveControl = new CompanionBatMoveControl(this, 10, false);
        this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, -1.0F);
        this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, -1.0F);
        this.setRoosting(false);
        this.setSitting(false);
    }

    protected void initDataTracker() {
        super.initDataTracker();
        this.dataTracker.startTracking(BAT_FLAGS, (byte)0);
    }

    protected void initGoals() {
        this.goalSelector.add(1, new PounceAtTargetGoal(this, 0.4F));
        this.goalSelector.add(2, new MeleeAttackGoal(this, 1.0D, true));
        this.goalSelector.add(3, new CompanionBatPickUpItemGoal(this, 1.0D, 2.0F));
        this.goalSelector.add(4, new CompanionBatFollowOwnerGoal(this, 1.0D, 5.0F, 2.0F));
        this.targetSelector.add(1, new TrackOwnerAttackerGoal(this));
        this.targetSelector.add(2, new AttackWithOwnerGoal(this));
        this.targetSelector.add(3, (new RevengeGoal(this, new Class[0])).setGroupRevenge());
    }

    /*private void saveItemsToTag(CompoundTag tag){
        if (this.items != null) {
            if (!this.items.getStack(0).isEmpty()) {
                tag.put("EquipmentItem", this.items.getStack(0).toTag(new CompoundTag()));
            }
            if (!this.items.getStack(1).isEmpty()) {
                tag.put("BundleItem", this.items.getStack(1).toTag(new CompoundTag()));
            }
        }
    }

    private void loadItemsFromTag(CompoundTag tag){
        if (tag.contains("SaddleItem", 10)) {
            ItemStack itemStack = ItemStack.fromTag(tag.getCompound("EquipmentItem"));
            // TODO: Check for equipment items
            if (!itemStack.isEmpty()) {
               this.items.setStack(0, itemStack);
            }
        }
        if (tag.contains("BundleItem", 10)) {
            ItemStack itemStack2 = ItemStack.fromTag(tag.getCompound("BundleItem"));
            if (!itemStack2.isEmpty()) {
               this.items.setStack(1, itemStack2);
            }
        }
    }*/

    public void writeCustomDataToTag(CompoundTag tag) {
        super.writeCustomDataToTag(tag);
        tag.putInt("exp", this.getExp());
    }
  
    public void readCustomDataFromTag(CompoundTag tag) {
        super.readCustomDataFromTag(tag);
        this.setExp(tag.getInt("exp"));
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
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, LEVELS[0].health).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, LEVELS[0].attack).add(EntityAttributes.GENERIC_FLYING_SPEED, LEVELS[0].speed).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.4D);
    }
    
    /**
    * Returns whether this bat is hanging upside-down under a block.
    */
    public boolean isRoosting() {
        return ((Byte)this.dataTracker.get(BAT_FLAGS) & 1) != 0;
    }
  
    public void setRoosting(boolean roosting) {
        byte b = (Byte)this.dataTracker.get(BAT_FLAGS);
        if (roosting) {
           this.dataTracker.set(BAT_FLAGS, (byte)(b | 1));
        } else {
           this.dataTracker.set(BAT_FLAGS, (byte)(b & -2));
        }
    }
  
    public void tick() {
        super.tick();
        if (this.isRoosting()) {
            this.setVelocity(Vec3d.ZERO);
            this.setPos(this.getX(), (double)MathHelper.floor(this.getY()) + 1.0D - (double)this.getHeight(), this.getZ());
        } else {
            this.setVelocity(this.getVelocity().multiply(1.0D, 0.6D, 1.0D));
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
            if (!this.world.isClient && this.isRoosting()) {
                this.setRoosting(false);
            }
            return super.damage(source, amount);
        }
    }

    public boolean canAttackWithOwner(LivingEntity target, LivingEntity owner) {
        if (!(target instanceof CreeperEntity)) {
            if (target instanceof WolfEntity) {
                WolfEntity wolfEntity = (WolfEntity)target;
                return !wolfEntity.isTamed() || wolfEntity.getOwner() != owner;
            } else if (target instanceof CompanionBatEntity){
                CompanionBatEntity companionBatEntity = (CompanionBatEntity)target;
                return companionBatEntity.getOwner() != owner;
            } else if (target instanceof PlayerEntity && owner instanceof PlayerEntity && !((PlayerEntity)owner).shouldDamagePlayer((PlayerEntity)target)) {
                return false;
            } else if (target instanceof HorseBaseEntity && ((HorseBaseEntity)target).isTame()) {
                return false;
            } else {
                return !(target instanceof TameableEntity) || !((TameableEntity)target).isTamed();
            }
        } else {
           return false;
        }
    }

    @Override
    protected void mobTick() {
    }

    /*

    public void tickMovement() {
        super.mobTick();
    }

    */

    public CompanionBatEntity createChild(ServerWorld serverWorld, PassiveEntity passiveEntity) {
        return null;
    }

    public boolean tryAttack(Entity target) {
        float targetHealth = target instanceof LivingEntity ? ((LivingEntity)target).getHealth() : 0;
        boolean bl = target.damage(DamageSource.mob(this), (float)((int)this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE)));
        if (bl) {
            this.dealDamage(this, target);
            float damageDealt = targetHealth - (target instanceof LivingEntity ? ((LivingEntity)target).getHealth() : 0);
            CompanionBats.log(Level.INFO,"damageDealt "+damageDealt+" "+targetHealth+"/"+((LivingEntity)target).getHealth());
            if (damageDealt > 0){
                this.gainExp();
                this.applyLifesteal(damageDealt);
            }
        }
        return bl;
    }

    @Override
    public ActionResult interactMob(PlayerEntity player, Hand hand) {
        if (player.isSneaking()){
            // Apri l'inventario
        } 
        ItemStack itemStack = player.getStackInHand(hand);
        if (this.world.isClient){
            return itemStack.isOf(Items.PUMPKIN_PIE) /*&& !this.isTamed()*/ ? ActionResult.CONSUME : ActionResult.PASS;
        } else {
            return this.healWithItem(itemStack);
        }
    }

    public ActionResult healWithItem(ItemStack stack){
        if (this.getHealth() == this.getMaxHealth()) return ActionResult.PASS;
        float amount = getItemHealAmount(stack);
        if (amount > 0){
            this.heal(amount);
            return ActionResult.SUCCESS;
        }
        return ActionResult.PASS;
    }

    public static float getItemHealAmount(ItemStack stack){
        if (stack.isOf(Items.PUMPKIN_PIE)){
            return 6.0F;
        }
        return 0;
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

    public boolean returnToPlayerInventory() {
        ServerPlayerEntity player = (ServerPlayerEntity)this.getOwner();
        if (player != null){
            PlayerInventory inventory = player.getInventory();
            ImmutableList<DefaultedList<ItemStack>> mainAndOffhand = ImmutableList.of(inventory.main, inventory.offHand);
            Iterator<DefaultedList<ItemStack>> iterator = mainAndOffhand.iterator();
            while(iterator.hasNext()) {
                DefaultedList<ItemStack> defaultedList = (DefaultedList<ItemStack>)iterator.next();
                for(int i = 0; i < defaultedList.size(); ++i) {
                    if (defaultedList.get(i).getItem() == CompanionBats.BAT_FLUTE_ITEM && defaultedList.get(i).getTag().getUuid("entityUuid").equals(this.getUuid())) {
                        // Get item in hand
                        this.discard();
                        defaultedList.set(i, this.toItem());
                        world.playSound(null, player.getBlockPos(), SoundEvents.ENTITY_SLIME_ATTACK, SoundCategory.HOSTILE, 1F, 1F);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public int getExp(){
        return this.exp;
    }

    protected void setExp(int exp){
        this.exp = exp;
        this.tryLevelUp();
    }

    protected void addExp(int expToAdd){
        this.setExp(this.exp + expToAdd);
    }

    protected void gainExp(){
        this.addExp(1 * 100);
        CompanionBats.log(Level.INFO, "total exp "+this.getExp());
    }

    protected void applyLifesteal(float damageDealt){
        /*CompanionBatLevel level = LEVELS[this.level];
        if (level.lifesteal > 0){
            this.heal(damageDealt * level.lifesteal);
            CompanionBats.info("heal with lifesteal"+(damageDealt * level.lifesteal));
        }*/
    }

    protected void tryLevelUp(){
        if (LEVELS.length > this.level + 1 && LEVELS[this.level + 1].expNeeded <= this.exp) {
            this.level++;
            this.setLevelAttributes(this.level);
            this.heal(this.getMaxHealth());
            this.notifyLevelUp(this.level);
        }
    }

    protected void setLevelAttributes(int level){
        this.getAttributes().getCustomInstance(EntityAttributes.GENERIC_MAX_HEALTH).setBaseValue(LEVELS[level].health);
        this.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue(LEVELS[level].attack);
        this.getAttributes().getCustomInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).setBaseValue(LEVELS[level].speed);
        CompanionBats.info("Level: "+this.level+" Attributes: maxHealth:"+this.getMaxHealth()+" attack:"+this.getAttributes().getCustomInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).getValue()+" speed:"+this.getAttributes().getCustomInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED).getValue());
    }

    protected void notifyLevelUp(int level){
        if (level > 0){
            MutableText message = new TranslatableText("entity.companion_bats.bat.level_up",level+1).append("\n");
            if (LEVELS[level].health > LEVELS[level-1].health){
                message.append(new TranslatableText("entity.companion_bats.bat.level_up_health", (int)(LEVELS[level].health - LEVELS[level-1].health))).append(" ");
            }
            if (LEVELS[level].attack > LEVELS[level-1].attack){
                message.append(new TranslatableText("entity.companion_bats.bat.level_up_attack", (int)(LEVELS[level].attack - LEVELS[level-1].attack))).append(" ");
            }
            if (LEVELS[level].speed > LEVELS[level-1].speed){
                message.append(new TranslatableText("entity.companion_bats.bat.level_up_speed", Math.round(100 - (LEVELS[level-1].speed / LEVELS[level].speed * 100)))).append(" ");
            }
            ((PlayerEntity)this.getOwner()).sendMessage(message, false);
        }
    }

    protected ItemStack toItem(){
        ItemStack batItemStack = new ItemStack(CompanionBats.BAT_ITEM);
        // Set companion bat item durability realtive to the bat health
        float percentage = 1 - (this.getHealth() / this.getMaxHealth());
        batItemStack.setDamage(Math.round(percentage * batItemStack.getMaxDamage()));
        CompoundTag entityAttributes = CompanionBatItem.getOrCreateEntityAttributes(batItemStack);
        entityAttributes.putFloat("health", this.getHealth());
        entityAttributes.putInt("exp", this.getExp());
        return batItemStack;
    }

    public void fromItem(PlayerEntity owner, CompoundTag entityAttributes, Inventory inventory){
        this.setOwner(owner);
        this.exp = entityAttributes.getInt("exp");
        this.level = findLevelByExp(this.exp);
        this.setLevelAttributes(this.level);
        this.equipBundle(inventory.getStack(0));
        this.equipArmor(inventory.getStack(1));
        this.setHealth(entityAttributes.getFloat("health"));
    }

    public static void loadIntoInventoryFromTag(SimpleInventory inventory, CompoundTag tag){
        // TODO!
    }

    private void equipBundle(ItemStack stack) {
        this.equipStack(EquipmentSlot.FEET, stack);
        this.setEquipmentDropChance(EquipmentSlot.FEET, 0.0F);
    }

    private void equipArmor(ItemStack stack) {
        this.equipStack(EquipmentSlot.CHEST, stack);
        this.setEquipmentDropChance(EquipmentSlot.CHEST, 0.0F);
    }

    public ItemStack getBundle(){
        return this.getEquippedStack(EquipmentSlot.FEET);
    }

    public static int findLevelByExp(int exp) {
        for (int i=LEVELS.length-1; i>=0; i--) { 
            if (LEVELS[i].expNeeded <= exp){
                return i;
            }
        }
        return LEVELS.length-1;
    }

    public static void setDefaultAttributes(CompoundTag tag){
        tag.putFloat("health", LEVELS[0].health);
        tag.putInt("exp",0);
    }

    public static class CompanionBatLevel {
        public int expNeeded;
        public float health;
        public float attack;
        public float speed;

        CompanionBatLevel(int expNeeded, float health, float attack, float speed) {
            this.expNeeded = expNeeded;
            this.health = health;
            this.attack = attack;
            this.speed = speed;
        }
    }

    static {
        BAT_FLAGS = DataTracker.registerData(CompanionBatEntity.class, TrackedDataHandlerRegistry.BYTE);
        LEVELS = new CompanionBatLevel[10];
        LEVELS[0] = new CompanionBatLevel(0, 6.0F, 3.0F, 0.3F);
        LEVELS[1] = new CompanionBatLevel(100, 8.0F, 3.0F, 0.3F);
        LEVELS[2] = new CompanionBatLevel(300, 10.0F, 3.0F, 0.3F);
        LEVELS[3] = new CompanionBatLevel(600, 12.0F, 3.0F, 0.3F);
        LEVELS[4] = new CompanionBatLevel(1000, 12.0F, 4.0F, 0.35F);
        LEVELS[5] = new CompanionBatLevel(1500, 14.0F, 4.0F, 0.35F);
        LEVELS[6] = new CompanionBatLevel(2100, 14.0F, 5.0F, 0.35F);
        LEVELS[7] = new CompanionBatLevel(2800, 16.0F, 5.0F, 0.4F);
        LEVELS[8] = new CompanionBatLevel(3700, 16.0F, 6.0F, 0.4F);
        LEVELS[9] = new CompanionBatLevel(5000, 18.0F, 6.0F, 0.4F);
        IS_FOOD_ITEM_ENTITY = (itemEntity) -> {
            return itemEntity.getStack().isOf(Items.PUMPKIN_PIE);
        };
    }
}
