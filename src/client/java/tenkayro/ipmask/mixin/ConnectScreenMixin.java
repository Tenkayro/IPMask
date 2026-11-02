package tenkayro.ipmask.mixin;

import tenkayro.ipmask.client.IPMaskClient;
import tenkayro.ipmask.client.IPMaskConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.TransferState;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ConnectScreen.class)
public abstract class ConnectScreenMixin {
	@Inject(method = "startConnecting", at = @At("HEAD"))
	private static void ipmask$rememberConnectionAddress(
		Screen parent,
		Minecraft minecraft,
		ServerAddress hostAndPort,
		ServerData data,
		boolean isQuickPlay,
		TransferState transferState,
		CallbackInfo ci
	) {
		IPMaskClient.setActiveAddress(data.ip, hostAndPort.getHost());
	}

	@ModifyArg(
		method = "extractRenderState",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;centeredText(Lnet/minecraft/client/gui/Font;Lnet/minecraft/network/chat/Component;III)V"
		),
		index = 1
	)
	private Component ipmask$maskConnectingMessage(Component message) {
		return IPMaskConfig.hideConnectionScreens() ? IPMaskClient.maskActive(message) : message;
	}
}
