package de.ifgi.iobapp.model;

public class DoubleComparator {
    private static final double EPSILON = 0.000001;

    public static boolean equals(double a, double b)
    {
        return Math.abs(a - b) < EPSILON * Math.max(Math.abs(a), Math.abs(b));
    }
}
