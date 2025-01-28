package com.fx.curs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.sql.*;

public class EmployeeOrderContr {
    @FXML
    private TableView<Order> tableView;
    @FXML
    private TableColumn<Order, Integer> orderIdColumn;
    @FXML
    private TableColumn<Order, Integer> userIdColumn;
    @FXML
    private TableColumn<Order, String> productNameColumn;
    @FXML
    private TableColumn<Order, String> productLocationColumn;
    @FXML
    private TableColumn<Order, Integer> quantityColumn;
    @FXML
    private TextField orderIdField;
    @FXML
    private Button collectButton;
    @FXML
    private Label alert;

    private static final String DB_URL = "jdbc:mysql://localhost:3306/my_database";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "1903";

    @FXML
    public void initialize() {
        collectButton.setDisable(true);
        orderIdField.textProperty().addListener((observable, oldValue, newValue) ->
                collectButton.setDisable(newValue.trim().isEmpty())
        );

        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        userIdColumn.setCellValueFactory(new PropertyValueFactory<>("userId"));
        productNameColumn.setCellValueFactory(new PropertyValueFactory<>("productName"));
        productLocationColumn.setCellValueFactory(new PropertyValueFactory<>("productLocation"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        loadOrders();
    }

    private void loadOrders() {
        ObservableList<Order> orders = FXCollections.observableArrayList();
        String query = "SELECT o.id AS orderId, o.user_id AS userId, i.название AS productName, " +
                "i.place AS productLocation, oi.quantity " +
                "FROM orders o " +
                "JOIN order_items oi ON o.id = oi.order_id " +
                "JOIN items i ON oi.item_id = i.id " +
                "WHERE o.status = 'обрабатывается'";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                orders.add(new Order(
                        resultSet.getInt("orderId"),
                        resultSet.getInt("userId"),
                        resultSet.getString("productName"),
                        resultSet.getString("productLocation"),
                        resultSet.getInt("quantity")
                ));
            }
            tableView.setItems(orders);
        } catch (SQLException e) {
            alert.setText("Ошибка загрузки данных!");
            e.printStackTrace();
        }
    }

    @FXML
    private void collect() {
        String orderIdText = orderIdField.getText().trim();
        if (orderIdText.isEmpty()) {
            alert.setText("Введите ID заказа!");
            return;
        }

        try {
            int orderId = Integer.parseInt(orderIdText);
            processOrder(orderId);
        } catch (NumberFormatException e) {
            alert.setText("ID заказа должен быть числом!");
        }
    }

    private void processOrder(int orderId) {
        String checkQuery = "SELECT SUM(oi.quantity) AS orderQuantity, SUM(i.количество) AS stockQuantity " +
                "FROM orders o " +
                "JOIN order_items oi ON o.id = oi.order_id " +
                "JOIN items i ON oi.item_id = i.id " +
                "WHERE o.id = ? AND o.status = 'обрабатывается'";

        String updateOrderQuery = "UPDATE orders SET status = 'собрано' WHERE id = ?";

        String updateStockQuery = "UPDATE items i " +
                "JOIN order_items oi ON i.id = oi.item_id " +
                "SET i.количество = i.количество - oi.quantity " +
                "WHERE oi.order_id = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {

            checkStmt.setInt(1, orderId);
            ResultSet resultSet = checkStmt.executeQuery();

            if (resultSet.next()) {
                int orderQuantity = resultSet.getInt("orderQuantity");
                int stockQuantity = resultSet.getInt("stockQuantity");

                if (stockQuantity >= orderQuantity) {
                    try (PreparedStatement updateOrderStmt = connection.prepareStatement(updateOrderQuery);
                         PreparedStatement updateStockStmt = connection.prepareStatement(updateStockQuery)) {

                        updateOrderStmt.setInt(1, orderId);
                        updateStockStmt.setInt(1, orderId);

                        updateOrderStmt.executeUpdate();
                        updateStockStmt.executeUpdate();

                        alert.setText("Заказ успешно собран!");
                        loadOrders();
                    }
                } else {
                    alert.setText("Недостаточно товара на складе!");
                }
            } else {
                alert.setText("Заказ не найден или уже обработан!");
            }
        } catch (SQLException e) {
            alert.setText("Ошибка при обработке заказа!");
            e.printStackTrace();
        }
    }


    public void GoToDelivery(ActionEvent actionEvent) throws IOException {
        Main m = new Main();
        m.changeScene("EmployeeDelivery.fxml");
    }
    public void GoToAdminMenu(ActionEvent actionEvent) throws IOException {
        Main m = new Main();
        m.changeScene("EmployeeMenu.fxml");
    }

    public void GoToEmployeeCatalog(ActionEvent actionEvent) throws IOException {
        Main m = new Main();
        m.changeScene("EmployeeCatalog.fxml");
    }

}
