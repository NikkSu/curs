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

public class LogIn  {



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



    public void userlogin(ActionEvent actionEvent) throws IOException {
        if (username.getText().isEmpty() || password.getText().isEmpty()) {
            wrongLogIn.setText("Please enter all fields!");
            return;
        }

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM users WHERE login = ? AND password = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, username.getText());
            statement.setString(2, password.getText());

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Main.currentUser = username.getText(); // Сохраняем текущего пользователя
                Main m = new Main();
                m.changeScene("Afterlogin.fxml");
            } else {
                wrongLogIn.setText("Wrong Username or Password!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            wrongLogIn.setText("Database connection error!");
        }
    }


    private void checkLogin() throws IOException {
        if (username.getText().isEmpty() || password.getText().isEmpty()) {
            wrongLogIn.setText("Please enter all fields!");
            return;
        }

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {

            String query = "SELECT * FROM users WHERE login = ? AND password = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, username.getText());
            preparedStatement.setString(2, password.getText());

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                wrongLogIn.setText("Success!");
                Main m = new Main();
                m.changeScene("Afterlogin.fxml");
            } else {
                wrongLogIn.setText("Wrong Username or Password!");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            wrongLogIn.setText("Database connection error!");
        }
    }

    public void usereg(ActionEvent actionEvent) throws IOException {
        Main m = new Main();
        m.changeScene("Registr.fxml");
    }
}
