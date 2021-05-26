module fr.epsi {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    opens fr.epsi to javafx.fxml;
    opens fr.epsi.controllers to javafx.fxml;

    exports fr.epsi;
}
