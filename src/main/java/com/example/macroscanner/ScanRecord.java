package com.example.macroscanner;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScanRecord {
    private final StringProperty fileName;
    private final StringProperty filePath;
    private final StringProperty status;
    private final StringProperty scanDate;

    public ScanRecord(String fileName, String filePath, String status, String scanDate) {
        this.fileName = new SimpleStringProperty(fileName);
        this.filePath = new SimpleStringProperty(filePath);
        this.status = new SimpleStringProperty(status);
        this.scanDate = new SimpleStringProperty(scanDate);
    }

    public ScanRecord(String fileName, String filePath, String status) {
        this(fileName, filePath, status, LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    public String getFileName() { return fileName.get(); }
    public void setFileName(String value) { fileName.set(value); }
    public StringProperty fileNameProperty() { return fileName; }

    public String getFilePath() { return filePath.get(); }
    public void setFilePath(String value) { filePath.set(value); }
    public StringProperty filePathProperty() { return filePath; }

    public String getStatus() { return status.get(); }
    public void setStatus(String value) { status.set(value); }
    public StringProperty statusProperty() { return status; }

    public String getScanDate() { return scanDate.get(); }
    public void setScanDate(String value) { scanDate.set(value); }
    public StringProperty scanDateProperty() { return scanDate; }

    public String toFileFormat() {
        return fileName.get() + "|" + filePath.get() + "|" + status.get() + "|" + scanDate.get();
    }

    public static ScanRecord fromFileFormat(String line) {
        String[] parts = line.split("\\|");
        if (parts.length == 4) {
            return new ScanRecord(parts[0], parts[1], parts[2], parts[3]);
        }
        return null;
    }
}