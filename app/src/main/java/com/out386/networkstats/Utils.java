package com.out386.networkstats;

class Utils {

    static String formatBytes(long bytes) {
        if (bytes < 1000)
            return bytes + " B";
        if (bytes < 1024000) {
            double formattedBytes = round(bytes / 1024D);
            return formattedBytes + " KB";
        }
        if (bytes < 1048576000) {
            double formattedBytes = round(bytes / 1048576D);
            return formattedBytes + " MB";
        }

        double formattedBytes = round(bytes / 1073741824D);
        return formattedBytes + " GB";

    }

    private static double round(double value) {
        long factor = 10;
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }
}
