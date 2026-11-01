package tenkayro.ipmask.mixin;

import tenkayro.ipmask.client.IPMaskClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.ConnectScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.TransferState;
import net.minecraft.client.multiplayer.resolver.ServerAddress;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
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
}
