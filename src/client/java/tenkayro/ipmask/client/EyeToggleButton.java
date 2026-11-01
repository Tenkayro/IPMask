package tenkayro.ipmask.client;

import java.util.function.BooleanSupplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public final class EyeToggleButton extends Button {
	private static final Identifier OPEN_SPRITE = Identifier.fromNamespaceAndPath("ipmask", "ipmask/eye_open");
	private static final Identifier HIDDEN_SPRITE = Identifier.fromNamespaceAndPath("ipmask", "ipmask/eye_hidden");
	private static final Identifier OPEN_TEXTURE = Identifier.fromNamespaceAndPath("ipmask", "textures/gui/sprites/ipmask/eye_open.png");
	private static final Identifier HIDDEN_TEXTURE = Identifier.fromNamespaceAndPath("ipmask", "textures/gui/sprites/ipmask/eye_hidden.png");
	private final BooleanSupplier shown;
	private final Runnable toggle;

	public EyeToggleButton(int x, int y, BooleanSupplier shown, Runnable toggle) {
		super(x, y, 20, 20, Component.translatable("ipmask.show"), button -> {
			EyeToggleButton eyeButton = (EyeToggleButton)button;
			eyeButton.toggle.run();
			eyeButton.refreshState();
		}, DEFAULT_NARRATION);
		this.shown = shown;
		this.toggle = toggle;
		this.refreshState();
	}

	private void refreshState() {
		Component action = Component.translatable(this.shown.getAsBoolean() ? "ipmask.hide" : "ipmask.show");
		this.setMessage(action);
		this.setTooltip(Tooltip.create(action));
	}

	@Override
	protected void extractContents(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
		this.extractDefaultSprite(graphics);
		boolean isShown = this.shown.getAsBoolean();
		Identifier sprite = isShown ? OPEN_SPRITE : HIDDEN_SPRITE;
		Identifier texture = isShown ? OPEN_TEXTURE : HIDDEN_TEXTURE;
		if (Minecraft.getInstance().getResourceManager().getResource(texture).isPresent()) {
			graphics.blitSprite(RenderPipelines.GUI_TEXTURED, sprite, this.getX() + 2, this.getY() + 2, 16, 16);
			return;
		}

		Component fallback = Component.translatable(isShown ? "ipmask.hide.short" : "ipmask.show.short");
		var font = Minecraft.getInstance().font;
		int x = this.getX() + (this.width - font.width(fallback)) / 2;
		int y = this.getY() + (this.height - font.lineHeight) / 2;
		graphics.text(font, fallback, x, y, -1);
	}
}
