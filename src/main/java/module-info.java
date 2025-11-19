module com.ljuslin.tinaljuslin_oop2_uppg1_withmaven {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;


    opens com.ljuslin.tinaljuslin_oop2_uppg1_withmaven to javafx.fxml;
    exports com.ljuslin.tinaljuslin_oop2_uppg1_withmaven;
}