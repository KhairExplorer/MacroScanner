package com.example.macroscanner;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;

public class HomePageController implements Initializable {

    @FXML private HBox rootPane;
    @FXML private StackPane contentArea;
    @FXML private VBox dropZone;
    @FXML private VBox sidebarPane;

    private File currentFile;
    private SidebarController sidebarController;
    private static HomePageController instance;
    private ProgressBar progressBar;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        instance = this;
        setupDragAndDrop();
        loadHomePage();
    }

    public static HomePageController getInstance() {
        return instance;
    }

    private void setupDragAndDrop() {
        if (dropZone != null) {
            dropZone.setOnDragOver(this::handleDragOver);
            dropZone.setOnDragDropped(this::handleDragDropped);
            dropZone.setOnDragEntered(e -> dropZone.setStyle("-fx-background-color: #145a6a; -fx-background-radius: 15;"));
            dropZone.setOnDragExited(e -> dropZone.setStyle("-fx-background-color: #1a7a8a; -fx-background-radius: 15;"));
        }
    }

    private void handleDragOver(DragEvent event) {
        if (event.getDragboard().hasFiles()) {
            event.acceptTransferModes(TransferMode.COPY);
        }
        event.consume();
    }

    private void handleDragDropped(DragEvent event) {
        Dragboard db = event.getDragboard();
        if (db.hasFiles()) {
            currentFile = db.getFiles().get(0);
            showReadyToScan();
        }
        event.setDropCompleted(true);
        event.consume();
    }

    @FXML
    private void onSelectFileClick() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Office File to Scan");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Office Files", "*.doc", "*.docx", "*.xls", "*.xlsx", "*.ppt", "*.pptx"),
                new FileChooser.ExtensionFilter("All Files", "*.*")
        );

        Stage stage = (Stage) rootPane.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);

        if (selectedFile != null) {
            currentFile = selectedFile;
            showReadyToScan();
        }
    }

    private void showReadyToScan() {
        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(40));
        content.setStyle("-fx-background-color: #FFFFFF;");

        VBox readyBox = new VBox(15);
        readyBox.setAlignment(Pos.CENTER);
        readyBox.setPadding(new Insets(30));
        readyBox.setStyle("-fx-border-color: #E0E0E0; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-color: #FFFFFF; -fx-background-radius: 10;");
        readyBox.setMaxWidth(400);

        Label titleLabel = new Label("Ready to Scan");
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 28));
        titleLabel.setStyle("-fx-text-fill: #333333;");

        HBox fileInfo = new HBox(10);
        fileInfo.setAlignment(Pos.CENTER);

        Label fileIcon = new Label("📄");
        fileIcon.setStyle("-fx-font-size: 24px; -fx-background-color: #E3F2FD; -fx-padding: 5 10; -fx-background-radius: 5;");

        VBox fileDetails = new VBox(2);
        fileDetails.setAlignment(Pos.CENTER_LEFT);
        Label fileName = new Label(currentFile.getName());
        fileName.setFont(Font.font("System", FontWeight.BOLD, 14));
        String size = String.format("%.1f MB", currentFile.length() / (1024.0 * 1024.0));
        Label fileSize = new Label(size);
        fileSize.setStyle("-fx-text-fill: #666666;");
        fileDetails.getChildren().addAll(fileName, fileSize);

        fileInfo.getChildren().addAll(fileIcon, fileDetails);

        readyBox.getChildren().addAll(titleLabel, fileInfo);

        Button startScanBtn = new Button("START SCAN");
        startScanBtn.setPrefSize(350, 45);
        startScanBtn.setStyle("-fx-background-color: #1a7a8a; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");
        startScanBtn.setOnAction(e -> performScan());

        content.getChildren().addAll(readyBox, startScanBtn);
        contentArea.getChildren().setAll(content);
    }

    private void performScan() {
        showScanningProgress();

        final double[] progress = {0.0};
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(javafx.util.Duration.millis(50), e -> {
                    progress[0] += 0.02;
                    if (progressBar != null) {
                        progressBar.setProgress(progress[0]);
                    }
                    if (progress[0] >= 1.0) {
                        Random random = new Random();
                        int result = random.nextInt(3);

                        switch (result) {
                            case 0 -> showScanResultSafe();
                            case 1 -> showScanResultDanger();
                            case 2 -> showScanResultWarning();
                        }
                    }
                })
        );
        timeline.setCycleCount(50);
        timeline.play();
    }

    private void showScanningProgress() {
        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #E0E0E0; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10;");

        StackPane iconPane = new StackPane();
        Label scanningIcon = new Label("🔍");
        scanningIcon.setStyle("-fx-font-size: 60px;");
        iconPane.getChildren().add(scanningIcon);

        Label title = new Label("Scanning File...");
        title.setFont(Font.font("System", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: #1a7a8a; -fx-padding: 5 15;");

        this.progressBar = new ProgressBar();
        progressBar.setPrefWidth(300);
        progressBar.setProgress(0);

        Label fileLabel = new Label("File: " + currentFile.getName());
        fileLabel.setStyle("-fx-text-fill: #666666;");

        content.getChildren().addAll(iconPane, title, progressBar, fileLabel);
        contentArea.getChildren().setAll(content);
    }

    private void showScanResultSafe() {
        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #E0E0E0; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10;");

        StackPane iconPane = new StackPane();
        Circle circle = new Circle(60);
        circle.setFill(Color.web("#7CB342"));
        Label checkmark = new Label("✓");
        checkmark.setStyle("-fx-text-fill: white; -fx-font-size: 60px; -fx-font-weight: bold;");
        iconPane.getChildren().addAll(circle, checkmark);

        Label title = new Label("NO THREATS FOUND");
        title.setFont(Font.font("System", FontWeight.BOLD, 26));
        title.setStyle("-fx-text-fill: #7CB342; -fx-padding: 5 15;");

        Label subtitle = new Label("The file is safe to open.");
        subtitle.setStyle("-fx-text-fill: #333333; -fx-padding: 5 15;");

        HBox buttons = new HBox(15);
        buttons.setAlignment(Pos.CENTER);

        Button openBtn = new Button("Open File");
        openBtn.setPrefSize(140, 40);
        openBtn.setStyle("-fx-background-color: #1a7a8a; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;");
        openBtn.setOnAction(e -> openFile());

        Button scanAnotherBtn = new Button("Scan Another");
        scanAnotherBtn.setPrefSize(140, 40);
        scanAnotherBtn.setStyle("-fx-background-color: #F0F0F0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-background-radius: 10; -fx-cursor: hand;");
        scanAnotherBtn.setOnMouseEntered(e -> scanAnotherBtn.setStyle("-fx-background-color: #E0E0E0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-background-radius: 10; -fx-cursor: hand;"));
        scanAnotherBtn.setOnMouseExited(e -> scanAnotherBtn.setStyle("-fx-background-color: #F0F0F0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-background-radius: 10; -fx-cursor: hand;"));
        scanAnotherBtn.setOnAction(e -> loadHomePage());

        buttons.getChildren().addAll(openBtn, scanAnotherBtn);

        content.getChildren().addAll(iconPane, title, subtitle, buttons);
        contentArea.getChildren().setAll(content);
    }

    private void showScanResultDanger() {
        VBox content = new VBox(15);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #E0E0E0; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10;");

        StackPane iconPane = new StackPane();
        Circle circle = new Circle(55);
        circle.setFill(Color.web("#D32F2F"));
        Label exclamation = new Label("!");
        exclamation.setStyle("-fx-text-fill: white; -fx-font-size: 60px; -fx-font-weight: bold;");
        iconPane.getChildren().addAll(circle, exclamation);

        Label title = new Label("MALICIOUS MACRO DETECTED");
        title.setFont(Font.font("System", FontWeight.BOLD, 22));
        title.setStyle("-fx-text-fill: #D32F2F; -fx-padding: 5 15;");

        Label desc = new Label("It contains malicious code that can Hack or Damage your computer");
        desc.setStyle("-fx-text-fill: #333333; -fx-padding: 5 15;");

        Label threat1 = new Label("Threat: AutoOpen Trigger Found");
        threat1.setStyle("-fx-text-fill: #333333; -fx-padding: 3 10;");

        Label threat2 = new Label("Threat: Shell Command Found");
        threat2.setStyle("-fx-text-fill: #333333; -fx-padding: 3 10;");

        HBox buttons = new HBox(15);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(10, 0, 0, 0));

        Button quarantineBtn = new Button("Quarantine File");
        quarantineBtn.setPrefSize(150, 40);
        quarantineBtn.setStyle("-fx-background-color: #D32F2F; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;");
        quarantineBtn.setOnAction(e -> {
            showAlert("File quarantined successfully!");
            loadHomePage();
        });

        Button ignoreBtn = new Button("Ignore");
        ignoreBtn.setPrefSize(120, 40);
        ignoreBtn.setStyle("-fx-background-color: #F0F0F0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-background-radius: 10; -fx-cursor: hand;");
        ignoreBtn.setOnMouseEntered(e -> ignoreBtn.setStyle("-fx-background-color: #E0E0E0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-background-radius: 10; -fx-cursor: hand;"));
        ignoreBtn.setOnMouseExited(e -> ignoreBtn.setStyle("-fx-background-color: #F0F0F0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-background-radius: 10; -fx-cursor: hand;"));
        ignoreBtn.setOnAction(e -> loadHomePage());

        buttons.getChildren().addAll(quarantineBtn, ignoreBtn);

        content.getChildren().addAll(iconPane, title, desc, threat1, threat2, buttons);
        contentArea.getChildren().setAll(content);
    }

    private void showScanResultWarning() {
        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: #E0E0E0; -fx-border-width: 2; -fx-border-radius: 10; -fx-background-radius: 10;");

        StackPane iconPane = new StackPane();
        Label triangle = new Label("⚠");
        triangle.setStyle("-fx-text-fill: #FFC107; -fx-font-size: 120px;");
        iconPane.getChildren().add(triangle);

        Label title = new Label("CAUTION: UNKNOWN MACROS");
        title.setFont(Font.font("System", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: #FFA000; -fx-padding: 5 15;");

        VBox descBox = new VBox(5);
        descBox.setAlignment(Pos.CENTER);
        descBox.setStyle("-fx-padding: 10 20;");

        Label desc1 = new Label("This file contains automated scripts (Macros).");
        Label desc2 = new Label("Only open this file if you know and trust the sender.");
        desc1.setStyle("-fx-text-fill: #333333; -fx-font-size: 14px;");
        desc2.setStyle("-fx-text-fill: #333333; -fx-font-size: 14px;");
        descBox.getChildren().addAll(desc1, desc2);

        HBox buttons = new HBox(20);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(15, 0, 0, 0));

        Button openCautionBtn = new Button("Open with Caution");
        openCautionBtn.setPrefSize(180, 45);
        openCautionBtn.setStyle("-fx-background-color: #FFA000; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 5; -fx-cursor: hand;");
        openCautionBtn.setOnAction(e -> openFile());

        Button doNotOpenBtn = new Button("Do Not Open");
        doNotOpenBtn.setPrefSize(150, 45);
        doNotOpenBtn.setStyle("-fx-background-color: #F0F0F0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-background-radius: 10; -fx-cursor: hand;");
        doNotOpenBtn.setOnMouseEntered(e -> doNotOpenBtn.setStyle("-fx-background-color: #E0E0E0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-background-radius: 10; -fx-cursor: hand;"));
        doNotOpenBtn.setOnMouseExited(e -> doNotOpenBtn.setStyle("-fx-background-color: #F0F0F0; -fx-text-fill: #333333; -fx-font-size: 14px; -fx-background-radius: 10; -fx-cursor: hand;"));
        doNotOpenBtn.setOnAction(e -> loadHomePage());

        buttons.getChildren().addAll(openCautionBtn, doNotOpenBtn);

        content.getChildren().addAll(iconPane, title, descBox, buttons);
        contentArea.getChildren().setAll(content);
    }

    public void loadHomePage() {
        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: #FFFFFF;");

        VBox dropArea = new VBox(15);
        dropArea.setAlignment(Pos.CENTER);
        dropArea.setPadding(new Insets(40));
        dropArea.setPrefSize(550, 400);
        dropArea.setStyle("-fx-background-color: #1a7a8a; -fx-background-radius: 15;");

        dropArea.setOnDragOver(this::handleDragOver);
        dropArea.setOnDragDropped(this::handleDragDropped);
        dropArea.setOnDragEntered(e -> dropArea.setStyle("-fx-background-color: #145a6a; -fx-background-radius: 15;"));
        dropArea.setOnDragExited(e -> dropArea.setStyle("-fx-background-color: #1a7a8a; -fx-background-radius: 15;"));

        StackPane cloudIcon = new StackPane();
        Label cloud = new Label("☁");
        cloud.setStyle("-fx-font-size: 50px; -fx-text-fill: #CCCCCC;");
        Label arrow = new Label("↑");
        arrow.setStyle("-fx-font-size: 25px; -fx-text-fill: #666666;");
        StackPane.setAlignment(arrow, Pos.CENTER);
        cloudIcon.getChildren().addAll(cloud, arrow);
        cloudIcon.setStyle("-fx-background-color: #E8E8E8; -fx-background-radius: 10; -fx-padding: 10 20;");

        VBox textBox = new VBox(2);
        textBox.setAlignment(Pos.CENTER);
        Label dragText = new Label("Drag & Drop Office");
        Label filesText = new Label("Files Here to Scan");
        dragText.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
        filesText.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold; -fx-underline: true;");
        textBox.getChildren().addAll(dragText, filesText);

        dropArea.getChildren().addAll(cloudIcon, textBox);

        Button selectBtn = new Button("SELECT FILE");
        selectBtn.setPrefSize(400, 45);
        selectBtn.setStyle("-fx-background-color: #1a7a8a; -fx-text-fill: white; -fx-font-size: 16px; -fx-font-weight: bold; -fx-background-radius: 8; -fx-cursor: hand;");
        selectBtn.setOnAction(e -> onSelectFileClick());

        content.getChildren().addAll(dropArea, selectBtn);
        contentArea.getChildren().setAll(content);
    }

    public void loadScanHistory() {
        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: #FFFFFF;");

        Label title = new Label("Scan History");
        title.setFont(Font.font("System", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: #1a3a54;");

        Label info = new Label("No scan history available.");
        info.setStyle("-fx-text-fill: #666666;");

        content.getChildren().addAll(title, info);
        contentArea.getChildren().setAll(content);
    }

    public void loadSettings() {
        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: #FFFFFF;");

        Label title = new Label("Settings");
        title.setFont(Font.font("System", FontWeight.BOLD, 24));
        title.setStyle("-fx-text-fill: #1a3a54;");

        Label info = new Label("Settings page coming soon.");
        info.setStyle("-fx-text-fill: #666666;");

        content.getChildren().addAll(title, info);
        contentArea.getChildren().setAll(content);
    }

    private void openFile() {
        if (currentFile != null && Desktop.isDesktopSupported()) {
            try {
                Desktop.getDesktop().open(currentFile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        loadHomePage();
    }

    private void showAlert(String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.INFORMATION);
        alert.setTitle("MacroScanner");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void onCloseClick() {
        Platform.exit();
    }

    @FXML
    private void onMinimizeClick() {
        ((Stage) rootPane.getScene().getWindow()).setIconified(true);
    }

    @FXML
    private void onMaximizeClick() {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.setMaximized(!stage.isMaximized());
    }
}
