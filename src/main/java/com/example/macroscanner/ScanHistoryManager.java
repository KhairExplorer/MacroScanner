package com.example.macroscanner;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.*;
import java.nio.file.*;

public class ScanHistoryManager {
    private static final String HISTORY_FILE = "scan_history.txt";
    private static final Path HISTORY_PATH = Paths.get(System.getProperty("user.home"), ".macroscanner", HISTORY_FILE);

    public static void initialize() {
        try {
            Files.createDirectories(HISTORY_PATH.getParent());
            if (!Files.exists(HISTORY_PATH)) {
                Files.createFile(HISTORY_PATH);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveScanRecord(ScanRecord record) {
        initialize();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HISTORY_PATH.toFile(), true))) {
            writer.write(record.toFileFormat());
            writer.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ObservableList<ScanRecord> loadScanHistory() {
        ObservableList<ScanRecord> records = FXCollections.observableArrayList();
        initialize();

        try (BufferedReader reader = new BufferedReader(new FileReader(HISTORY_PATH.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                ScanRecord record = ScanRecord.fromFileFormat(line);
                if (record != null) {
                    records.add(record);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return records;
    }

    public static void clearHistory() {
        try {
            Files.write(HISTORY_PATH, new byte[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}