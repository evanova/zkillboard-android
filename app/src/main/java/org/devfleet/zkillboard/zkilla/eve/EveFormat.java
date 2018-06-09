package org.devfleet.zkillboard.zkilla.eve;


import android.graphics.Color;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DurationFormatUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public final class EveFormat {
    private static TimeZone TZ = TimeZone.getDefault();

    public enum Currency {
        SHORT("###,###,###.0"), MEDIUM("###,###,###,###,###"), LONG("###,###,###,###,###.00");

        private final NumberFormat iskFormat;

        Currency(String pattern) {
            this.iskFormat = new DecimalFormat(pattern);
        }

        public final String format(double isk, String suffix) {
            String f = this.iskFormat.format(isk);
            if (StringUtils.isNotBlank(suffix)) {
                f = f + " " + suffix;
            }
            return f;
        }

        public final String format(double isk) {
            return format(isk, null);
        }

        public static String SHORT(double isk) {
            return SHORT(isk, true);
        }

        public static String SHORT(double isk, boolean suffix) {
            if (isk >= 1000000000d) {
                //1Billion
                return (suffix) ? Currency.SHORT.format(isk / 1000000000d, "B ISK") : Currency.SHORT.format(isk / 1000000000d, "B");
            }
            if (isk >= 1000000d) {
                //1M
                return (suffix) ? Currency.SHORT.format( isk / 1000000L, "M ISK") : Currency.SHORT.format(isk / 1000000L, "M");
            }
            if (isk >= 1000d) {
                return (suffix) ? Currency.SHORT.format(isk / 1000L, "K ISK") : Currency.SHORT.format( isk / 1000L, "K");
            }
            return (suffix) ? Currency.SHORT.format(isk, "ISK") : Currency.SHORT.format(isk, null);
        }

        public static String MEDIUM(double isk) {
            return MEDIUM(isk, true);
        }

        public static String MEDIUM(double isk, boolean suffix) {
            return (suffix) ? Currency.MEDIUM.format((long) isk, "ISK") : Currency.MEDIUM.format((long) isk, null);
        }

        public static String LONG(double isk) {
            return LONG(isk, true);
        }

        public static String LONG(double isk, boolean suffix) {
            return (suffix) ? Currency.LONG.format(isk, "ISK") : Currency.LONG.format(isk, null);
        }
    }

    public enum DateTime {
        PLAIN("EE dd MMM yyyy HH:mm"),
        YEAR("EEEE dd MMMM yyyy"),
        YEAR_S("EE dd MMM yyyy"),
        MEDIUM("EEEE dd MMMM HH:mm"),
        DATE("EEE dd MMM"),
        DATE_S("dd MMM"),
        SHORT("EEE dd MMM HH:mm"),
        SHORT_Y("EEE dd MMM yyyy HH:mm"),
        LONG("EEEE dd MMMM yyyy HH:mm"),
        LONG_S("EEEE dd MMMM yyyy HH:mm ss");

        private final SimpleDateFormat format;

        DateTime(String pattern) {
            this.format = new SimpleDateFormat(pattern);
        }

        public final String format(long dateTime) {
            this.format.setTimeZone(TZ);
            return this.format.format(dateTime);
        }

        public static String MEDIUM(long dateTime) {
            return DateTime.MEDIUM.format(dateTime);
        }

        public static String SHORT(long dateTime) {
            return DateTime.SHORT.format(dateTime);
        }

        public static String SHORT(long dateTime, boolean withYear) {
            return (withYear) ? DateTime.SHORT_Y.format(dateTime) : DateTime.SHORT.format(dateTime);
        }

        public static String DATE(long dateTime) {
            return DateTime.DATE.format(dateTime);
        }

        public static String DATE(long dateTime, boolean withYear) {
            return (withYear) ? DateTime.DATE.format(dateTime) : DateTime.DATE_S.format(dateTime);
        }

        public static String LONG(long dateTime, boolean withSeconds) {
            return (withSeconds) ? DateTime.LONG_S.format(dateTime) : DateTime.LONG.format(dateTime);
        }

        public static String YEAR(long dateTime) {
            return YEAR(dateTime, false);
        }

        public static String YEAR(long dateTime, boolean shortVersion) {
            return (shortVersion) ? DateTime.YEAR_S.format(dateTime) : DateTime.YEAR.format(dateTime);
        }

        public static String PLAIN(long dateTime) {
            return DateTime.PLAIN.format(dateTime);
        }
    }

    public enum Date {
        DAY("dd MMM"), SHORT("EE. dd MM"), MEDIUM("EE. dd MMM yyyy"), LONG("EEEE dd MMMM yyyy");

        private final SimpleDateFormat format;

        Date(String pattern) {
            this.format = new SimpleDateFormat(pattern);
        }

        public final String format(long dateTime) {
            this.format.setTimeZone(TZ);
            return this.format.format(dateTime);
        }

        public static String DAY(long dateTime) {
            return Date.DAY.format(dateTime);
        }

        public static String SHORT(long dateTime) {
            return Date.SHORT.format(dateTime);
        }

        public static String MEDIUM(long dateTime) {
            return Date.MEDIUM.format(dateTime);
        }

        public static String LONG(long dateTime) {
            return Date.LONG.format(dateTime);
        }
    }

    public enum Duration {
        SHORT("H'h'", "d'd' H'h'"),
        MEDIUM("H'h' mm'mn' ", "d'd' H'h' mm'mn'"),
        LONG("H'h' mm'mn' s's'", "d'd' H'h' mm'mn' ss's'");

        private final String shortFormat;
        private final String longFormat;

        Duration(String shortPattern, String longPattern) {
            this.shortFormat = shortPattern;
            this.longFormat = longPattern;
        }

        public String format(long durationMillis) {
            if (durationMillis < (24 * 3600 * 1000)) {
                return DurationFormatUtils.formatDuration(Math.abs(durationMillis), shortFormat);
            }
            return DurationFormatUtils.formatDuration(Math.abs(durationMillis), longFormat);
        }

        public static String MEDIUM(long durationMillis) {
            return Duration.MEDIUM.format(durationMillis);
        }

        public static String LONG(long durationMillis) {
            return Duration.LONG.format(durationMillis);
        }

        public static String SHORT(long durationMillis) {
            return Duration.SHORT.format(durationMillis);
        }
    }

    private EveFormat() {
    }

    public static int getSecurityLevelColor(final float security) {
        if (security > 0.5) {
            return Color.HSVToColor(255, new float[]{180f * security, 100f, 100f});
        }
        if (security > 0.1) {
            return Color.HSVToColor(255, new float[]{Math.max(180f * security - 30f, 0f), 100f, 100f});
        }
        if (security > 0) {
            return Color.HSVToColor(255, new float[]{Math.max(180f * security - 60f, 0f), 100f, 100f});
        }
        return Color.HSVToColor(255, new float[]{0f, 100f, 100f});
    }

    public static int getSecurityStatusColor(final float security) {
        if (security > 0) {
            return Color.LTGRAY;
        }
        if (security > -2.0) {
            return Color.WHITE;
        }
        return getSecurityLevelColor((security + 10) / 20);
    }

    public static void setTimeZone(final TimeZone timeZone) {
        TZ = timeZone;
    }
}
