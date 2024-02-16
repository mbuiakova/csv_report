package com.example.demo.service;

import com.example.demo.model.AssetIncidentSummariesForPeriod;
import com.example.demo.model.AssetIncidentSummary;
import com.example.demo.model.Incident;
import com.example.demo.model.ReportPeriod;
import com.example.demo.util.FileUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;

/**
 * Provides a way to read the input data for a report from a CSV file,
 * and write the summary reports back in the CSV format.
 */
@Slf4j
@Component
public class CsvReportProvider implements ReportProvider {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("M/d/yyyy H:mm");
    private static final String[] INPUT_CSV_HEADERS = new String[]{"Asset Name", "Start Date", "End Time", "Severity"};
    private static final String[] OUTPUT_CSV_HEADERS = new String[]{"Asset Name", "Total Incidents", "Total Downtime", "Rating"};

    private static final CSVFormat INPUT_CSV_FORMAT = CSVFormat.Builder
            .create()
            .setDelimiter(',')
            .setSkipHeaderRecord(true)
            .setHeader(INPUT_CSV_HEADERS)
            .build();

    private static final CSVFormat OUTPUT_CSV_FORMAT = CSVFormat.Builder
            .create()
            .setHeader(OUTPUT_CSV_HEADERS)
            .build();

    private CSVParser parser;

    @Override
    public File getOutputFile() {
        return new File(getOutputFilePath());
    }

    public String getOutputFilePath() {
        return System.getenv("OUTPUT_FILE_PATH");
    }

    public String getInputFilePath() {
        return System.getenv("INPUT_FILE_PATH");
    }

    @Override
    public Stream<Incident> getIncidentsStream() throws IOException {
        final String inputFilePath = getInputFilePath();
        log.info("Reading csv file: {}", inputFilePath);

        if (parser != null && !parser.isClosed()) {
            parser.close();
        }

        parser = CSVParser.parse(new FileReader(inputFilePath), INPUT_CSV_FORMAT);

        return parser
                .stream()
                .map(CsvReportProvider::parseIncidentFromCsv);
    }

    @Override
    public void saveAssetSummariesForPeriod(final AssetIncidentSummariesForPeriod assetIncidentSummariesForPeriod) {
        final String outputFilePath = getOutputFilePath();
        FileUtils.ensureFileExists(Paths.get(outputFilePath));

        try (final FileWriter fileWriter = new FileWriter(outputFilePath)) {
            final CSVPrinter csvPrinter = new CSVPrinter(fileWriter, OUTPUT_CSV_FORMAT);

            final ReportPeriod reportPeriod = assetIncidentSummariesForPeriod.reportPeriod();

            assetIncidentSummariesForPeriod
                    .assetIncidentSummaries()
                    .entrySet()
                    .stream()
                    .map(entry -> convertAssetSummaryToCsv(entry.getKey(), entry.getValue(), reportPeriod.getPeriod().getDays()))
                    .forEach(data -> {
                        try {
                            csvPrinter.printRecord(data);
                        } catch (IOException e) {
                            log.error(e.getMessage());
                        }
                    });
        } catch (IOException e) {
            log.error(e.getMessage());
        }

        log.info("Saved asset summaries for the period to the file: {}", outputFilePath);
    }

    private static Incident parseIncidentFromCsv(final CSVRecord csvRecord) {
        final String name = csvRecord.get(INPUT_CSV_HEADERS[0]);
        final LocalDateTime start = LocalDateTime.parse(csvRecord.get(INPUT_CSV_HEADERS[1]), FORMATTER);
        final LocalDateTime end = LocalDateTime.parse(csvRecord.get(INPUT_CSV_HEADERS[2]), FORMATTER);
        final int severity = Integer.parseInt(csvRecord.get(INPUT_CSV_HEADERS[3]));

        return new Incident(name, start, end, severity);
    }

    private static String[] convertAssetSummaryToCsv(final String assetName,
                                                     final AssetIncidentSummary assetSummary,
                                                     final int reportPeriodDays
    ) {
        return new String[]{
                assetName,
                String.valueOf(assetSummary.getTotalIncidents()),
                String.format("%2.02f", (100.0 * assetSummary.getTotalDowntime() / Duration.ofDays(reportPeriodDays).toSeconds())),
                String.valueOf(assetSummary.getRating())
        };
    }

    @Override
    public void close() {
        if (parser != null) {
            try {
                parser.close();
            } catch (IOException e) {
                log.error(e.getMessage());
            }
        }
    }
}
