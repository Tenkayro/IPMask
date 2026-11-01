package tenkayro.ipmask.mixin;

import tenkayro.ipmask.client.AddressListVisibility;
import tenkayro.ipmask.client.MaskText;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.multiplayer.ServerSelectionList;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ServerSelectionList.OnlineServerEntry.class)
public abstract class OnlineServerEntryMixin {
	@Shadow
	@Final
	private ServerData serverData;

	@Shadow
	@Final
	private JoinMultiplayerScreen screen;

	@ModifyArg(
		method = "extractContent",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;text(Lnet/minecraft/client/gui/Font;Ljava/lang/String;III)V",
			ordinal = 0
		),
		index = 1
	)
	private String ipmask$maskEntryName(String name) {
		if (((AddressListVisibility)this.screen).ipmask$areAddressesVisible()) {
			return name;
		}

		return MaskText.mask(name, this.serverData.ip);
	}

	@ModifyArg(
		method = "getNarration",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/network/chat/Component;translatable(Ljava/lang/String;[Ljava/lang/Object;)Lnet/minecraft/network/chat/MutableComponent;",
			ordinal = 0
		),
		index = 1
	)
	private Object[] ipmask$maskEntryNarration(Object[] arguments) {
		if (((AddressListVisibility)this.screen).ipmask$areAddressesVisible() || arguments.length == 0 || !(arguments[0] instanceof String name)) {
			return arguments;
		}

		Object[] maskedArguments = arguments.clone();
		maskedArguments[0] = MaskText.mask(name, this.serverData.ip);
		return maskedArguments;
	}

	@ModifyArg(
		method = "extractContent",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;setTooltipForNextFrame(Lnet/minecraft/network/chat/Component;II)V"
		),
		index = 0
	)
	private Component ipmask$maskStatusTooltip(Component tooltip) {
		if (this.isAddressVisible()) {
			return tooltip;
		}

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
		if (this.isAddressVisible()) {
			return lines;
		}

		List<FormattedCharSequence> maskedLines = new ArrayList<>(lines.size());
		for (FormattedCharSequence line : lines) {
			maskedLines.add(MaskText.mask(line, this.serverData.ip));
		}
		return maskedLines;
	}

	@Unique
	private boolean isAddressVisible() {
		return ((AddressListVisibility)this.screen).ipmask$areAddressesVisible();
	}
}
