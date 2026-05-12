package utils;

public class AlertHelper {
	public static void info(String title, String message) {
		System.out.println("[INFO] " + title + ": " + message);
	}

	public static void error(String title, String message) {
		System.err.println("[ERROR] " + title + ": " + message);
	}

	public static boolean confirm(String title, String message) {
		System.out.println("[CONFIRM] " + title + ": " + message);
		return true;
	}
}
