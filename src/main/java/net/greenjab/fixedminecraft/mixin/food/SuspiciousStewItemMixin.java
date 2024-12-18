package net.greenjab.fixedminecraft.mixin.food;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SuspiciousStewItem;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SuspiciousStewItem.class)
public class SuspiciousStewItemMixin {

    @Inject(method = "finishUsing", at = @At("TAIL"), cancellable = true)
    private void fixStackedBowlUse(ItemStack stack, World world, LivingEntity user, CallbackInfoReturnable<ItemStack> cir) {
        if (user instanceof ServerPlayerEntity serverPlayerEntity) {
            Criteria.CONSUME_ITEM.trigger(serverPlayerEntity, stack);
            serverPlayerEntity.incrementStat(Stats.USED.getOrCreateStat((SuspiciousStewItem)(Object)this));
        }
        if (stack.isEmpty()) {
            cir.setReturnValue( new ItemStack(Items.BOWL));
        } else {
            if (user instanceof PlayerEntity playerEntity) {
                if (!playerEntity.getAbilities().creativeMode) {
                    ItemStack itemStack2 = new ItemStack(Items.BOWL);
                    if (!playerEntity.getInventory().insertStack(itemStack2)) {
                        playerEntity.dropItem(itemStack2, false);
                    }
                }
            }
            cir.setReturnValue(stack);
        }
    }
}
