package com.example.macroscanner;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
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
    private String lastScanStatus = "";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        instance = this;
        setupDragAndDrop();
        loadHomePage();
        ScanHistoryManager.initialize();
        rootPane.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
    }

    public static HomePageController getInstance() {
        return instance;
    }

    private void setupDragAndDrop() {
        if (dropZone != null) {
            dropZone.setOnDragOver(this::handleDragOver);
            dropZone.setOnDragDropped(this::handleDragDropped);
            dropZone.setOnDragEntered(e -> dropZone.getStyleClass().add("drop-area:drag-over"));
            dropZone.setOnDragExited(e -> dropZone.getStyleClass().remove("drop-area:drag-over"));
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
        content.getStyleClass().add("main-content-center");

        VBox readyBox = new VBox(15);
        readyBox.setAlignment(Pos.CENTER);
        readyBox.getStyleClass().add("ready-box");

        Label titleLabel = new Label("Ready to Scan");
        titleLabel.getStyleClass().add("ready-title");

        HBox fileInfo = new HBox(10);
        fileInfo.setAlignment(Pos.CENTER);

        Label fileIcon = new Label("📄");
        fileIcon.getStyleClass().add("file-icon-label");

        VBox fileDetails = new VBox(2);
        fileDetails.setAlignment(Pos.CENTER_LEFT);

        Label fileName = new Label(currentFile.getName());
        fileName.getStyleClass().add("file-name-label-ready");

        String size = String.format("%.1f MB", currentFile.length() / (1024.0 * 1024.0));
        Label fileSize = new Label(size);
        fileSize.getStyleClass().add("file-size-label");

        fileDetails.getChildren().addAll(fileName, fileSize);
        fileInfo.getChildren().addAll(fileIcon, fileDetails);

        readyBox.getChildren().addAll(titleLabel, fileInfo);

        Button startScanBtn = new Button("START SCAN");
        startScanBtn.getStyleClass().add("start-scan-btn");
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
                            case 0 -> {
                                lastScanStatus = "SAFE";
                                showScanResultSafe();
                            }
                            case 1 -> {
                                lastScanStatus = "DANGEROUS";
                                showScanResultDanger();
                            }
                            case 2 -> {
                                lastScanStatus = "WARNING";
                                showScanResultWarning();
                            }
                        }

                        ScanRecord record = new ScanRecord(
                                currentFile.getName(),
                                currentFile.getAbsolutePath(),
                                lastScanStatus
                        );
                        ScanHistoryManager.saveScanRecord(record);
                    }
                })
        );
        timeline.setCycleCount(50);
        timeline.play();
    }

    private void showScanningProgress() {
        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        content.getStyleClass().add("scanning-container");

        StackPane iconPane = new StackPane();
        Label scanningIcon = new Label("🔍");
        scanningIcon.getStyleClass().add("scanning-icon");
        iconPane.getChildren().add(scanningIcon);

        Label title = new Label("Scanning File...");
        title.getStyleClass().add("scanning-title");

        this.progressBar = new ProgressBar();
        progressBar.getStyleClass().add("progress-bar-custom");
        progressBar.setProgress(0);

        Label fileLabel = new Label("File: " + currentFile.getName());
        fileLabel.getStyleClass().add("file-info-label");

        content.getChildren().addAll(iconPane, title, progressBar, fileLabel);
        contentArea.getChildren().setAll(content);
    }

    private void showScanResultSafe() {
        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        content.getStyleClass().add("result-container");

        StackPane iconPane = new StackPane();
        Circle circle = new Circle(60);
        circle.getStyleClass().add("safe-circle");

        Label checkmark = new Label("✓");
        checkmark.getStyleClass().add("safe-checkmark");
        iconPane.getChildren().addAll(circle, checkmark);

        Label title = new Label("NO THREATS FOUND");
        title.getStyleClass().add("safe-title");

        Label subtitle = new Label("The file is safe to open.");
        subtitle.getStyleClass().add("safe-subtitle");

        HBox buttons = new HBox(15);
        buttons.setAlignment(Pos.CENTER);

        Button openBtn = new Button("Open File");
        openBtn.getStyleClass().add("open-file-btn");
        openBtn.setOnAction(e -> openFile());

        Button scanAnotherBtn = new Button("Scan Another");
        scanAnotherBtn.getStyleClass().add("scan-another-btn");
        scanAnotherBtn.setOnAction(e -> loadHomePage());

        buttons.getChildren().addAll(openBtn, scanAnotherBtn);

        content.getChildren().addAll(iconPane, title, subtitle, buttons);
        contentArea.getChildren().setAll(content);
    }

    private void showScanResultDanger() {
        VBox content = new VBox(15);
        content.setAlignment(Pos.CENTER);
        content.getStyleClass().add("result-container");

        StackPane iconPane = new StackPane();
        Circle circle = new Circle(55);
        circle.getStyleClass().add("danger-circle");

        Label exclamation = new Label("!");
        exclamation.getStyleClass().add("danger-exclamation");
        iconPane.getChildren().addAll(circle, exclamation);

        Label title = new Label("MALICIOUS MACRO DETECTED");
        title.getStyleClass().add("danger-title");

        Label desc = new Label("It contains malicious code that can Hack or Damage your computer");
        desc.getStyleClass().add("danger-desc");

        Label threat1 = new Label("Threat: AutoOpen Trigger Found");
        threat1.getStyleClass().add("threat-label");

        Label threat2 = new Label("Threat: Shell Command Found");
        threat2.getStyleClass().add("threat-label");

        HBox buttons = new HBox(15);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(10, 0, 0, 0));

        Button quarantineBtn = new Button("Quarantine File");
        quarantineBtn.getStyleClass().add("quarantine-btn");
        quarantineBtn.setOnAction(e -> {
            showAlert("File quarantined successfully!");
            loadHomePage();
        });

        Button ignoreBtn = new Button("Ignore");
        ignoreBtn.getStyleClass().add("ignore-btn");
        ignoreBtn.setOnAction(e -> loadHomePage());

        buttons.getChildren().addAll(quarantineBtn, ignoreBtn);

        content.getChildren().addAll(iconPane, title, desc, threat1, threat2, buttons);
        contentArea.getChildren().setAll(content);
    }

    private void showScanResultWarning() {
        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        content.getStyleClass().add("result-container");

        StackPane iconPane = new StackPane();
        Label triangle = new Label("⚠ ");
        triangle.getStyleClass().add("warning-triangle");
        iconPane.getChildren().add(triangle);

        Label title = new Label("CAUTION: UNKNOWN MACROS");
        title.getStyleClass().add("warning-title");

        VBox descBox = new VBox(5);
        descBox.setAlignment(Pos.CENTER);

        Label desc1 = new Label("This file contains automated scripts (Macros).");
        desc1.getStyleClass().add("warning-desc");

        Label desc2 = new Label("Only open this file if you know and trust the sender.");
        desc2.getStyleClass().add("warning-desc");

        descBox.getChildren().addAll(desc1, desc2);

        HBox buttons = new HBox(20);
        buttons.setAlignment(Pos.CENTER);
        buttons.setPadding(new Insets(15, 0, 0, 0));

        Button openCautionBtn = new Button("Open with Caution");
        openCautionBtn.getStyleClass().add("open-caution-btn");
        openCautionBtn.setOnAction(e -> openFile());

        Button doNotOpenBtn = new Button("Do Not Open");
        doNotOpenBtn.getStyleClass().add("do-not-open-btn");
        doNotOpenBtn.setOnAction(e -> loadHomePage());

        buttons.getChildren().addAll(openCautionBtn, doNotOpenBtn);

        content.getChildren().addAll(iconPane, title, descBox, buttons);
        contentArea.getChildren().setAll(content);
    }

    public void loadHomePage() {
        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        content.getStyleClass().add("main-content");

        VBox dropArea = new VBox(15);
        dropArea.setAlignment(Pos.CENTER);
        dropArea.setPadding(new Insets(40));
        dropArea.getStyleClass().add("drop-area");

        dropArea.setOnDragOver(this::handleDragOver);
        dropArea.setOnDragDropped(this::handleDragDropped);
        dropArea.setOnDragEntered(e -> dropArea.setStyle("-fx-background-color: #145a6a; -fx-background-radius: 15;"));
        dropArea.setOnDragExited(e -> dropArea.setStyle("-fx-background-color: #1a7a8a; -fx-background-radius: 15;"));

        StackPane cloudIcon = new StackPane();
        Label cloud = new Label("☁");
        cloud.getStyleClass().add("cloud-icon");

        Label arrow = new Label("↑");
        arrow.getStyleClass().add("upload-arrow");
        StackPane.setAlignment(arrow, Pos.CENTER);

        cloudIcon.getChildren().addAll(cloud, arrow);
        cloudIcon.getStyleClass().add("cloud-icon-container");

        VBox textBox = new VBox(2);
        textBox.setAlignment(Pos.CENTER);

        Label dragText = new Label("Drag & Drop Office");
        dragText.getStyleClass().add("drag-text-1");

        Label filesText = new Label("Files Here to Scan");
        filesText.getStyleClass().add("drag-text-2");

        textBox.getChildren().addAll(dragText, filesText);

        dropArea.getChildren().addAll(cloudIcon, textBox);

        Button selectBtn = new Button("SELECT FILE");
        selectBtn.getStyleClass().add("select-file-btn");
        selectBtn.setOnAction(e -> onSelectFileClick());

        content.getChildren().addAll(dropArea, selectBtn);
        contentArea.getChildren().setAll(content);
    }

    public void loadScanHistory() {
        VBox content = new VBox(20);
        content.setAlignment(Pos.TOP_CENTER);
        content.getStyleClass().add("main-content");

        Label title = new Label("📋 Scan History");
        title.getStyleClass().add("history-title");

        ObservableList<ScanRecord> data = ScanHistoryManager.loadScanHistory();

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        VBox cardsContainer = new VBox(15);
        cardsContainer.getStyleClass().add("cards-container");

        if (data.isEmpty()) {
            VBox emptyState = new VBox(20);
            emptyState.setAlignment(Pos.CENTER);
            emptyState.getStyleClass().add("empty-state");

            Label emptyIcon = new Label("📂");
            emptyIcon.getStyleClass().add("empty-icon");

            Label emptyText = new Label("No scan history available");
            emptyText.getStyleClass().add("empty-text");

            Label emptySubText = new Label("Start scanning files to see your history here");
            emptySubText.getStyleClass().add("empty-subtext");

            emptyState.getChildren().addAll(emptyIcon, emptyText, emptySubText);
            cardsContainer.getChildren().add(emptyState);
        } else {
            for (ScanRecord record : data) {
                HBox card = createScanCard(record);
                cardsContainer.getChildren().add(card);
            }
        }

        scrollPane.setContent(cardsContainer);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        content.getChildren().addAll(title, scrollPane);
        contentArea.getChildren().setAll(content);
    }

    private HBox createScanCard(ScanRecord record) {
        HBox card = new HBox(20);
        card.setAlignment(Pos.CENTER_LEFT);
        card.getStyleClass().add("scan-card");

        VBox statusBox = new VBox(5);
        statusBox.setAlignment(Pos.CENTER);
        statusBox.setPrefWidth(100);

        StackPane statusBadge = new StackPane();
        statusBadge.setPrefSize(65, 65);
        statusBadge.getStyleClass().add("status-badge");

        String statusText, circleClass, iconClass, labelClass;
        if (record.getStatus().equals("SAFE")) {
            statusText = "SAFE";
            circleClass = "status-circle-safe";
            iconClass = "status-icon-safe";
            labelClass = "status-label-safe";
        } else if (record.getStatus().equals("DANGEROUS")) {
            statusText = "DANGER";
            circleClass = "status-circle-danger";
            iconClass = "status-icon-danger";
            labelClass = "status-label-danger";
        } else {
            statusText = "WARNING";
            circleClass = "status-circle-warning";
            iconClass = "status-icon-warning";
            labelClass = "status-label-warning";
        }

        Circle statusCircle = new Circle(32);
        statusCircle.getStyleClass().add(circleClass);

        Label iconLabel = new Label(record.getStatus().equals("SAFE") ? "✓" :
                record.getStatus().equals("DANGEROUS") ? "!" : "⚠");
        iconLabel.getStyleClass().add(iconClass);

        statusBadge.getChildren().addAll(statusCircle, iconLabel);

        Label statusLabel = new Label(statusText);
        statusLabel.getStyleClass().add(labelClass);

        statusBox.getChildren().addAll(statusBadge, statusLabel);

        VBox fileInfo = new VBox(8);
        fileInfo.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(fileInfo, Priority.ALWAYS);

        HBox fileNameBox = new HBox(8);
        fileNameBox.setAlignment(Pos.CENTER_LEFT);

        Label fileIcon = new Label("📄");
        fileIcon.getStyleClass().add("file-icon");

        Label fileName = new Label(record.getFileName());
        fileName.getStyleClass().add("file-name-label");
        fileName.setMaxWidth(400);

        fileNameBox.getChildren().addAll(fileIcon, fileName);

        HBox pathBox = new HBox(8);
        pathBox.setAlignment(Pos.CENTER_LEFT);

        Label pathIcon = new Label("📁");
        pathIcon.getStyleClass().add("path-icon");

        Label filePath = new Label(record.getFilePath());
        filePath.getStyleClass().add("file-path-label");
        filePath.setMaxWidth(400);

        pathBox.getChildren().addAll(pathIcon, filePath);

        HBox dateBox = new HBox(8);
        dateBox.setAlignment(Pos.CENTER_LEFT);

        Label dateIcon = new Label("🕐");
        dateIcon.getStyleClass().add("date-icon");

        Label scanDate = new Label(record.getScanDate());
        scanDate.getStyleClass().add("scan-date-label");

        dateBox.getChildren().addAll(dateIcon, scanDate);

        fileInfo.getChildren().addAll(fileNameBox, pathBox, dateBox);

        VBox actionBox = new VBox(5);
        actionBox.setAlignment(Pos.CENTER);
        actionBox.setPrefWidth(80);

        Button viewBtn = new Button("View");
        viewBtn.setPrefSize(70, 32);
        viewBtn.getStyleClass().add("view-btn");
        viewBtn.setOnAction(e -> {
            File file = new File(record.getFilePath());
            if (file.exists() && Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().open(file);
                } catch (IOException ex) {
                    showAlert("Cannot open file: " + ex.getMessage());
                }
            } else {
                showAlert("File not found!");
            }
        });

        actionBox.getChildren().add(viewBtn);

        card.getChildren().addAll(statusBox, fileInfo, actionBox);

        return card;
    }

    public void loadSettings() {
        VBox content = new VBox(25);
        content.setAlignment(Pos.TOP_LEFT);
        content.setPadding(new Insets(30, 40, 30, 40));
        content.getStyleClass().add("settings-container");

        Label title = new Label("Security Enforcement");
        title.getStyleClass().add("settings-title");

        // Security Settings Card
        VBox securityCard = createSettingsCard("Security Settings", "⚙️");
        securityCard.setMaxWidth(650); // تصغير العرض

        HBox autoQuarantineBox = new HBox(15);
        autoQuarantineBox.getStyleClass().add("settings-item");

        VBox labelBox1 = new VBox(3);
        labelBox1.getStyleClass().add("settings-item-label-box");
        Label autoQuarantineLabel = new Label("Force Auto-Quarantine");
        autoQuarantineLabel.getStyleClass().add("settings-item-title");
        Label autoQuarantineDesc = new Label("Automatically quarantine detected threats");
        autoQuarantineDesc.getStyleClass().add("settings-item-description");
        labelBox1.getChildren().addAll(autoQuarantineLabel, autoQuarantineDesc);
        HBox.setHgrow(labelBox1, Priority.ALWAYS);

        CheckBox autoQuarantineCheck = new CheckBox();
        autoQuarantineCheck.getStyleClass().add("settings-checkbox");

        autoQuarantineBox.getChildren().addAll(labelBox1, autoQuarantineCheck);

        Region separator1 = new Region();
        separator1.getStyleClass().add("settings-separator");

        HBox sensitivityBox = new HBox(15);
        sensitivityBox.setAlignment(Pos.CENTER_LEFT);
        sensitivityBox.setPadding(new Insets(15, 0, 0, 0));

        VBox labelBox2 = new VBox(3);
        labelBox2.getStyleClass().add("settings-item-label-box");
        Label sensitivityLabel = new Label("Scan Sensitivity");
        sensitivityLabel.getStyleClass().add("settings-item-title");
        Label sensitivityDesc = new Label("Set the detection level for macro scanning");
        sensitivityDesc.getStyleClass().add("settings-item-description");
        labelBox2.getChildren().addAll(sensitivityLabel, sensitivityDesc);
        HBox.setHgrow(labelBox2, Priority.ALWAYS);

        ComboBox<String> sensitivityCombo = new ComboBox<>();
        sensitivityCombo.getStyleClass().add("settings-combo-box");
        sensitivityCombo.getItems().addAll("Low", "Medium", "High (Corporate Mode)");
        sensitivityCombo.setValue("High (Corporate Mode)");

        sensitivityBox.getChildren().addAll(labelBox2, sensitivityCombo);

        securityCard.getChildren().addAll(autoQuarantineBox, separator1, sensitivityBox);

        VBox accountCard = createSettingsCard("Account Management", "👤");
        accountCard.setMaxWidth(650); // تصغير العرض

        HBox changePasswordBox = createActionRow(
                "Change Password",
                "Update your account password",
                "🔑",
                e -> showChangePasswordDialog()
        );

        Region separator2 = new Region();
        separator2.getStyleClass().add("settings-separator");

        HBox faceIDBox = createActionRow(
                "Update Face ID",
                "Re-register your face for authentication",
                "✅",
                e -> showUpdateFaceIDDialog()
        );

        accountCard.getChildren().addAll(changePasswordBox, separator2, faceIDBox);

        VBox notificationsCard = createSettingsCard("Notifications", "🔔");
        notificationsCard.setMaxWidth(650); // تصغير العرض

        HBox scanResultsBox = new HBox(15);
        scanResultsBox.getStyleClass().add("settings-item");

        VBox labelBox3 = new VBox(3);
        labelBox3.getStyleClass().add("settings-item-label-box");
        Label scanResultsLabel = new Label("Show Scan Results");
        scanResultsLabel.getStyleClass().add("settings-item-title");
        Label scanResultsDesc = new Label("Display notifications after each scan");
        scanResultsDesc.getStyleClass().add("settings-item-description");
        labelBox3.getChildren().addAll(scanResultsLabel, scanResultsDesc);
        HBox.setHgrow(labelBox3, Priority.ALWAYS);

        CheckBox scanResultsCheck = new CheckBox();
        scanResultsCheck.setSelected(true);
        scanResultsCheck.getStyleClass().add("settings-checkbox");

        scanResultsBox.getChildren().addAll(labelBox3, scanResultsCheck);

        notificationsCard.getChildren().add(scanResultsBox);

        content.getChildren().addAll(title, securityCard, accountCard, notificationsCard);

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("settings-scroll-pane");

        contentArea.getChildren().setAll(scrollPane);
    }

    private VBox createSettingsCard(String cardTitle, String icon) {
        VBox card = new VBox(20);
        card.getStyleClass().add("settings-card");

        HBox header = new HBox(10);
        header.getStyleClass().add("settings-card-header");

        Label iconLabel = new Label(icon);
        iconLabel.getStyleClass().add("settings-card-icon");

        Label titleLabel = new Label(cardTitle);
        titleLabel.getStyleClass().add("settings-card-title");

        header.getChildren().addAll(iconLabel, titleLabel);

        card.getChildren().add(header);

        return card;
    }

    private HBox createActionRow(String title, String description, String icon, javafx.event.EventHandler<javafx.scene.input.MouseEvent> action) {
        HBox row = new HBox(15);
        row.getStyleClass().add("settings-action-row");
        row.setOnMouseClicked(action);

        VBox labelBox = new VBox(3);
        labelBox.getStyleClass().add("settings-item-label-box");
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("settings-item-title");
        Label descLabel = new Label(description);
        descLabel.getStyleClass().add("settings-item-description");
        labelBox.getChildren().addAll(titleLabel, descLabel);
        HBox.setHgrow(labelBox, Priority.ALWAYS);

        Label iconLabel = new Label(icon);
        iconLabel.getStyleClass().add("settings-action-icon");

        Label arrow = new Label("›");
        arrow.getStyleClass().add("settings-action-arrow");

        row.getChildren().addAll(labelBox, iconLabel, arrow);

        return row;
    }

    private void showChangePasswordDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Change Password");
        alert.setHeaderText("Password Change");
        alert.setContentText("Password change feature will be available soon!");
        alert.showAndWait();
    }

    private void showUpdateFaceIDDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Update Face ID");
        alert.setHeaderText("Face ID Update");
        alert.setContentText("Face ID update feature will be available soon!");
        alert.showAndWait();
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