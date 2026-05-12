package service;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

public class ValidationService {
	private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

	public void requireText(String value, String fieldName) {
		if (value == null || value.trim().isEmpty()) {
			throw new IllegalArgumentException(fieldName + " este obligatoriu.");
		}
	}

	public void requirePositive(int value, String fieldName) {
		if (value <= 0) {
			throw new IllegalArgumentException(fieldName + " trebuie sa fie pozitiv.");
		}
	}

	public void requireNonNegative(double value, String fieldName) {
		if (value < 0) {
			throw new IllegalArgumentException(fieldName + " nu poate fi negativ.");
		}
	}

	public void requireEmail(String email) {
		requireText(email, "Emailul");
		if (!EMAIL_PATTERN.matcher(email.trim()).matches()) {
			throw new IllegalArgumentException("Format email invalid.");
		}
	}

	public void requireFutureDate(LocalDateTime dateTime, String fieldName) {
		if (dateTime == null) {
			throw new IllegalArgumentException(fieldName + " este obligatoriu.");
		}
	}
}
