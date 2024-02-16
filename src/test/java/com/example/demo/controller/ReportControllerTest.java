package com.example.demo.controller;

import com.example.demo.service.CsvReportProvider;
import com.example.demo.service.Processor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ReportController controller;

    @MockBean
    private CsvReportProvider reportProvider;

    @Autowired
    private Processor processor;

    @BeforeEach
    void setUp() {
        when(reportProvider.getOutputFilePath()).thenReturn("src/test/resources/output.csv");
        when(reportProvider.getInputFilePath()).thenReturn("src/test/resources/input.csv");
        final File outputFile = new File(getClass().getClassLoader().getResource("output.csv").getPath());
        when(reportProvider.getOutputFile()).thenReturn(outputFile);
        processor.processStream();
    }

    @Test
    void downloadOutputFile() {
        ResponseEntity<Resource> resourceResponseEntity = controller.downloadOutputFile();
        Resource body = resourceResponseEntity.getBody();
        HttpStatusCode statusCode = resourceResponseEntity.getStatusCode();
        assertEquals(200, statusCode.value());
        assertNotNull(body);
    }

    @Test
    void shouldReturnFile() throws Exception {
        this.mockMvc.perform(get("/api/dashboard"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"output.csv\""));
    }
}