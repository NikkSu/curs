package com.fx.curs;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import static com.fx.curs.DatabaseConfig.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegistrContr {



    @FXML
    private TextField username;

    @FXML
    private PasswordField password;

    @FXML
    private Button login;

    @FXML
    private Button reg;

    @FXML
    private Label wrongLogIn;


    public void usereg(ActionEvent actionEvent) throws IOException {
        String user = username.getText();
        String pass = password.getText();

        // Проверка на пустые поля
        if (user.isEmpty() || pass.isEmpty()) {
            wrongLogIn.setText("Please enter all fields!");
            return;
        }

        // Проверка длины логина
        if (user.length() < 6) {
            wrongLogIn.setText("Username must be at least 6 characters!");
            return;
        }

        // Проверка длины пароля
        if (pass.length() < 6) {
            wrongLogIn.setText("Password must be at least 6 characters!");
            return;
        }

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String checkUserQuery = "SELECT * FROM users WHERE login = ?";
            PreparedStatement checkStmt = connection.prepareStatement(checkUserQuery);
            checkStmt.setString(1, user);
            ResultSet resultSet = checkStmt.executeQuery();

            if (resultSet.next()) {

                wrongLogIn.setText("Username already exists!");
            } else {
                String insertQuery = "INSERT INTO users (login, password, isadmin) VALUES (?, ?, ?)";
                PreparedStatement insertStmt = connection.prepareStatement(insertQuery);
                insertStmt.setString(1, user);
                insertStmt.setString(2, pass);
                insertStmt.setBoolean(3, false);
                int rowsInserted = insertStmt.executeUpdate();
                if (rowsInserted > 0) {
                    wrongLogIn.setText("Registration successful!");
                    Main m = new Main();
                    m.changeScene("Login.fxml");
                } else {
                    wrongLogIn.setText("Failed to register. Try again.");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            wrongLogIn.setText("Database error!");
        }
    }

    public void userlogin(ActionEvent actionEvent) throws IOException {
        Main m = new Main();
        m.changeScene("Login.fxml");
    }
}
