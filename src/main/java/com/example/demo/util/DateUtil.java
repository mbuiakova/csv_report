package com.example.demo.util;

import java.time.LocalDate;

/**
 * Utility class for date operations.
 */
public class DateUtil {

    private DateUtil() {
    }

    /**
     * Returns the minimum of the two dates.
     * @return the minimum of the two dates.
     */
    public static LocalDate min(final LocalDate date1, final LocalDate date2) {
        return date1.isBefore(date2) ? date1 : date2;
    }

    /**
     * Returns the maximum of the two dates.
     * @return the maximum of the two dates.
     */
    public static LocalDate max(final LocalDate date1, final LocalDate date2) {
        return date1.isAfter(date2) ? date1 : date2;
    }
}
