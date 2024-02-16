package com.example.demo.service;

import com.example.demo.model.AssetIncidentSummary;
import com.example.demo.model.AssetIncidentSummariesForPeriod;
import com.example.demo.model.Incident;
import com.example.demo.model.ReportPeriod;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import static java.time.Duration.between;

/**
 * Processes the stream of incidents and generates the asset incident summaries for the period
 * of the input incident data.
 */
@Slf4j
@Component
@AllArgsConstructor
public class Processor {
    private final Map<String, AssetIncidentSummary> assetSummaryMap = new HashMap<>();
    private final ReportPeriod reportPeriod = new ReportPeriod();

    private final ReportProvider reportProvider;

    @Scheduled(cron = "0 0 4 * * ?", zone = "Europe/Amsterdam")
    @EventListener(ApplicationReadyEvent.class)
    public void processStream() {
        log.info("Stream processing started");

        /*
         * Gather AssertSummary for assets to map
         */
        try {
            reportProvider
                    .getIncidentsStream()
                    .forEach(incident -> assetSummaryMap.put(incident.assetName(),
                            convertToAssetSummary(incident))
                    );
        } catch (final Exception e) {
            log.error("Error processing the incident stream: {}", e.getMessage());
        } finally {
            reportProvider.close();
        }

        reportProvider.saveAssetSummariesForPeriod(convertToSummariesForPeriod());
        assetSummaryMap.clear();
        reportPeriod.clear();

        log.info("File processed successfully.");
    }

    private AssetIncidentSummary convertToAssetSummary(final Incident incident) {
        final var summary = assetSummaryMap.getOrDefault(incident.assetName(), new AssetIncidentSummary());
        summary.incrementIncidents();

        final LocalDateTime startTime = incident.startTime();
        final LocalDateTime endTime = incident.endTime();

        reportPeriod.updateReportDates(startTime.toLocalDate(), endTime.toLocalDate());

        final int downtime = (int) between(startTime, endTime).getSeconds();
        summary.addDowntime(downtime);

        final int weight = (incident.severity() == 1) ? 30 : 10; // Severity 1 has a weight of 30, 2 and 3 have a weight of 10
        summary.addRating(weight);

        return summary;
    }

    /**
     * Convert the assetSummaryMap to a stream of {@link java.lang.String String[]} to be written to the output file
     * Count the % of total downtime for each asset based on a duration of the report period
     * @return Ready to save stream
     */
    private AssetIncidentSummariesForPeriod convertToSummariesForPeriod() {
        return new AssetIncidentSummariesForPeriod(assetSummaryMap, reportPeriod);
    }
}
