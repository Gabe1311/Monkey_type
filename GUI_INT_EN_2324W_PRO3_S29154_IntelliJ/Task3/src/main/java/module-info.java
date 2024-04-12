module task3.task3 {
    requires javafx.controls;
    requires javafx.fxml;


    opens task3.task3 to javafx.fxml;
    exports task3.task3;
}