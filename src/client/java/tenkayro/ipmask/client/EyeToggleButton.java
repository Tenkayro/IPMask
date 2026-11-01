package tenkayro.ipmask.client;

import java.util.function.BooleanSupplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.network.chat.Component;

public final class EyeToggleButton extends Button {
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
		String glyph = this.shown.getAsBoolean() ? "◉" : "⊗";
		var font = Minecraft.getInstance().font;
		int x = this.getX() + (20 - font.width(glyph)) / 2;
		int y = this.getY() + (20 - font.lineHeight) / 2;
		graphics.text(font, Component.literal(glyph), x, y, -1);
	}
}
