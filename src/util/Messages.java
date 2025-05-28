package util;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Utility class for retrieving internationalized messages.
 */
public class Messages {
    private static final ResourceBundle rb = createBundle();

    private static ResourceBundle createBundle() {
        try {
            return ResourceBundle.getBundle("resources.messages", Locale.getDefault());
        } catch (Exception e) {
            System.err.println("Resource bundle for locale " + Locale.getDefault() + " not found. Falling back to English.");
            return ResourceBundle.getBundle("resources.messages", Locale.ENGLISH);
        }
    }

    public static String get(String key) {
        return rb.getString(key);
    }
}
