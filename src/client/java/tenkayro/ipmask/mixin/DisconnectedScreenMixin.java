package tenkayro.ipmask.mixin;

import tenkayro.ipmask.client.IPMaskClient;
import tenkayro.ipmask.client.IPMaskConfig;
import net.minecraft.client.gui.screens.DisconnectedScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(DisconnectedScreen.class)
public abstract class DisconnectedScreenMixin {
	@ModifyArg(
		method = "init",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/components/StringWidget;<init>(Lnet/minecraft/network/chat/Component;Lnet/minecraft/client/gui/Font;)V"
		),
		index = 0
	)
	private Component ipmask$maskVisibleTitle(Component title) {
		return IPMaskConfig.hideConnectionScreens() ? IPMaskClient.maskActive(title) : title;
	}

	@ModifyArg(
		method = "init",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/components/MultiLineTextWidget;<init>(Lnet/minecraft/network/chat/Component;Lnet/minecraft/client/gui/Font;)V"
		),
		index = 0
	)
	private Component ipmask$maskVisibleReason(Component reason) {
		return IPMaskConfig.hideConnectionScreens() ? IPMaskClient.maskActive(reason) : reason;
	}

	@ModifyArg(
		method = "getNarrationMessage",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/network/chat/CommonComponents;joinForNarration([Lnet/minecraft/network/chat/Component;)Lnet/minecraft/network/chat/MutableComponent;"
		),
		index = 0
	)
	private Component[] ipmask$maskNarration(Component[] components) {
		Component[] maskedComponents = components.clone();
		for (int i = 0; i < maskedComponents.length; i++) {
			maskedComponents[i] = IPMaskClient.maskNarration(maskedComponents[i]);
		}
		return maskedComponents;
	}
}
