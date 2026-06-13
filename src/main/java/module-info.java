module com.proyecto.musicgofx {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    opens com.proyecto.musicgofx to javafx.fxml;
    exports com.proyecto.musicgofx;
}