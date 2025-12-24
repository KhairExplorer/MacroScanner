package com.example.macroscanner;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import java.io.IOException;

public class MainController {

    @FXML
    private BorderPane mainContainer;

    @FXML
    private VBox sidebarContainer;

    @FXML
    public void initialize() {
        loadSidebar();
        loadHomePage();
    }

    private void loadSidebar() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Sidebar.fxml"));
            VBox sidebar = loader.load();
            SidebarController sidebarController = loader.getController();
            sidebarController.setMainController(this);
            mainContainer.setLeft(sidebar);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadHomePage() {
        loadPage("HomePage.fxml");
    }

    public void loadScanHistory() {
        loadPage("ScanHistoryPage.fxml");
    }

    public void loadSettings() {
        loadPage("SettingsPage.fxml");
    }

    private void loadPage(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
            VBox page = loader.load();
            mainContainer.setCenter(page);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}