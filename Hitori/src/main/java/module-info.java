module hitori {
    requires java.scripting;
    requires javafx.controls;
    requires org.apache.logging.log4j;
    opens hitori to javafx.fxml;
    exports hitori;
}