package com.example.demo.model;

import com.example.demo.util.DateUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.Period;

/**
 * Report period to keep track of the start and end dates of the report.
 * <p>
 * The class attempts to always keep the earliest start date and the latest end date.
 */
@Getter
@Setter
@NoArgsConstructor
public class ReportPeriod {

    private LocalDate startDate = LocalDate.MAX;
    private LocalDate endDate = LocalDate.MIN;

    /**
     * Updates the report dates with the given incidentStartDate and incidentEndDate dates.
     *
     * @param incidentStartDate the incidentStartDate date of the incident.
     * @param incidentEndDate   the incidentEndDate date of the incident.
     */
    public void updateReportDates(final LocalDate incidentStartDate, final LocalDate incidentEndDate) {
        if (incidentStartDate == null || incidentEndDate == null) {
            return;
        }

        startDate = DateUtil.min(incidentStartDate, startDate);
        endDate = DateUtil.max(incidentEndDate, endDate);
    }

    /**
     * Returns the period of the report, a difference between the start and end dates.
     *
     * @return the period of the report.
     * @see Period
     */
    public Period getPeriod() {
        return Period.between(startDate, endDate.plusDays(1));
    }

    /**
     * Clears the report period.
     *
     * Resets the start and the end dates.
     */
    public void clear() {
        startDate = LocalDate.MAX;
        endDate = LocalDate.MIN;
    }

}
