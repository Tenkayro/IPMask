package tenkayro.ipmask.mixin;

import tenkayro.ipmask.client.AddressListVisibility;
import tenkayro.ipmask.client.EyeToggleButton;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(JoinMultiplayerScreen.class)
public abstract class JoinMultiplayerScreenMixin implements AddressListVisibility {
	@Unique
	private boolean ipmask$visible;

	@Override
	@Unique
	public boolean ipmask$areAddressesVisible() {
		return this.ipmask$visible;
	}

	@Inject(method = "init", at = @At("TAIL"))
	private void ipmask$addListToggle(CallbackInfo ci) {
		((ScreenInvoker)this).ipmask$addRenderableWidget(
			new EyeToggleButton(((Screen)(Object)this).width - 26, 6, () -> this.ipmask$visible, () -> this.ipmask$visible = !this.ipmask$visible)
		);
	}
}
