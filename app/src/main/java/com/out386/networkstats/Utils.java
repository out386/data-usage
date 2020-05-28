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

    public static String getWebViewJs() {
        StringBuilder sb = new StringBuilder();
        sb.append("(function() {\n");
        sb.append("var usageDiv = document.getElementById('pt1:r1:0:r2:0:pglBC4')\n");
        sb.append("if (!usageDiv) usageDiv = document.getElementById('pt1:r1:0:r2:1:pglBC4')\n");
        sb.append("if (!usageDiv) return null\n");
        sb.append("var res = usageDiv.innerText.replace('\\n', '')\n");
        sb.append("var timeDiv = document.querySelector('.durationText.dataExpContent')\n");
        sb.append("var totalDiv = document.querySelector('.dataRemainContent')\n");
        sb.append("if (!timeDiv || !totalDiv) return null;\n");
        sb.append("return `${res}:${totalDiv.innerText}:${timeDiv.innerText}`\n");
        sb.append("})()\n");
        return sb.toString();
    }
}
