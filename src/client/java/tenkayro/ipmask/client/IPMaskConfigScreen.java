package tenkayro.ipmask.client;

import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public final class IPMaskConfigScreen {
	private IPMaskConfigScreen() {
	}

	public static Screen create(Screen parent) {
		ConfigCategory category = ConfigCategory.createBuilder()
			.name(Component.translatable("ipmask.config.category.privacy"))
			.options(List.of(
				option("address_fields", true, IPMaskConfig::hideAddressFields, IPMaskConfig::setHideAddressFields),
				option("server_list", true, IPMaskConfig::hideServerList, IPMaskConfig::setHideServerList),
				option("in_game_text", true, IPMaskConfig::hideInGameText, IPMaskConfig::setHideInGameText),
				option("narration", true, IPMaskConfig::hideNarration, IPMaskConfig::setHideNarration),
				option("connection_screens", true, IPMaskConfig::hideConnectionScreens, IPMaskConfig::setHideConnectionScreens)
			))
			.build();

		return YetAnotherConfigLib.createBuilder()
			.title(Component.translatable("ipmask.config.title"))
			.category(category)
			.save(IPMaskConfig::save)
			.build()
			.generateScreen(parent);
	}

	private static Option<Boolean> option(
		String key,
		boolean defaultValue,
		Supplier<Boolean> getter,
		Consumer<Boolean> setter
	) {
		String translationKey = "ipmask.config." + key;
		return Option.<Boolean>createBuilder()
			.name(Component.translatable(translationKey))
			.description(OptionDescription.of(Component.translatable(translationKey + ".description")))
			.binding(defaultValue, getter, setter)
			.controller(option -> BooleanControllerBuilder.create(option).coloured(true))
			.build();
	}
}
