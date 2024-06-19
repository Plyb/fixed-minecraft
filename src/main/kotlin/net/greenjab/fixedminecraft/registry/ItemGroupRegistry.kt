package net.greenjab.fixedminecraft.registry

import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.text.Text

@Suppress("MemberVisibilityCanBePrivate")
object ItemGroupRegistry {
    val FIXED = itemGroup(Text.translatable("itemgroup.fixed")) {
        icon { ItemStack(ItemRegistry.DRAGON_FIREWORK_ROCKET) }
        entries { _: ItemGroup.DisplayContext, entries: ItemGroup.Entries ->
            entries.add(ItemRegistry.DRAGON_FIREWORK_ROCKET)
            entries.add(ItemRegistry.MAP_BOOK)
            entries.add(ItemRegistry.NETHERITE_HORSE_ARMOR)
            entries.add(BlockRegistry.NETHERITE_ANVIL)
            entries.add(BlockRegistry.CHIPPED_NETHERITE_ANVIL)
            entries.add(BlockRegistry.DAMAGED_NETHERITE_ANVIL)

            entries.add(BlockRegistry.COPPER_RAIL)
            entries.add(BlockRegistry.EXPOSED_COPPER_RAIL)
            entries.add(BlockRegistry.WEATHERED_COPPER_RAIL)
            entries.add(BlockRegistry.OXIDIZED_COPPER_RAIL)
            entries.add(BlockRegistry.WAXED_COPPER_RAIL)
            entries.add(BlockRegistry.WAXED_EXPOSED_COPPER_RAIL)
            entries.add(BlockRegistry.WAXED_WEATHERED_COPPER_RAIL)
            entries.add(BlockRegistry.WAXED_OXIDIZED_COPPER_RAIL)

            entries.add(BlockRegistry.AZALEA_PLANKS)
            entries.add(BlockRegistry.AZALEA_LOG)
            entries.add(BlockRegistry.STRIPPED_AZALEA_LOG)
            entries.add(BlockRegistry.AZALEA_WOOD)
            entries.add(BlockRegistry.STRIPPED_AZALEA_WOOD)
            entries.add(ItemRegistry.AZALEA_SIGN)
            entries.add(ItemRegistry.AZALEA_HANGING_SIGN)
            entries.add(BlockRegistry.AZALEA_PRESSURE_PLATE)
            entries.add(BlockRegistry.AZALEA_TRAPDOOR)
            entries.add(BlockRegistry.AZALEA_BUTTON)
            entries.add(BlockRegistry.AZALEA_STAIRS)
            entries.add(BlockRegistry.AZALEA_SLAB)
            entries.add(BlockRegistry.AZALEA_FENCE_GATE)
            entries.add(BlockRegistry.AZALEA_FENCE)
            entries.add(BlockRegistry.AZALEA_DOOR)
            entries.add(ItemRegistry.AZALEA_BOAT)
            entries.add(ItemRegistry.AZALEA_CHEST_BOAT)



        }
    }

    fun register() {
        Registries.ITEM_GROUP.register("fixed", FIXED)
    }
}
