package util;
import java.util.regex.Pattern;
public final class Validator {
    private static final Pattern MOBILE = Pattern.compile("^[6-9][0-9]{9}$");
    private static final Pattern EMAIL = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PAN = Pattern.compile("^[A-Z]{5}[0-9]{4}[A-Z]$");
    private static final Pattern IFSC = Pattern.compile("^[A-Z]{4}0[A-Z0-9]{6}$");
    private Validator() {}
    public static boolean isValidMobile(String value) {
        return value != null && MOBILE.matcher(value).matches();
    }
    public static boolean isValidEmail(String value) {
        return value != null && EMAIL.matcher(value).matches();
    }
    public static boolean isValidPan(String value) {
        return value != null && PAN.matcher(value).matches();
    }
    public static boolean isValidIfsc(String value) {
        return value != null && IFSC.matcher(value).matches();
    }
}
