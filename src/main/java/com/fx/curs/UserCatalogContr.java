package com.fx.curs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.sql.*;

public class UserCatalogContr {

    private final String DB_URL = "jdbc:mysql://localhost:3306/my_database";
    private final String DB_USER = "root";
    private final String DB_PASSWORD = "1903";

    @FXML
    private TableView<Item> tableView;

    @FXML
    private TableColumn<Item, Integer> idColumn;

    @FXML
    private TableColumn<Item, String> nameColumn;

    @FXML
    private TableColumn<Item, String> categoryColumn;

    @FXML
    private TableColumn<Item, Integer> quantityColumn;

    @FXML
    private TableColumn<Item, Integer> deliveryColumn;

    @FXML
    private TableColumn<Item, Double> priceColumn;

    @FXML
    private TextField searchField;

    @FXML
    public void initialize() {
        // Устанавливаем привязку столбцов таблицы к свойствам объекта Item
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        deliveryColumn.setCellValueFactory(new PropertyValueFactory<>("delivery"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));


        loadDataFromDatabase();
    }

    private void loadDataFromDatabase() {
        String query = "SELECT id, название, категория, количество, доставляется, цена FROM items";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            ObservableList<Item> items = FXCollections.observableArrayList();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("название");
                String category = resultSet.getString("категория");
                int quantity = resultSet.getInt("количество");
                int delivery = resultSet.getInt("доставляется");
                double price = resultSet.getDouble("цена");

                items.add(new Item(id, name, category, quantity, delivery, price));
            }

            tableView.setItems(items);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void searchById(ActionEvent event) {
        String searchText = searchField.getText().trim();
        if (!searchText.matches("\\d+")) {
            return;
        }

        String query = "SELECT id, название, категория, количество, доставляется, цена FROM items WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, Integer.parseInt(searchText));
            executeSearch(preparedStatement);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void searchByName(ActionEvent event) {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty()) {
            return;
        }

        String query = "SELECT id, название, категория, количество, доставляется, цена FROM items WHERE название LIKE ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, "%" + searchText + "%");
            executeSearch(preparedStatement);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void searchByCategory(ActionEvent event) {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty()) {
            return;
        }

        String query = "SELECT id, название, категория, количество, доставляется, цена FROM items WHERE категория LIKE ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, "%" + searchText + "%");
            executeSearch(preparedStatement);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void executeSearch(PreparedStatement preparedStatement) throws SQLException {
        try (ResultSet resultSet = preparedStatement.executeQuery()) {
            ObservableList<Item> items = FXCollections.observableArrayList();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("название");
                String category = resultSet.getString("категория");
                int quantity = resultSet.getInt("количество");
                int delivery = resultSet.getInt("доставляется");
                double price = resultSet.getDouble("цена");

                items.add(new Item(id, name, category, quantity, delivery, price));
            }

            tableView.setItems(items);
        }
    }

    @FXML
    private void clearSearch(ActionEvent event) {
        searchField.clear();
        loadDataFromDatabase();
    }

    @FXML
    public void GoToAdminMenu(ActionEvent actionEvent) throws IOException {
        Main m = new Main();
        m.changeScene("AfterLogin.fxml");
    }


}
