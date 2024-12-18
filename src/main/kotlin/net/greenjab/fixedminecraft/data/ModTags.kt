package net.greenjab.fixedminecraft.data

import net.greenjab.fixedminecraft.FixedMinecraft
import net.minecraft.block.Block
import net.minecraft.entity.EntityType
import net.minecraft.registry.RegistryKeys
import net.minecraft.registry.tag.TagKey

object ModTags {

    val VEHICLES: TagKey<EntityType<*>> = TagKey.of(RegistryKeys.ENTITY_TYPE, FixedMinecraft.id("vehicles"))
    val ARTHROPODS: TagKey<EntityType<*>> = TagKey.of(RegistryKeys.ENTITY_TYPE, FixedMinecraft.id("arthropods"))
    val ORES: TagKey<Block> = TagKey.of(RegistryKeys.BLOCK, FixedMinecraft.id("ores"))

}
