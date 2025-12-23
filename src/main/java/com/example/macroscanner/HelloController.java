package com.example.macroscanner;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class HelloController {
    @FXML
    private Label welcomeText;

    private Label lblMacroScanner;

    private Label lblWelcome;

    private Label lblChoose;

    private Button btUP;

    private Button btVC;

    private Button btFID;

    private VBox vBox;

    private ImageView imageViewM;





    @FXML
    protected void onHelloButtonClick() {
        welcomeText.setText("Welcome to JavaFX Application!");
    }
}