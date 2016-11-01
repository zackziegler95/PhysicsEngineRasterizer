package rasterization.tools;

public class Utils {
    public static double clamp01(double d) {
        if (d < 0) d = 0;
        else if (d > 1) d = 1;
        return d;
    }
}
