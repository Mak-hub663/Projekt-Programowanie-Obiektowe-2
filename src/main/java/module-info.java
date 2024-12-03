module com.example.kolko {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;

    opens com.example.kolko to javafx.fxml;
    exports com.example.kolko;
}