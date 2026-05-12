package utils;

import java.util.regex.Pattern;

public class Validator {
	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

	public static String requireText(String value, String fieldName) {
		if (value == null || value.trim().isEmpty()) {
			throw new IllegalArgumentException(fieldName + " este obligatoriu.");
		}
		return value.trim();
	}

	public static int requirePositive(int value, String fieldName) {
		if (value <= 0) {
			throw new IllegalArgumentException(fieldName + " trebuie sa fie pozitiv.");
		}
		return value;
	}

	public static double requireNonNegative(double value, String fieldName) {
		if (value < 0) {
			throw new IllegalArgumentException(fieldName + " nu poate fi negativ.");
		}
		return value;
	}

	public static String requireEmail(String value) {
		String email = requireText(value, "Emailul");
		if (!EMAIL_PATTERN.matcher(email).matches()) {
			throw new IllegalArgumentException("Format email invalid.");
		}
		return email;
	}
}
