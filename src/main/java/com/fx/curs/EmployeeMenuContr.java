package com.fx.curs;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EmployeeMenuContr {

    @FXML
    private Button AdminButton;

    @FXML
    private Button EmployeeButton;

    private final String DB_URL = "jdbc:mysql://localhost:3306/my_database";
    private final String DB_USER = "root";
    private final String DB_PASSWORD = "1903";





    public void GoToAfterLogin(ActionEvent actionEvent) throws IOException {
        Main m = new Main();
        m.changeScene("Afterlogin.fxml");
    }
    public void GoToAdminMenu(ActionEvent actionEvent) throws IOException {
        Main m = new Main();
        m.changeScene("AdminMenu.fxml");
    }
    public void GoToEmployeeOrder(ActionEvent actionEvent) throws IOException {
        Main m = new Main();
        m.changeScene("EmployeeOrder.fxml");
    }
    public void GoToEmployeeCatalog(ActionEvent actionEvent) throws IOException {
        Main m = new Main();
        m.changeScene("EmployeeCatalog.fxml");
    }
    public void GoToEmployeeMenu(ActionEvent actionEvent) throws IOException {
        Main m = new Main();
        m.changeScene("EmployeeMenu.fxml");
    }
    public void GoToDelivery(ActionEvent actionEvent) throws IOException {
        Main m = new Main();
        m.changeScene("EmployeeDelivery.fxml");
    }
}
