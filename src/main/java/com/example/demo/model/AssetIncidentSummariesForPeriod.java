package com.example.demo.model;

import java.util.Map;

/**
 * Represents the asset incident summaries for a period.
 *
 * @param assetIncidentSummaries the asset incident summaries, a map of asset name to asset incident summary.
 * @param reportPeriod the report period for the summaries.
 */
public record AssetIncidentSummariesForPeriod(Map<String, AssetIncidentSummary> assetIncidentSummaries, ReportPeriod reportPeriod) {
}
