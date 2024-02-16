package com.example.demo.controller;

import com.example.demo.service.ReportProvider;
import lombok.AllArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.core.io.Resource;

import java.io.File;

@RestController
@AllArgsConstructor
public class ReportController {

    private ReportProvider outputFileProvider;

    @GetMapping("api/dashboard")
    public ResponseEntity<Resource> downloadOutputFile() {
        try {
            final File outputFile = outputFileProvider.getOutputFile();
            final FileSystemResource fileResource = new FileSystemResource(outputFile);

            if (!fileResource.exists()) {
                return ResponseEntity.notFound().build();
            }

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileResource.getFilename() + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(fileResource);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
