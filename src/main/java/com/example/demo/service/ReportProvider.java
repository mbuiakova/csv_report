package com.example.demo.service;

import com.example.demo.model.AssetIncidentSummariesForPeriod;
import com.example.demo.model.Incident;

import java.io.File;
import java.util.stream.Stream;

/**
 * Provides the input data for making summary reports and a way to store the reports.
 */
public interface ReportProvider {
    /**
     * Returns the output file of the final report. Useful for downloading the report.
     *
     * @return the output file
     */
    File getOutputFile();

    /**
     * Obtains the incidents from a data source.
     * All the incidents do not necessarily present in the memory at the time of the call.
     *
     * @return a lazy stream of incidents from a data source.
     * @throws Exception if an error occurs while obtaining the incidents.
     */
    Stream<Incident> getIncidentsStream() throws Exception;

    /**
     * Saves the asset summaries for a period.
     *
     * @param assetIncidentSummariesForPeriod the asset summaries for a period.
     */
    void saveAssetSummariesForPeriod(final AssetIncidentSummariesForPeriod assetIncidentSummariesForPeriod);

    /**
     * Closes the report provider.
     * Should be called after a successful {@link #getIncidentsStream()} call.
     */
    void close();
}
