package tenkayro.ipmask.client;

import net.minecraft.util.FormattedCharSequence;

import java.util.ArrayList;
import java.util.List;

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
		if (masked.equals(original)) {
			return sequence;
		}

		List<FormattedCharSequence> parts = new ArrayList<>();
		int[] offset = {0};
		sequence.accept((index, style, codePoint) -> {
			int charCount = Character.charCount(codePoint);
			String replacement = masked.substring(offset[0], offset[0] + charCount);
			String originalCodePoint = new String(Character.toChars(codePoint));
			if (replacement.equals(originalCodePoint)) {
				parts.add(FormattedCharSequence.codepoint(codePoint, style));
			} else {
				replacement.codePoints().forEach(replacementCodePoint ->
					parts.add(FormattedCharSequence.codepoint(replacementCodePoint, style))
				);
			}
			offset[0] += charCount;
			return true;
		});
		return FormattedCharSequence.composite(parts);
	}
}
