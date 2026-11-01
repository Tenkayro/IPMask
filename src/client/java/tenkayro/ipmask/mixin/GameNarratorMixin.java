package tenkayro.ipmask.mixin;

import tenkayro.ipmask.client.IPMaskClient;
import net.minecraft.client.GameNarrator;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(GameNarrator.class)
public abstract class GameNarratorMixin {
	@ModifyVariable(method = "sayChatQueued", at = @At("HEAD"), argsOnly = true, ordinal = 0)
	private Component ipmask$maskChatNarration(Component message) {
		return IPMaskClient.maskActive(message);
	}

	@ModifyVariable(method = "saySystemChatQueued", at = @At("HEAD"), argsOnly = true, ordinal = 0)
	private Component ipmask$maskSystemChatNarration(Component message) {
		return IPMaskClient.maskActive(message);
	}

	@ModifyVariable(method = "saySystemQueued", at = @At("HEAD"), argsOnly = true, ordinal = 0)
	private Component ipmask$maskSystemNarration(Component message) {
		return IPMaskClient.maskActive(message);
	}

	@ModifyVariable(method = "saySystemNow(Lnet/minecraft/network/chat/Component;)V", at = @At("HEAD"), argsOnly = true, ordinal = 0)
	private Component ipmask$maskImmediateNarration(Component message) {
		return IPMaskClient.maskActive(message);
	}

	@ModifyVariable(method = "saySystemNow(Ljava/lang/String;)V", at = @At("HEAD"), argsOnly = true, ordinal = 0)
	private String ipmask$maskImmediateNarration(String message) {
		return IPMaskClient.maskActive(message);
	}
}
