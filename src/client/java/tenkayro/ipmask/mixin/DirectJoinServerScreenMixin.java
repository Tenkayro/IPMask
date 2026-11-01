package tenkayro.ipmask.mixin;

import tenkayro.ipmask.client.AddressMaskable;
import tenkayro.ipmask.client.EyeToggleButton;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.DirectJoinServerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DirectJoinServerScreen.class)
public abstract class DirectJoinServerScreenMixin {
	@Shadow
	private EditBox ipEdit;

	@Unique
	private boolean ipmask$visible;

	@Inject(method = "init", at = @At("TAIL"))
	private void ipmask$addAddressToggle(CallbackInfo ci) {
		this.ipEdit.setWidth(176);
		((AddressMaskable)this.ipEdit).ipmask$setHidden(!this.ipmask$visible);
		((ScreenInvoker)this).ipmask$addRenderableWidget(new EyeToggleButton(this.ipEdit.getX() + 180, this.ipEdit.getY(), () -> this.ipmask$visible, () -> {
			this.ipmask$visible = !this.ipmask$visible;
			((AddressMaskable)this.ipEdit).ipmask$setHidden(!this.ipmask$visible);
		}));
	}
}
