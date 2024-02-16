package com.example.demo.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class AssetIncidentSummary {
    private int totalIncidents;
    private long totalDowntime;
    /**
     * Total weight of all incidents for the period.
     */
    private int rating;

    public void incrementIncidents() {
        this.totalIncidents++;
    }

    public void addDowntime(final int downtime) {
        this.totalDowntime += downtime;
    }

    public void addRating(final int weight) {
        this.rating += weight;
    }
}
