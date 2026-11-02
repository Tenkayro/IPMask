package tenkayro.ipmask.mixin;

import tenkayro.ipmask.client.MaskText;
import tenkayro.ipmask.client.IPMaskConfig;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import net.minecraft.client.server.LanServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ServerSelectionList.NetworkServerEntry.class)
public abstract class NetworkServerEntryMixin {
	@Shadow
	@Final
	protected LanServer serverData;

	@ModifyArg(
		method = "extractContent",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;text(Lnet/minecraft/client/gui/Font;Ljava/lang/String;III)V",
			ordinal = 0
		),
		index = 1
	)
	private String ipmask$maskLanAddress(String address) {
		return IPMaskConfig.hideServerList() ? MaskText.mask(address, address) : address;
	}
}
