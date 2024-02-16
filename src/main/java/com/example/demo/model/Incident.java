package com.example.demo.model;

import java.time.LocalDateTime;

/**
 * Represents an incident.
 *
 * @param assetName the asset name for which the incident occurred.
 * @param startTime the start time of the incident.
 * @param endTime the end time of the incident.
 * @param severity the severity of the incident.
 */
public record Incident(String assetName, LocalDateTime startTime, LocalDateTime endTime, int severity) {
}