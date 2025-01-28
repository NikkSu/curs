package com.fx.curs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.sql.*;
import static com.fx.curs.DatabaseConfig.*;
public class BasketContr {


    @FXML
    private TableView<CartItem> tableView;

    @FXML
    private TableColumn<CartItem, String> nameColumn;

    @FXML
    private TableColumn<CartItem, String> categoryColumn;

    @FXML
    private TableColumn<CartItem, Integer> quantityColumn;

    @FXML
    private TableColumn<CartItem, Double> deliveryColumn;

    @FXML
    private Label SumPrice;

    @FXML
    private TableColumn<CartItem, Double> priceColumn;

    private int currentUserId;

    @FXML
    public void initialize() {
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("itemName"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        deliveryColumn.setCellValueFactory(new PropertyValueFactory<>("unitPrice"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("totalPrice"));

        loadCurrentUserId();

        loadCart();
    }

    private void loadCurrentUserId() {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT id FROM users WHERE login = ?";
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, Main.currentUser);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                currentUserId = resultSet.getInt("id");
            } else {
                System.out.println("Пользователь не найден.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void loadCart() {
        String query = "SELECT oi.item_id, i.название AS item_name, i.категория AS category, oi.quantity, " +
                "i.цена AS unit_price, (oi.quantity * i.цена) AS total_price " +
                "FROM order_items oi " +
                "JOIN items i ON oi.item_id = i.id " +
                "JOIN orders o ON oi.order_id = o.id " +
                "WHERE o.user_id = ? AND o.isPlaced = false";




        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, currentUserId);
            ResultSet resultSet = statement.executeQuery();

            ObservableList<CartItem> cartItems = FXCollections.observableArrayList();
            double totalSum = 0.0;

            while (resultSet.next()) {
                double itemTotalPrice = resultSet.getDouble("total_price");
                totalSum += itemTotalPrice;

                cartItems.add(new CartItem(
                        resultSet.getString("item_name"),
                        resultSet.getString("category"),
                        resultSet.getInt("quantity"),
                        resultSet.getDouble("unit_price"),
                        itemTotalPrice
                ));
            }

            tableView.setItems(cartItems);

            SumPrice.setText(String.format("Общая стоимость: %.2f", totalSum));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    @FXML
    private void PlaceOrder() {
        String query = "UPDATE orders SET status = 'none', isPlaced = true WHERE user_id = ? AND isPlaced = false";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, currentUserId);
            int rowsUpdated = statement.executeUpdate();

            if (rowsUpdated > 0) {
                System.out.println("Заказ оформлен!");
                loadCart();
                Main m = new Main();
                m.changeScene("MyOrders.fxml");
            } else {
                System.out.println("Корзина пуста.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @FXML
    private void GoToAdminMenu() {
        try {
            Main m = new Main();
            m.changeScene("AfterLogin.fxml");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
