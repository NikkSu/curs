package com.fx.curs;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
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
import java.time.LocalTime;

public class MyAccContr {
    @FXML
    private Label hello;
    @FXML
    private Label alert;
    @FXML
    private TextField firstName;
    @FXML
    private TextField lastName;
    @FXML
    private TextField email;
    @FXML
    private TextField newPassword;
    @FXML
    private Button redactButton;
    @FXML
    private Button changePassButton;


    @FXML
    public void initialize() {
        LocalTime now = LocalTime.now();
        String greeting = now.getHour() < 12 ? "Доброе утро" :
                (now.getHour() < 18 ? "Добрый день" : "Добрый вечер");

        String userDisplayName = loadUserInfo();
        hello.setText(greeting + ", " + userDisplayName + "!");
    }


    private String loadUserInfo() {
        String displayName = Main.currentUser; // Логин по умолчанию

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT first_name FROM users WHERE login = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, Main.currentUser);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                String firstName = resultSet.getString("first_name");
                if (firstName != null && !firstName.trim().isEmpty()) {
                    displayName = firstName;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            alert.setText("Ошибка загрузки данных.");
        }

        return displayName;
    }


    public void Redact(ActionEvent actionEvent) {
        firstName.setDisable(false);
        lastName.setDisable(false);
        email.setDisable(false);
        alert.setText("Теперь вы можете редактировать данные.");
    }

    public void saveChanges(ActionEvent actionEvent) {
        String fn = firstName.getText().trim();
        String ln = lastName.getText().trim();
        String em = email.getText().trim();

        if (fn.length() < 4 || ln.length() < 4 || em.length() < 4) {
            alert.setText("Имя, фамилия и email должны быть не короче 4 символов.");
            return;
        }
        if (!em.contains("@")) {
            alert.setText("Email должен быть действительным.");
            return;
        }

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "UPDATE users SET first_name = ?, last_name = ?, email = ? WHERE login = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, fn);
            statement.setString(2, ln);
            statement.setString(3, em);
            statement.setString(4, Main.currentUser);

            statement.executeUpdate();
            alert.setText("Данные успешно сохранены.");
            firstName.setDisable(true);
            lastName.setDisable(true);
            email.setDisable(true);
        } catch (SQLException e) {
            e.printStackTrace();
            alert.setText("Ошибка сохранения данных.");
        }
    }

    public void ChangePass(ActionEvent actionEvent) {
        newPassword.setDisable(false);
        alert.setText("Введите новый пароль.");
    }

    public void savePassword(ActionEvent actionEvent) {
        String pass = newPassword.getText().trim();
        if (pass.length() < 6) {
            alert.setText("Пароль должен быть не короче 6 символов.");
            return;
        }

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "UPDATE users SET password = ? WHERE login = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, pass);
            statement.setString(2, Main.currentUser);

            statement.executeUpdate();
            alert.setText("Пароль успешно изменен.");
            newPassword.clear();
            newPassword.setDisable(true);
        } catch (SQLException e) {
            e.printStackTrace();
            alert.setText("Ошибка изменения пароля.");
        }
    }

    public void GoBack(ActionEvent actionEvent) throws IOException {
        Main m = new Main();
        m.changeScene("Afterlogin.fxml");
    }
}

