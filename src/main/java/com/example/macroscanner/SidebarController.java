package com.example.macroscanner;

import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class SidebarController {

    @FXML
    private Button btnHome;

    @FXML
    private Button btnScanHistory;

    @FXML
    private Button btnSettings;

    @FXML
    private void handleHome() {
        HomePageController.getInstance().loadHomePage();
        updateButtonStyles(btnHome);
    }

    @FXML
    private void handleScanHistory() {
        HomePageController.getInstance().loadScanHistory();
        updateButtonStyles(btnScanHistory);
    }

    @FXML
    private void handleSettings() {
        HomePageController.getInstance().loadSettings();
        updateButtonStyles(btnSettings);
    }

    private void updateButtonStyles(Button activeButton) {
        btnHome.getStyleClass().remove("active");
        btnScanHistory.getStyleClass().remove("active");
        btnSettings.getStyleClass().remove("active");

        activeButton.getStyleClass().add("active");
    }

    @FXML
    public void initialize() {
        btnHome.getStyleClass().add("active");
    }
}