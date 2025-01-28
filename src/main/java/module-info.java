module com.fx.curs {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires java.sql;
    requires mysql.connector.java;


    opens com.fx.curs to javafx.fxml;
    exports com.fx.curs;
}