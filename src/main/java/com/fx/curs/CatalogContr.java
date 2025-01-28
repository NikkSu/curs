package com.fx.curs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.awt.*;
import java.io.IOException;
import java.sql.*;

public class CatalogContr {

    private final String DB_URL = "jdbc:mysql://localhost:3306/my_database";
    private final String DB_USER = "root";
    private final String DB_PASSWORD = "1903";

    @FXML
    private TableColumn<Item, String> placeColumn;

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
    private TextField nameField;

    private void loadDataFromDatabase() {
        String query = "SELECT id, название, категория, количество, доставляется, цена, place FROM items";


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
                String place = resultSet.getString("place");

                items.add(new Item(id, name, category, quantity, delivery, price, place));

            }

            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
            categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
            quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
            deliveryColumn.setCellValueFactory(new PropertyValueFactory<>("delivery"));
            priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

            tableView.setItems(items);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private TextField nameFind;

    @FXML
    private void Find(ActionEvent event) {
        String searchText = nameField.getText().trim();

        if (searchText.isEmpty()) {
            return;
        }

        String query = "SELECT id, название, категория, количество, доставляется, цена, place FROM items WHERE название LIKE ?";


        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, "%" + searchText + "%");
            ResultSet resultSet = ((PreparedStatement) preparedStatement).executeQuery();

            ObservableList<Item> items = FXCollections.observableArrayList();

            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("название");
                String category = resultSet.getString("категория");
                int quantity = resultSet.getInt("количество");
                int delivery = resultSet.getInt("доставляется");
                double price = resultSet.getDouble("цена");
                String place = resultSet.getString("place");
                items.add(new Item(id, name, category, quantity, delivery, price, place));

            }

            tableView.setItems(items);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void clearr(ActionEvent event) {
        nameField.clear();

        loadDataFromDatabase();
    }

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        deliveryColumn.setCellValueFactory(new PropertyValueFactory<>("delivery"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        placeColumn.setCellValueFactory(new PropertyValueFactory<>("place"));

        loadDataFromDatabase();
    }


    public void GoToAdminMenu(ActionEvent actionEvent) throws IOException{
        Main m = new Main();
        m.changeScene("AdminMenu.fxml");
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
