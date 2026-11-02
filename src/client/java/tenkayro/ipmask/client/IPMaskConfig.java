package tenkayro.ipmask.client;

import net.fabricmc.loader.api.FabricLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class IPMaskConfig {
	private static final Logger LOGGER = LoggerFactory.getLogger("ipmask");
	private static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("ipmask.properties");

	private static boolean hideAddressFields = true;
	private static boolean hideServerList = true;
	private static boolean hideInGameText = true;
	private static boolean hideNarration = true;
	private static boolean hideConnectionScreens = true;

	private IPMaskConfig() {
	}

	public static void load() {
		if (!Files.exists(CONFIG_PATH)) {
			return;
		}

		Properties properties = new Properties();
		try (InputStream input = Files.newInputStream(CONFIG_PATH)) {
			properties.load(input);
		} catch (IOException exception) {
			LOGGER.error("Failed to load IPMask configuration from {}", CONFIG_PATH, exception);
			return;
		}

		hideAddressFields = properties.getProperty("hideAddressFields", "true").equalsIgnoreCase("true");
		hideServerList = properties.getProperty("hideServerList", "true").equalsIgnoreCase("true");
		hideInGameText = properties.getProperty("hideInGameText", "true").equalsIgnoreCase("true");
		hideNarration = properties.getProperty("hideNarration", "true").equalsIgnoreCase("true");
		hideConnectionScreens = properties.getProperty("hideConnectionScreens", "true").equalsIgnoreCase("true");
	}

	public static void save() {
		Properties properties = new Properties();
		properties.setProperty("hideAddressFields", Boolean.toString(hideAddressFields));
		properties.setProperty("hideServerList", Boolean.toString(hideServerList));
		properties.setProperty("hideInGameText", Boolean.toString(hideInGameText));
		properties.setProperty("hideNarration", Boolean.toString(hideNarration));
		properties.setProperty("hideConnectionScreens", Boolean.toString(hideConnectionScreens));

		try {
			Files.createDirectories(CONFIG_PATH.getParent());
			try (OutputStream output = Files.newOutputStream(CONFIG_PATH)) {
				properties.store(output, "IPMask client settings");
			}
		} catch (IOException exception) {
			LOGGER.error("Failed to save IPMask configuration to {}", CONFIG_PATH, exception);
		}
	}

	public static boolean hideAddressFields() {
		return hideAddressFields;
	}

	public static void setHideAddressFields(boolean value) {
		hideAddressFields = value;
	}

	public static boolean hideServerList() {
		return hideServerList;
	}

	public static void setHideServerList(boolean value) {
		hideServerList = value;
	}

	public static boolean hideInGameText() {
		return hideInGameText;
	}

	public static void setHideInGameText(boolean value) {
		hideInGameText = value;
	}

	public static boolean hideNarration() {
		return hideNarration;
	}

	public static void setHideNarration(boolean value) {
		hideNarration = value;
	}

	public static boolean hideConnectionScreens() {
		return hideConnectionScreens;
	}

	public static void setHideConnectionScreens(boolean value) {
		hideConnectionScreens = value;
	}
}
