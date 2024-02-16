package com.example.demo.util;

import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Utility class for file operations.
 */
@Slf4j
public class FileUtils {

    private FileUtils() {
    }

    /**
     * Ensures that the file exists by creating it if it doesn't exist.
     *
     * @param filePath the file path.
     */
    public static void ensureFileExists(final Path filePath) {
        try {
            Files.createFile(filePath);
        } catch (FileAlreadyExistsException ignored) {
            // do nothing
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }
}
