package tenkayro.ipmask.client;

import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;

public final class MaskText {
	private static final String MASK = "*";

	private MaskText() {
	}

	public static String mask(String text, String address) {
		if (text.isEmpty() || address == null || address.isEmpty()) {
			return text;
		}

		StringBuilder masked = new StringBuilder(text);
		int position = 0;
		while (position <= text.length() - address.length()) {
			if (text.regionMatches(true, position, address, 0, address.length())) {
				masked.replace(position, position + address.length(), MASK.repeat(address.length()));
				position += address.length();
			} else {
				position++;
			}
		}

		return masked.toString();
	}

	public static FormattedCharSequence mask(FormattedCharSequence sequence, String address) {
		StringBuilder text = new StringBuilder();
		sequence.accept((index, style, codePoint) -> {
			text.appendCodePoint(codePoint);
			return true;
		});
		String original = text.toString();
		String masked = mask(original, address);
		return masked.equals(original) ? sequence : FormattedCharSequence.forward(masked, Style.EMPTY);
	}
}
