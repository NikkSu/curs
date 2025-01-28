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

public class AfterLoginContr {

    @FXML
    private Button AdminButton;

    @FXML
    private Button EmployeeButton;

    private final String DB_URL = "jdbc:mysql://localhost:3306/my_database";
    private final String DB_USER = "root";
    private final String DB_PASSWORD = "1903";

    @FXML
    public void initialize() {
        checkPrivileges();
    }

    private void checkPrivileges() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT isadmin, isemployee FROM users WHERE login = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, Main.currentUser);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                boolean isAdmin = resultSet.getBoolean("isadmin");
                boolean isEmployee = resultSet.getBoolean("isemployee");

                AdminButton.setVisible(isAdmin);
                EmployeeButton.setVisible(isEmployee);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void UserLogOut(ActionEvent actionEvent) throws IOException {
        Main m = new Main();
        m.changeScene("Login.fxml");
    }

    public void GoToAdminMenu(ActionEvent actionEvent) throws IOException {
        Main m = new Main();
        m.changeScene("AdminMenu.fxml");
    }

    public void GoToBasket(ActionEvent actionEvent) throws IOException {
        Main m = new Main();
        m.changeScene("Basket.fxml");
    }

    public void MyAcc(ActionEvent actionEvent) throws IOException {
        Main m = new Main();
        m.changeScene("MyAcc.fxml");
    }

    public void GoToOrderCreator(ActionEvent actionEvent) throws IOException {
        Main m = new Main();
        m.changeScene("OrderCreator.fxml");
    }

    public void GoToMyOrders(ActionEvent actionEvent) throws IOException {
        Main m = new Main();
        m.changeScene("MyOrders.fxml");
    }

    public void GoToEmployeeMenu(ActionEvent actionEvent) throws IOException {
        Main m = new Main();
        m.changeScene("EmployeeMenu.fxml");
    }
}
