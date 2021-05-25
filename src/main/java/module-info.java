module fr.epsi {
    requires javafx.controls;
    requires javafx.fxml;

    opens fr.epsi to javafx.fxml;
    exports fr.epsi;
}