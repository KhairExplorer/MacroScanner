module com.example.macroscanner {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.macroscanner to javafx.fxml;
    exports com.example.macroscanner;
}