package net.greenjab.fixedminecraft.mixin.villager;

import com.mojang.datafixers.util.Pair;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.village.Merchant;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Iterator;

@Mixin(MerchantScreenHandler.class)
public abstract class MerchantScreenHandlerMixin extends ScreenHandler implements InventoryChangedListener {

    @Shadow
    protected abstract void playYesSound();
    @Unique
    private static final Identifier[] EMPTY_ARMOR_SLOT_TEXTURES;
    @Unique
    private static final EquipmentSlot[] EQUIPMENT_SLOT_ORDER;

    protected MerchantScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId) {
        super(type, syncId);
    }

    @Inject(method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/village/Merchant;)V", at = @At("TAIL"))
    private void armorSlots(int syncId, PlayerInventory playerInventory, Merchant merchant, CallbackInfo ci){
        double d = 10f;
        Vec3d vec3d = merchant.getCustomer().getClientCameraPosVec(1.0f);
        double e = d*d;

        Vec3d vec3d2 = merchant.getCustomer().getRotationVec(1.0F);
        Vec3d vec3d3 = vec3d.add(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d);
        Box box = merchant.getCustomer().getBoundingBox().stretch(vec3d2.multiply(d)).expand(1.0, 1.0, 1.0);
        EntityHitResult entityHitResult = ProjectileUtil.raycast(merchant.getCustomer(), vec3d, vec3d3, box, (entityx) -> {
            return !entityx.isSpectator() && entityx.canHit();
        }, e);

        if (entityHitResult != null) {
            if (entityHitResult.getEntity() instanceof VillagerEntity VE) {
                Inventory inventory = Inv(VE.getArmorItems());
                for (int i = 5-VE.getVillagerData().getLevel(); i < 4; i++) {
                    final EquipmentSlot equipmentSlot = EQUIPMENT_SLOT_ORDER[i];
                    int finalI = i;
                    this.addSlot(new Slot(inventory, 3 - i, 252, 8 + i * 18) {

                    public void setStack(ItemStack stack, ItemStack previousStack) {
                            merchant.getCustomer().onEquipStack(equipmentSlot, stack, previousStack);
                            super.setStack(stack, previousStack);
                            VE.equipStack(equipmentSlot, inventory.getStack(3-finalI));
                        }

                        public int getMaxItemCount() {
                            return 1;
                        }

                        public boolean canInsert(ItemStack stack) {
                            if (stack != null) {
                                if (equipmentSlot == EquipmentSlot.HEAD) {
                                    return
                                    stack.getItem() == Items.CARVED_PUMPKIN ||
                                    stack.getItem() == Items.DRAGON_HEAD ||
                                    stack.getItem() == Items.PLAYER_HEAD ||
                                    stack.isIn(ItemTags.BANNERS);
                                }
                                EquipmentSlot slot = MobEntity.getPreferredEquipmentSlot(stack);
                                return equipmentSlot == slot && stack.getItem() instanceof DyeableArmorItem;
                            }
                            return false;
                        }

                        public boolean canTakeItems(PlayerEntity playerEntity) {
                            ItemStack itemStack = this.getStack();
                            if (itemStack == null) return false;
                            return (itemStack.isEmpty() || playerEntity.isCreative() ||
                                    !EnchantmentHelper.hasBindingCurse(itemStack)) && super.canTakeItems(playerEntity);
                        }

                        public Pair<Identifier, Identifier> getBackgroundSprite() {
                            return Pair.of(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE, EMPTY_ARMOR_SLOT_TEXTURES[equipmentSlot.getEntitySlotId()]);
                        }
                    });
                }
            }
        }

    }

    static {
        EMPTY_ARMOR_SLOT_TEXTURES = new Identifier[]{PlayerScreenHandler.EMPTY_BOOTS_SLOT_TEXTURE, PlayerScreenHandler.EMPTY_LEGGINGS_SLOT_TEXTURE, PlayerScreenHandler.EMPTY_CHESTPLATE_SLOT_TEXTURE, PlayerScreenHandler.EMPTY_HELMET_SLOT_TEXTURE};
        EQUIPMENT_SLOT_ORDER = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    @Unique
    Inventory Inv(Iterable<ItemStack> armorItems) {
        SimpleInventory items= new SimpleInventory(4);
        Iterator<ItemStack> iter = armorItems.iterator();
        int i = 0;
        while (iter.hasNext()) {
            items.heldStacks.set(i++, iter.next());
        }
        return items;
    }

    @Inject(method = "quickMove", at = @At("HEAD"), cancellable = true)
    private void quickMove(PlayerEntity player, int slot, CallbackInfoReturnable<ItemStack> cir) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot2 = this.slots.get(slot);
        if (slot2.hasStack()) {
            ItemStack itemStack2 = slot2.getStack();
            itemStack = itemStack2.copy();
            if (slot <3 || slot > 38) {
                if (this.insertItem(itemStack2, 3, 39, true)) {
                    if (slot == 2) {
                        slot2.onQuickTransfer(itemStack2, itemStack);
                        this.playYesSound();
                    }
                } else {
                    cir.setReturnValue(ItemStack.EMPTY);
                    return;
                }
            } else {
                if (itemStack2.getItem() instanceof DyeableArmorItem) {
                    if (this.slots.size()>38) {
                        if (!this.insertItem(itemStack2, 39, this.slots.size(), false)) {
                            cir.setReturnValue(ItemStack.EMPTY);
                            return;
                        }
                    }
                }
                if (!this.insertItem(itemStack2, 0, 2, false)) {
                    if (slot < 30) {
                        if (!this.insertItem(itemStack2, 30, 39, false)) {
                            cir.setReturnValue( ItemStack.EMPTY);
                            return;
                        }
                    } else if (!this.insertItem(itemStack2, 3, 30, false)) {
                        cir.setReturnValue( ItemStack.EMPTY);
                        return;
                    }
                }
            }

            if (itemStack2.isEmpty()) {
                slot2.setStack(ItemStack.EMPTY);
            } else {
                slot2.markDirty();
            }

            if (itemStack2.getCount() == itemStack.getCount()) {
                cir.setReturnValue( ItemStack.EMPTY);
                return;
            }
            slot2.onTakeItem(player, itemStack2);
        }
        cir.setReturnValue( itemStack);
    }
}
