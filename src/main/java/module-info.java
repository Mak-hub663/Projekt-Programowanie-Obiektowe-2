module com.example.kolko {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.fasterxml.jackson.databind;

    opens com.example.kolko to com.fasterxml.jackson.databind; // This line is important
    exports com.example.kolko;
}
