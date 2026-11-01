package tenkayro.ipmask.mixin;

import tenkayro.ipmask.client.AddressMaskable;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EditBox.class)
public abstract class EditBoxMixin implements AddressMaskable {
	@Unique
	private boolean ipmask$hidden;

	@Override
	@Unique
	public void ipmask$setHidden(boolean hidden) {
		this.ipmask$hidden = hidden;
	}

	@Override
	@Unique
	public boolean ipmask$isHidden() {
		return this.ipmask$hidden;
	}

	@Inject(method = "applyFormat", at = @At("HEAD"), cancellable = true)
	private void ipmask$maskRenderedText(String text, int offset, CallbackInfoReturnable<FormattedCharSequence> cir) {
		if (this.ipmask$hidden) {
			cir.setReturnValue(FormattedCharSequence.forward("*".repeat(text.length()), Style.EMPTY));
		}
	}

	@Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
	private void ipmask$blockCopyAndCut(KeyEvent event, CallbackInfoReturnable<Boolean> cir) {
		if (this.ipmask$hidden && (event.isCopy() || event.isCut())) {
			cir.setReturnValue(true);
		}
	}

	@Inject(method = "createNarrationMessage", at = @At("HEAD"), cancellable = true)
	private void ipmask$maskNarration(CallbackInfoReturnable<Component> cir) {
		if (this.ipmask$hidden) {
			cir.setReturnValue(Component.translatable("ipmask.field.hidden"));
		}
	}
}
