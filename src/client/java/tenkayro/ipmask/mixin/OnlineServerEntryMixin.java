package tenkayro.ipmask.mixin;

import tenkayro.ipmask.client.MaskText;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ServerSelectionList.OnlineServerEntry.class)
public abstract class OnlineServerEntryMixin {
	@Shadow
	@Final
	private ServerData serverData;

	@ModifyArg(
		method = "extractContent",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;setTooltipForNextFrame(Lnet/minecraft/network/chat/Component;II)V"
		),
		index = 0
	)
	private Component ipmask$maskStatusTooltip(Component tooltip) {
		String original = tooltip.getString();
		String masked = MaskText.mask(original, this.serverData.ip);
		return masked.equals(original) ? tooltip : Component.literal(masked);
	}

	@ModifyArg(
		method = "extractContent",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;setTooltipForNextFrame(Ljava/util/List;II)V"
		),
		index = 0
	)
	private List<FormattedCharSequence> ipmask$maskPlayerTooltip(List<FormattedCharSequence> lines) {
		List<FormattedCharSequence> maskedLines = new ArrayList<>(lines.size());
		for (FormattedCharSequence line : lines) {
			maskedLines.add(MaskText.mask(line, this.serverData.ip));
		}
		return maskedLines;
	}
}
