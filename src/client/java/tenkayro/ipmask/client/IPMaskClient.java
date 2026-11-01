package tenkayro.ipmask.client;

import net.fabricmc.api.ClientModInitializer;
import net.minecraft.network.chat.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class IPMaskClient implements ClientModInitializer {
	private static final Logger LOGGER = LoggerFactory.getLogger("ipmask");
	private static volatile String activeAddress = "";
	private static volatile String activeHost = "";

	@Override
	public void onInitializeClient() {
		LOGGER.info("IPMask initialized");
	}

	public static void setActiveAddress(String address, String host) {
		activeAddress = address;
		activeHost = host;
	}

	public static Component maskActive(Component component) {
		String original = component.getString();
		String masked = MaskText.mask(original, activeAddress);
		masked = MaskText.mask(masked, activeHost);
		return masked.equals(original) ? component : Component.literal(masked);
	}
}
