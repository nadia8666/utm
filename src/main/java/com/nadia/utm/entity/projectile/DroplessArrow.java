package com.nadia.utm.entity.projectile;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class DroplessArrow extends Arrow {
    public DroplessArrow(EntityType<? extends Arrow> entityType, Level level) {
        super(entityType, level);
    }

    public DroplessArrow(Level level, double x, double y, double z, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon) {
        super(level, x, y, z, pickupItemStack, firedFromWeapon);
    }

    public DroplessArrow(Level level, LivingEntity owner, ItemStack pickupItemStack, @Nullable ItemStack firedFromWeapon) {
        super(level, owner, pickupItemStack, firedFromWeapon);
    }

 //   @Override
 //   protected void onHitBlock(BlockHitResult result) {
  //      this.lastState = this.level().getBlockState(result.getBlockPos());
  //      super.onHitBlock(result);
  //      Vec3 vec3 = result.getLocation().subtract(this.getX(), this.getY(), this.getZ());
   //     this.setDeltaMovement(vec3);
   //     ItemStack itemstack = this.getWeaponItem();
   //     Level var5 = this.level();
   //     if (var5 instanceof ServerLevel serverlevel) {
   //         if (itemstack != null) {
  //              this.hitBlockEnchantmentEffects(serverlevel, result, itemstack);
 //           }
 //       }
//
 //       Vec3 vec31 = vec3.normalize().scale((double)0.05F);
 //       this.setPosRaw(this.getX() - vec31.x, this.getY() - vec31.y, this.getZ() - vec31.z);
  //      this.playSound(this.getHitGroundSoundEvent(), 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
 //       this.inGround = true;
  //      this.shakeTime = 7;
 //       this.setCritArrow(false);
 //       this.setPierceLevel((byte)0);
 //       this.setSoundEvent(SoundEvents.ARROW_HIT);
 //       this.resetPiercedEntities();
 //   }

    @Override
    protected ItemStack getDefaultPickupItem() {
        return new ItemStack(Items.AIR);
    }
    @Override
    protected boolean tryPickup(Player player) {

        return false;
    }
}
