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

    private MainController mainController;

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    private void handleHome() {
        mainController.loadHomePage();
        updateButtonStyles(btnHome);
    }

    @FXML
    private void handleScanHistory() {
        mainController.loadScanHistory();
        updateButtonStyles(btnScanHistory);
    }

    @FXML
    private void handleSettings() {
        mainController.loadSettings();
        updateButtonStyles(btnSettings);
    }

    private void updateButtonStyles(Button activeButton) {
        // إزالة الستايل النشط من جميع الأزرار
        btnHome.getStyleClass().remove("active");
        btnScanHistory.getStyleClass().remove("active");
        btnSettings.getStyleClass().remove("active");

        // إضافة الستايل النشط للزر المحدد
        activeButton.getStyleClass().add("active");
    }

    @FXML
    public void initialize() {
        // تفعيل زر Home بشكل افتراضي
        btnHome.getStyleClass().add("active");
    }
}