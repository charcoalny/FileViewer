module com.demo.fileviewer {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.demo.fileviewer to javafx.fxml;
    exports com.demo.fileviewer;
}