package net.fhtagn.utils.utils;

public class MathUtils {
    public final static float EPSILON = 1E-5f;

    public static boolean floatEq(float a, float b, float delta) {
        return Math.abs(a - b) < delta;
    }

    public static boolean floatEq(float a, float b) {
        return floatEq(a, b, EPSILON);
    }

    public static float degToRad(float d) {
        return (float) Math.PI * d / 180.0f;
    }

    public static float radToDeg(float r) {
        return 180.0f * r / (float) Math.PI;
    }

    public static boolean isPowerOfTwo(int n) {
        // http://www.exploringbinary.com/ten-ways-to-check-if-an-integer-is-a-power-of-two-in-c/
        return (n & (n - 1)) == 0;
    }

    public static int nextPowerOfTwo(int n) {
        // Explanation :
        // http://stackoverflow.com/questions/1322510/given-an-integer-how-do-i-find-the-next-largest-power-of-two-using-bit-twiddlin
        if (n < 0) {
            return 0;
        }
        n--;
        n |= n >> 1;   // Divide by 2^k for consecutive doublings of k up to 32,
        n |= n >> 2;   // and then or the results.
        n |= n >> 4;
        n |= n >> 8;
        n |= n >> 16;
        n++;           // The result is a number of 1 bits equal to the number
        // of bits in the original number, plus 1. That's the
        // next highest power of 2.
        return n;
    }

    public static <T extends Comparable<T>> T clamp(T v, T min, T max) {
        if (v.compareTo(min) < 0) {
            return min;
        }
        if (v.compareTo(max) > 0) {
            return max;
        }
        return v;
    }
}
