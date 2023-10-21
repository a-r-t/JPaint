package Utils;

// detects which operating system program is running on 
// https://stackoverflow.com/a/31547504/16948475
public class OperatingSystemUtils {
 
    private static OperatingSystem os = null;

    public static OperatingSystem getOperatingSystem() {
        if (os == null) {
            String currentOperatingSystem = System.getProperty("os.name").toLowerCase();
            if (currentOperatingSystem.contains("win")) {
                os = OperatingSystem.WINDOWS;
            } else if (currentOperatingSystem.contains("nix") || currentOperatingSystem.contains("nux")
                    || currentOperatingSystem.contains("aix")) {
                os = OperatingSystem.LINUX;
            } else if (currentOperatingSystem.contains("mac")) {
                os = OperatingSystem.MAC;
            } else if (currentOperatingSystem.contains("sunos")) {
                os = OperatingSystem.SOLARIS;
            }
        }
        return os;
    }
}
