package util;

import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

/**
 * Utility class for formatting dates.
 * Ensures that stored date strings are formatted in GMT.
 */
public class DateUtils {
    public static String getCanonicalDatePattern() {
        return "yyyy-MM-dd HH:mm";
    }

    public static String getLocalizedDatePattern() {
        Locale locale = Locale.getDefault();
        String language = locale.getLanguage();

        if (language.equals("pt")) {
            return "dd/MM/yyyy HH:mm"; // Brazilian format
        } else if (language.equals("en")) {
            return "MM/dd/yyyy hh:mm a"; // US format
        } else {
            return "yyyy-MM-dd HH:mm"; // ISO format (fallback)
        }
    }

    public static SimpleDateFormat getCanonicalFormatter() {
        SimpleDateFormat sdf = new SimpleDateFormat(getCanonicalDatePattern(), Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf;
    }

    public static SimpleDateFormat getLocalizedFormatter() {
        return new SimpleDateFormat(getLocalizedDatePattern(), Locale.getDefault());
    }
}
