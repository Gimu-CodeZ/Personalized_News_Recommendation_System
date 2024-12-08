module com.example.ood_cw_new {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.json;
    requires java.desktop;
    requires java.sql;


    opens com.example.ood_cw_new to javafx.fxml;
    exports com.example.ood_cw_new;
}