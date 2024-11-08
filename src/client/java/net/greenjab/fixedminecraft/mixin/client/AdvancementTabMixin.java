package net.greenjab.fixedminecraft.mixin.client;

import net.minecraft.client.gui.screen.advancement.AdvancementTab;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;


@Mixin(AdvancementTab.class)
public class AdvancementTabMixin {
    int newPAGE_WIDTH = 423;
    int newPAGE_HEIGHT = 218;
    @ModifyConstant(method = "render", constant = @Constant(intValue = 117))
    private int largerScreenX1(int constant) {return newPAGE_WIDTH/2;}
    @ModifyConstant(method = "render", constant = @Constant(intValue = 56))
    private int largerScreenY1(int constant) {return newPAGE_HEIGHT/2;}

    @ModifyConstant(method = "render", constant = @Constant(intValue = 234))
    private int largerScreenX2(int constant) {return newPAGE_WIDTH;}
    @ModifyConstant(method = "render", constant = @Constant(intValue = 113))
    private int largerScreenY2(int constant) {return newPAGE_HEIGHT;}

    @ModifyConstant(method = "drawWidgetTooltip", constant = @Constant(intValue = 234))
    private int largerScreenX3(int constant) {return newPAGE_WIDTH;}
    @ModifyConstant(method = "drawWidgetTooltip", constant = @Constant(intValue = 113))
    private int largerScreenY3(int constant) {return newPAGE_HEIGHT;}

    @ModifyConstant(method = "move", constant = @Constant(intValue = 234))
    private int largerScreenX4(int constant) {return newPAGE_WIDTH;}
    @ModifyConstant(method = "move", constant = @Constant(intValue = 113))
    private int largerScreenY4(int constant) {return newPAGE_HEIGHT;}

    @ModifyConstant(method = "render", constant = @Constant(intValue = 15))
    private int largerScreenX5(int constant) {return 30;}
    @ModifyConstant(method = "render", constant = @Constant(intValue = 8))
    private int largerScreenY5(int constant) {return 16;}

}