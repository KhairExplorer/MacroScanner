package com.example.macroscanner;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML private VBox rootPane;
    @FXML private Button btnUsernamePassword;
    @FXML private Button btnVerificationCode;
    @FXML private Button btnFaceID;
    @FXML private TextField txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private TextField txtVerificationCode;
    @FXML private Label lblError;
    @FXML private Label lblFaceStatus;

    private static final String DEMO_USERNAME = "admin";
    private static final String DEMO_PASSWORD = "1234";
    private static final String DEMO_CODE = "123456";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (lblError != null) lblError.setVisible(false);
    }

    @FXML
    private void onUsernamePasswordClick() {
        switchTo("LoginUsernamePassword.fxml");
    }

    @FXML
    private void onVerificationCodeClick() {
        switchTo("LoginVerificationCode.fxml");
    }

    @FXML
    private void onFaceIDClick() {
        switchTo("LoginFaceID.fxml");
    }

    @FXML
    private void onBackClick() {
        switchTo("MainScene.fxml");
    }

    @FXML
    private void onLoginClick() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Please enter both username and password");
            return;
        }

        if (username.equals(DEMO_USERNAME) && password.equals(DEMO_PASSWORD)) {
            switchTo("Verified.fxml");
        } else {
            showError("Invalid username or password");
        }
    }

    @FXML
    private void onVerifyCodeClick() {
        String code = txtVerificationCode.getText().trim();

        if (code.isEmpty()) {
            showError("Please enter the verification code");
            return;
        }

        if (code.equals(DEMO_CODE)) {
            switchTo("Verified.fxml");
        } else {
            showError("Invalid verification code");
        }
    }

    @FXML
    private void onFaceVerifyClick() {
        if (lblFaceStatus != null) {
            lblFaceStatus.setText("Detecting face...");
            javafx.animation.Timeline timeline = new javafx.animation.Timeline(
                    new javafx.animation.KeyFrame(javafx.util.Duration.seconds(0.5), e -> lblFaceStatus.setText("Analyzing...")),
                    new javafx.animation.KeyFrame(javafx.util.Duration.seconds(1.0), e -> lblFaceStatus.setText("Verifying...")),
                    new javafx.animation.KeyFrame(javafx.util.Duration.seconds(1.5), e -> switchTo("Verified.fxml"))
            );
            timeline.play();
        }
    }

    @FXML
    private void onContinueClick() {
        try {
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.close();

            Stage newStage = new Stage();
            newStage.initStyle(StageStyle.UNDECORATED);
            newStage.setTitle("MacroScanner");

            FXMLLoader loader = new FXMLLoader(HelloApplication.class.getResource("HomePage.fxml"));
            Scene scene = new Scene(loader.load());
            scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
            newStage.setScene(scene);
            newStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    private void showError(String message) {
        if (lblError != null) {
            lblError.setText(message);
            lblError.setVisible(true);
        }
    }

    private void switchTo(String fxml) {
        try {
            HelloApplication.switchScene(fxml);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
