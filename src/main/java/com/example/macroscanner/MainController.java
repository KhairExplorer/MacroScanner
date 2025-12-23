package com.example.macroscanner;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class MainController {
    @FXML
    private Label welcomeText;

    @FXML
    private Label lblMacroScanner;
    @FXML
    private Label lblWelcome;
    @FXML
    private Label lblChoose;
    @FXML
    private Button btUP;

    @FXML
    private Button btVC;

    @FXML
    private Button btFID;

    @FXML
    private VBox vBox;

    @FXML
    private ImageView imageViewM;





    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}