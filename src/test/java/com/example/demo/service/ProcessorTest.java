package com.example.demo.service;

import com.example.demo.model.Incident;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessorTest {

    @Mock
    CsvReportProvider csvReportProvider;

    private Processor processor;

    @BeforeEach
    void setUp() {
        processor = new Processor(csvReportProvider);
    }

    @Test
    void singleDayStreamProducesCorrectSummaries() throws IOException {
        final Stream<Incident> incidentStream = Stream.of(
                new Incident("asset1", LocalDateTime.of(2021, 1, 1, 1, 1), LocalDateTime.of(2021, 1, 1, 1, 2), 1),
                new Incident("asset1", LocalDateTime.of(2021, 1, 1, 1, 3), LocalDateTime.of(2021, 1, 1, 1, 4), 2),
                new Incident("asset2", LocalDateTime.of(2021, 1, 1, 1, 1), LocalDateTime.of(2021, 1, 1, 1, 2), 1),
                new Incident("asset2", LocalDateTime.of(2021, 1, 1, 1, 3), LocalDateTime.of(2021, 1, 1, 1, 4), 2)
        );

        when(csvReportProvider.getIncidentsStream()).thenReturn(incidentStream);

        doNothing().when(csvReportProvider).saveAssetSummariesForPeriod(argThat(
                summaries -> summaries.assetIncidentSummaries().size() == 2
                        && summaries.assetIncidentSummaries().get("asset1").getTotalIncidents() == 2
                        && summaries.assetIncidentSummaries().get("asset1").getTotalDowntime() == 2*60
                        && summaries.assetIncidentSummaries().get("asset1").getRating() == 40
                        && summaries.assetIncidentSummaries().get("asset2").getTotalIncidents() == 2
                        && summaries.assetIncidentSummaries().get("asset2").getTotalDowntime() == 2*60
                        && summaries.assetIncidentSummaries().get("asset2").getRating() == 40
        ));

        processor.processStream();
    }

    @Test
    void emptyStreamProducesEmptySummaries() throws IOException {
        final Stream<Incident> incidentStream = Stream.of();

        when(csvReportProvider.getIncidentsStream()).thenReturn(incidentStream);

        doNothing().when(csvReportProvider).saveAssetSummariesForPeriod(argThat(
                summaries -> summaries.assetIncidentSummaries().isEmpty()
        ));

        processor.processStream();
    }

    @Test
    void twoDayStreamProducesCorrectSummaries() throws IOException {
        final Stream<Incident> incidentStream = Stream.of(
                new Incident("asset1", LocalDateTime.of(2021, 1, 1, 1, 1), LocalDateTime.of(2021, 1, 1, 1, 2), 1),
                new Incident("asset1", LocalDateTime.of(2021, 1, 2, 1, 3), LocalDateTime.of(2021, 1, 2, 1, 4), 2)
        );

        when(csvReportProvider.getIncidentsStream()).thenReturn(incidentStream);

        doNothing().when(csvReportProvider).saveAssetSummariesForPeriod(argThat(
                summaries -> summaries.assetIncidentSummaries().size() == 1
                        && summaries.assetIncidentSummaries().get("asset1").getTotalIncidents() == 2
                        && summaries.assetIncidentSummaries().get("asset1").getTotalDowntime() == 2*60
                        && summaries.assetIncidentSummaries().get("asset1").getRating() == 40
        ));

        processor.processStream();
    }
}