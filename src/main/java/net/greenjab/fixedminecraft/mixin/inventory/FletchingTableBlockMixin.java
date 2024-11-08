package net.greenjab.fixedminecraft.mixin.inventory;

import net.greenjab.fixedminecraft.registry.screen.FletchingScreenHandler;
import net.minecraft.block.BlockState;
import net.minecraft.block.FletchingTableBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.screen.SmithingScreenHandler;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FletchingTableBlock.class)
public class FletchingTableBlockMixin {

    @Unique
    public NamedScreenHandlerFactory createScreenHandlerFactory(BlockState state, World world, BlockPos pos) {
        return new SimpleNamedScreenHandlerFactory((syncId, inventory, player) -> {
            return new FletchingScreenHandler(syncId, inventory, ScreenHandlerContext.create(world, pos));
        }, Text.of("Arrow Crafting"));//Text.translatable("container.upgrade"));
    }

   @Inject(method = "onUse", at = @At(value = "HEAD"), cancellable = true)
    private void fletchingScreen(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit,
                                 CallbackInfoReturnable<ActionResult> cir) {
       if (world.isClient) {
           cir.setReturnValue(ActionResult.SUCCESS);
       } else {
           player.openHandledScreen(createScreenHandlerFactory(state, world, pos));
           player.incrementStat(Stats.INTERACT_WITH_SMITHING_TABLE);
           cir.setReturnValue( ActionResult.CONSUME);
       }
       cir.cancel();
   }
}