package net.greenjab.fixedminecraft.mixin.raid;

import com.llamalad7.mixinextras.sugar.Local;
import net.greenjab.fixedminecraft.registry.GameruleRegistry;
import net.greenjab.fixedminecraft.registry.ItemRegistry;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.event.GameEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import java.util.Optional;
import java.util.Set;

@Mixin(LivingEntity.class)
public class LivingEntityMixin  {

    //Need to "use" totem
    @Redirect(method = "tryUseTotem", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"))
    private boolean requireUsingTotem(ItemStack itemStack2, Item item, DamageSource source) {
        LivingEntity LE = (LivingEntity)(Object)this;
        if (LE.getWorld().getGameRules().getBoolean(GameruleRegistry.INSTANCE.getRequire_Totem_Use())) {
            return ((itemStack2.isOf(item)||itemStack2.isOf(ItemRegistry.INSTANCE.getECHO_TOTEM())) && ((LivingEntity) (Object) this).isUsingItem());
        } else {
            return itemStack2.isOf(item)||itemStack2.isOf(ItemRegistry.INSTANCE.getECHO_TOTEM());
        }
    }

    @Inject(method = "tryUseTotem", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;decrement(I)V", shift = At.Shift.AFTER))
    private void brokenTotem(DamageSource source, CallbackInfoReturnable<Boolean> cir, @Local Hand hand) {
        ItemStack broken = new ItemStack(ItemRegistry.INSTANCE.getBROKEN_TOTEM());
        if ((LivingEntity)(Object)this instanceof PlayerEntity) {
            PlayerEntity user = (PlayerEntity) (Object) this;
            ItemStack i = user.getStackInHand(hand);
            if (i.isEmpty()) {
                user.setStackInHand(hand, broken);
            } else {
                if (!user.getInventory().insertStack(broken)) {
                    user.dropItem(broken, false);
                }
            }
        }
    }

    @Inject(method = "tryUseTotem", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/LivingEntity;getWorld()Lnet/minecraft/world/World;", shift = At.Shift.AFTER))
    private void echoTeleport(DamageSource source, CallbackInfoReturnable<Boolean> cir, @Local ItemStack itemStack) {
        LivingEntity LE = (LivingEntity)(Object)this;
        if (LE instanceof ServerPlayerEntity SPE) {
            if (itemStack.isOf(ItemRegistry.INSTANCE.getECHO_TOTEM())) {
                goToSpawn(SPE);
            }
        }
    }
    @Unique
    private void goToSpawn(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();
        LivingEntity LE = (LivingEntity)(Object)this;
        BlockPos blockPos = player.getSpawnPointPosition();
        ServerWorld serverWorld = player.server.getWorld(player.getSpawnPointDimension());
        Optional<Vec3d> optional;
        if (serverWorld != null && blockPos != null) {
            optional = PlayerEntity.findRespawnPosition(serverWorld, blockPos, player.getYaw(), false, true);
        } else {
            optional = Optional.empty();
        }

        ServerWorld serverWorld2 = serverWorld != null && optional.isPresent() ? serverWorld : player.server.getOverworld();
        Vec3d pos = serverWorld2.getSpawnPos().toCenterPos();

        if (optional.isPresent()) {
            pos = optional.get();
        } else if (blockPos != null) {
            player.networkHandler.sendPacket(new GameStateChangeS2CPacket(GameStateChangeS2CPacket.NO_RESPAWN_BLOCK, GameStateChangeS2CPacket.DEMO_OPEN_SCREEN));
        }
        Vec3d vec3d = player.getPos();
        if (LE.teleport(serverWorld2, pos.getX(),pos.getY(),pos.getZ(), Set.of(),player.getYaw(),player.getPitch())) {
            while (!serverWorld2.isSpaceEmpty(player) && player.getY() < (double) serverWorld2.getTopY()) {
                player.setPosition(player.getX(), player.getY() + 1.0, player.getZ());
            }
            world.emitGameEvent(GameEvent.TELEPORT, vec3d, GameEvent.Emitter.of(player));
            SoundEvent soundEvent = SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT;
            SoundCategory soundCategory = SoundCategory.PLAYERS;

            world.playSound(player, vec3d.getX(), vec3d.getY(), vec3d.getZ(), soundEvent, soundCategory);
            player.onLanding();
        }
    }
}
