package com.fx.curs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.StringConverter;


import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.function.BiConsumer;

public class AccountsContr {
    @FXML
    private TableColumn<User, Boolean> isEmployeeColumn;
    @FXML
    private TableView<User> tableView;

    @FXML
    private TableColumn<User, Integer> idColumn;

    @FXML
    private TableColumn<User, String> loginColumn;

    @FXML
    private TableColumn<User, String> passwordColumn;

    @FXML
    private TableColumn<User, Boolean> isAdminColumn;

    @FXML
    private TableColumn<User, String> emailColumn;

    private final String DB_URL = "jdbc:mysql://localhost:3306/my_database";
    private final String DB_USER = "root";
    private final String DB_PASSWORD = "1903";

    @FXML
    public void initialize() {
        // Привязка данных к столбцам
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        loginColumn.setCellValueFactory(new PropertyValueFactory<>("login"));
        passwordColumn.setCellValueFactory(new PropertyValueFactory<>("password"));
        isAdminColumn.setCellValueFactory(cellData -> cellData.getValue().isAdminProperty());
        isEmployeeColumn.setCellValueFactory(cellData -> cellData.getValue().isEmployeeProperty());
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));

        tableView.setEditable(true);

        setupEditableColumn(loginColumn, User::setLogin);
        setupEditableColumn(passwordColumn, User::setPassword);
        setupEditableColumn(emailColumn, User::setEmail);

        isAdminColumn.setCellFactory(CheckBoxTableCell.forTableColumn(isAdminColumn));
        isAdminColumn.setOnEditCommit(event -> {
            User user = event.getRowValue();
            user.setIsAdmin(event.getNewValue());
            saveUserToDatabase(user);
        });

        isEmployeeColumn.setCellFactory(CheckBoxTableCell.forTableColumn(isEmployeeColumn));
        isEmployeeColumn.setOnEditCommit(event -> {
            User user = event.getRowValue();
            user.setIsEmployee(event.getNewValue());
            saveUserToDatabase(user);
        });

        loadUserData();
    }

    private <T> void setupEditableColumn(TableColumn<User, T> column, BiConsumer<User, T> setter) {
        column.setCellFactory(TextFieldTableCell.forTableColumn(new StringConverter<>() {
            @Override
            public String toString(T object) {
                return object == null ? "" : object.toString();
            }

            @Override
            public T fromString(String string) {
                return (T) string;
            }
        }));

        column.setOnEditCommit(event -> {
            User user = event.getRowValue();
            T newValue = event.getNewValue();
            setter.accept(user, newValue);
            saveUserToDatabase(user);
        });
    }

    private void saveUserToDatabase(User user) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String updateQuery = "UPDATE users SET login = ?, password = ?, isadmin = ?, isemployee = ?, email = ? WHERE id = ?";
            PreparedStatement statement = connection.prepareStatement(updateQuery);
            statement.setString(1, user.getLogin());
            statement.setString(2, user.getPassword());
            statement.setBoolean(3, user.getIsAdmin());
            statement.setBoolean(4, user.getIsEmployee());
            statement.setString(5, user.getEmail());
            statement.setInt(6, user.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadUserData() {
        ObservableList<User> users = FXCollections.observableArrayList();

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM users";
            PreparedStatement statement = connection.prepareStatement(query);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String login = resultSet.getString("login");
                String password = resultSet.getString("password");
                boolean isAdmin = resultSet.getBoolean("isadmin");
                boolean isEmployee = resultSet.getBoolean("isemployee");
                String email = resultSet.getString("email");

                User user = new User(id, login, password, isAdmin, email, isEmployee);
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        tableView.setItems(users);
    }


    @FXML
    public void GoToAdminMenu(ActionEvent actionEvent) throws IOException {
        Main m = new Main();
        m.changeScene("AdminMenu.fxml");
    }

    public void UserLogOut(ActionEvent actionEvent) throws IOException{
        Main m = new Main();
        m.changeScene("Afterlogin.fxml");
    }
    public void GoToAccounts(ActionEvent actionEvent) throws IOException{
        Main m = new Main();
        m.changeScene("Accounts.fxml");
    }
    public void GoToCatalog  (ActionEvent actionEvent) throws IOException{
        Main m = new Main();
        m.changeScene("Catalog.fxml");
    }
    @FXML
    private void GoToRedactor(ActionEvent event)throws IOException{
        Main m = new Main();
        m.changeScene("Redactor.fxml");
    }
    public void GoToOrders(ActionEvent actionEvent) throws IOException {
        Main m = new Main();
        m.changeScene("Order.fxml");
    }

    public void GoToAdminOrders(ActionEvent actionEvent) throws IOException {
        Main m = new Main();
        m.changeScene("AdminOrders.fxml");
    }
}
