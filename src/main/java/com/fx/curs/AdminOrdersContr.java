package com.fx.curs;


import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Duration;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AdminOrdersContr {

    private final String DB_URL = "jdbc:mysql://localhost:3306/my_database";
    private final String DB_USER = "root";
    private final String DB_PASSWORD = "1903";

    @FXML
    private TableView<OrderSummary> ordersTableView;

    @FXML
    private TableColumn<OrderSummary, Integer> orderIdColumn;

    @FXML
    private TableColumn<OrderSummary, String> orderDateColumn;

    @FXML
    private TableColumn<OrderSummary, Integer> itemCountColumn;

    @FXML
    private TableColumn<OrderSummary, Double> totalPriceColumn;

    @FXML
    private TableColumn<OrderSummary, String> statusColumn;

    @FXML
    private Button applyButton;

    @FXML
    private Label alert;

    @FXML
    private Label sumPriceLabel;

    private int currentUserId;

    @FXML
    public void initialize() {
        orderIdColumn.setCellValueFactory(new PropertyValueFactory<>("orderId"));
        orderDateColumn.setCellValueFactory(new PropertyValueFactory<>("orderDate"));
        itemCountColumn.setCellValueFactory(new PropertyValueFactory<>("itemCount"));
        totalPriceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        ordersTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {

        });

        loadCurrentUserId();
        loadOrdersFromDatabase();
    }


    private void loadCurrentUserId() {
        String query = "SELECT id FROM users WHERE login = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, Main.currentUser);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                currentUserId = resultSet.getInt("id");
            } else {
                showTemporaryMessage("Ошибка: пользователь не найден.");
            }

        } catch (SQLException e) {
            showTemporaryMessage("Ошибка при получении ID пользователя.");
            e.printStackTrace();
        }
    }

    private void loadOrdersFromDatabase() {
        String query = """
            SELECT o.id AS order_id, o.order_date, COUNT(oi.item_id) AS item_count, 
                   SUM(oi.quantity * i.цена) AS total_price, o.status
            FROM orders o
            JOIN order_items oi ON o.id = oi.order_id
            JOIN items i ON oi.item_id = i.id
            WHERE  o.isPlaced = 1
            GROUP BY o.id
            """;

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            ResultSet resultSet = statement.executeQuery();

            ObservableList<OrderSummary> orderSummaries = ordersTableView.getItems();
            orderSummaries.clear(); // Очищаем текущие данные
            double totalSum = 0;

            while (resultSet.next()) {
                int orderId = resultSet.getInt("order_id");
                String orderDate = resultSet.getString("order_date");
                int itemCount = resultSet.getInt("item_count");
                double totalPrice = resultSet.getDouble("total_price");
                String status = resultSet.getString("status");

                totalSum += totalPrice;

                orderSummaries.add(new OrderSummary(orderId, orderDate, itemCount, totalPrice, status));
            }

            sumPriceLabel.setText("Общая сумма: " + String.format("%.2f", totalSum) + " руб.");

        } catch (SQLException e) {
            showTemporaryMessage("Ошибка при загрузке заказов.");
            e.printStackTrace();
        }
    }


    @FXML
    private void applyOrder(ActionEvent event) {
        OrderSummary selectedOrder = ordersTableView.getSelectionModel().getSelectedItem();
        if (selectedOrder == null) {
            showTemporaryMessage("Выберите заказ для применения.");
            return;
        }

        if (!"none".equals(selectedOrder.getStatus())) {
            showTemporaryMessage("Статус заказа уже изменён.");
            return;
        }

        String query = "UPDATE orders SET status = 'обрабатывается', order_date = ? WHERE id = ? AND user_id = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            statement.setString(1, currentDate);
            statement.setInt(2, selectedOrder.getOrderId());
            statement.setInt(3, currentUserId);

            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                showTemporaryMessage("Статус заказа обновлён.");
                loadOrdersFromDatabase();
            } else {
                showTemporaryMessage("Ошибка при обновлении заказа.");
            }

        } catch (SQLException e) {
            showTemporaryMessage("Ошибка при обновлении заказа.");
            e.printStackTrace();
        }
    }


    private void showTemporaryMessage(String message) {
        alert.setText(message);


        Timeline timeline = new Timeline(new KeyFrame(
                Duration.seconds(3),
                evt -> alert.setText("")
        ));
        timeline.setCycleCount(1);
        timeline.play();
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
