package com.out386.networkstats;

class Utils {

    static String formatBytes(double bytes) {
        int multiplier;

        if (bytes < 0) {
            multiplier = -1;
            bytes *= -1;
        } else
            multiplier = 1;

        if (bytes < 1000)
            return multiplier * bytes + " B";
        if (bytes < 1024000) {
            double formattedBytes = round(bytes / 1024);
            formattedBytes *= multiplier;
            return formattedBytes + " KB";
        }
        if (bytes < 1048576000) {
            double formattedBytes = round(bytes / 1048576);
            formattedBytes *= multiplier;
            return formattedBytes + " MB";
        }

        double formattedBytes = round(bytes / 1073741824);
        formattedBytes *= multiplier;
        return formattedBytes + " GB";

    }

    private static double round(double value) {
        long factor = 10;
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}
