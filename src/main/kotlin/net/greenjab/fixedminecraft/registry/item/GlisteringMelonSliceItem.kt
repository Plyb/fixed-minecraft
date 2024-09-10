package net.greenjab.fixedminecraft.registry.item

import net.greenjab.fixedminecraft.StatusEffects.StatusRegistry.INSOMNIA
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.world.World

class GlisteringMelonSliceItem(settings: Settings?) : Item(settings) {
    override fun finishUsing(stack: ItemStack, world: World, user: LivingEntity): ItemStack {
        super.finishUsing(stack, world, user)
        if (user is ServerPlayerEntity) {
            // Criteria.CONSUME_ITEM.trigger(serverPlayerEntity, stack);
            // serverPlayerEntity.incrementStat(Stats.USED.getOrCreateStat(this));
            //if (!user.hasStatusEffect(INSOMNIA)) {

            //}
        }

        if (!world.isClient) {
            user.health+=2;
        }

        return stack
    }

    /*public int getMaxUseTime(ItemStack stack) {
		return 40;
	}

	public UseAction getUseAction(ItemStack stack) {
		return UseAction.DRINK;
	}

	public SoundEvent getDrinkSound() {
		return SoundEvents.ITEM_HONEY_BOTTLE_DRINK;
	}

	public SoundEvent getEatSound() {
		return SoundEvents.ITEM_HONEY_BOTTLE_DRINK;
	}

	public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
		return ItemUsage.consumeHeldItem(world, user, hand);
	}*/
}