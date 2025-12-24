module com.example.macroscanner {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;

    opens com.example.macroscanner to javafx.fxml;
    exports com.example.macroscanner;
}
