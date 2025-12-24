package com.example.macroscanner;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class HomePageController {

    @FXML
    private StackPane dropArea;

    @FXML
    public void initialize() {
        setupDragAndDrop();
    }

    @FXML
    private void handleSelectFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Office File");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Office Files",
                        "*.docx", "*.doc", "*.xlsx", "*.xls", "*.pptx", "*.ppt"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        File selectedFile = fileChooser.showOpenDialog(dropArea.getScene().getWindow());

        if (selectedFile != null) {
            scanFile(selectedFile);
        }
    }

    private void setupDragAndDrop() {
        dropArea.setOnDragOver(event -> {
            if (event.getGestureSource() != dropArea && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
            }
            event.consume();
        });

        dropArea.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;

            if (db.hasFiles()) {
                File file = db.getFiles().get(0);
                scanFile(file);
                success = true;
            }

            event.setDropCompleted(success);
            event.consume();
        });
    }

    private void scanFile(File file) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("File Selected");
        alert.setHeaderText("Scanning File");
        alert.setContentText("File: " + file.getName() + "\nScanning for macros...");
        alert.showAndWait();
    }
}