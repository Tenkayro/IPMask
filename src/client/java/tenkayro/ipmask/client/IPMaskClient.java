package tenkayro.ipmask.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class IPMaskClient implements ClientModInitializer {
	private static final Logger LOGGER = LoggerFactory.getLogger("ipmask");
	private static volatile String activeAddress = "";
	private static volatile String activeHost = "";

	@Override
	public void onInitializeClient() {
		IPMaskConfig.load();
		LOGGER.info("IPMask initialized");
	}

	public static void setActiveAddress(String address, String host) {
		activeAddress = address;
		activeHost = host;
	}

	public static Component maskActive(Component component) {
		if (!IPMaskConfig.hideConnectionScreens()) {
			return component;
		}
		String original = component.getString();
		String masked = maskText(original);
		return masked.equals(original) ? component : Component.literal(masked);
	}

	public static String maskActive(String text) {
		return IPMaskConfig.hideInGameText() ? maskText(text) : text;
	}

	public static Component maskNarration(Component component) {
		if (!IPMaskConfig.hideNarration()) {
			return component;
		}
		String original = component.getString();
		String masked = maskText(original);
		return masked.equals(original) ? component : Component.literal(masked);
	}

	private static String maskText(String text) {
		String masked = MaskText.mask(text, activeAddress);
		return MaskText.mask(masked, activeHost);
	}

	public static FormattedCharSequence maskActive(FormattedCharSequence sequence) {
		if (!IPMaskConfig.hideInGameText()) {
			return sequence;
		}
		FormattedCharSequence masked = MaskText.mask(sequence, activeAddress);
		return MaskText.mask(masked, activeHost);
	}
}
