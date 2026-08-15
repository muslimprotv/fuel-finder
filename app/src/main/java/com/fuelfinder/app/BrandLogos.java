package com.fuelfinder.app;

import java.util.Locale;

public final class BrandLogos {
    private BrandLogos() {}

    public static String logoFor(String stationName) {
        String n = stationName == null ? "" : stationName.toLowerCase(Locale.ROOT);
        if (n.contains("pakistan state oil") || n.contains("pso")) {
            return "https://upload.wikimedia.org/wikipedia/commons/1/1d/Pakistan_State_Oil_-_New_Logo.png";
        }
        if (n.contains("shell")) {
            return "https://upload.wikimedia.org/wikipedia/commons/thumb/e/e3/Shell_wordmark_2019.svg/320px-Shell_wordmark_2019.svg.png";
        }
        if (n.contains("total")) {
            return "https://upload.wikimedia.org/wikipedia/commons/thumb/d/d1/TotalEnergies_wordmark_%282021-present%29.svg/320px-TotalEnergies_wordmark_%282021-present%29.svg.png";
        }
        if (n.contains("attock") || n.contains("apl")) {
            return "https://upload.wikimedia.org/wikipedia/commons/8/89/Attock_Petroleum_logo.png";
        }
        if (n.contains("gas & oil") || n.contains("gas and oil") || n.startsWith("go ")) {
            return "https://upload.wikimedia.org/wikipedia/commons/f/fb/Gas_and_Oil_Pakistan_Logo.jpg";
        }
        return null;
    }

    public static String shortBrand(String stationName) {
        String n = stationName == null ? "" : stationName.toLowerCase(Locale.ROOT);
        if (n.contains("pakistan state oil") || n.contains("pso")) return "PSO";
        if (n.contains("shell")) return "SHELL";
        if (n.contains("total")) return "TOTAL";
        if (n.contains("attock") || n.contains("apl")) return "ATTOCK";
        if (n.contains("gas & oil") || n.contains("gas and oil") || n.startsWith("go ")) return "GO";
        return "FUEL";
    }
}
