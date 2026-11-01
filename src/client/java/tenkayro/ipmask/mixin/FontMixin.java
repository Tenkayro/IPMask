package tenkayro.ipmask.mixin;

import tenkayro.ipmask.client.IPMaskClient;
import net.minecraft.client.gui.Font;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(Font.class)
public abstract class FontMixin {
	@ModifyVariable(
		method = "prepareText(Ljava/lang/String;FFIZI)Lnet/minecraft/client/gui/Font$PreparedText;",
		at = @At("HEAD"),
		argsOnly = true,
		ordinal = 0
	)
	private String ipmask$maskPreparedString(String text) {
		return IPMaskClient.maskActive(text);
	}

	@ModifyVariable(
		method = {
			"prepareText(Lnet/minecraft/util/FormattedCharSequence;FFIZZI)Lnet/minecraft/client/gui/Font$PreparedText;",
			"prepare8xTextOutline(Lnet/minecraft/util/FormattedCharSequence;FFI)Lnet/minecraft/client/gui/Font$PreparedText;"
		},
		at = @At("HEAD"),
		argsOnly = true,
		ordinal = 0
	)
	private FormattedCharSequence ipmask$maskPreparedSequence(FormattedCharSequence text) {
		return IPMaskClient.maskActive(text);
	}
}
