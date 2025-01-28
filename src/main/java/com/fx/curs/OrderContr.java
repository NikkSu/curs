package com.fx.curs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.io.IOException;
import java.sql.*;

public class OrderContr {
    private final String DB_URL = "jdbc:mysql://localhost:3306/my_database";
    private final String DB_USER = "root";
    private final String DB_PASSWORD = "1903";
    @FXML
    private Label priceField;

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
    private Label alert;

    @FXML
    private TextField OrderId;

    @FXML
    private TextField quantity;

    @FXML
    private Button TakeOrderButton;

    public void UserLogOut(ActionEvent actionEvent) throws IOException {
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
    @FXML
    private void updatePrice() {
        String orderIdText = OrderId.getText();
        String quantityText = quantity.getText();

        if (orderIdText.isEmpty() || quantityText.isEmpty()) {
            priceField.setText("Цена: $0.00");
            return;
        }

        int orderId;
        int orderQuantity;

        try {
            orderId = Integer.parseInt(orderIdText);
            orderQuantity = Integer.parseInt(quantityText);

            if (orderQuantity <= 0) {
                priceField.setText("Цена: $0.00");
                return;
            }

            String query = "SELECT цена FROM items WHERE id = ?";
            try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                 PreparedStatement preparedStatement = connection.prepareStatement(query)) {

                preparedStatement.setInt(1, orderId);
                ResultSet resultSet = preparedStatement.executeQuery();

                if (resultSet.next()) {
                    double pricePerUnit = resultSet.getDouble("цена");
                    double totalPrice = pricePerUnit * orderQuantity;
                    priceField.setText(String.format("Цена: $%.2f", totalPrice));
                } else {
                    priceField.setText("Цена: $0.00");
                }

            } catch (SQLException e) {
                e.printStackTrace();
                priceField.setText("Ошибка при расчете!");
            }

        } catch (NumberFormatException e) {
            priceField.setText("Некорректный ввод!");
        }
    }
    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        deliveryColumn.setCellValueFactory(new PropertyValueFactory<>("delivery"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        loadDataFromDatabase();
        OrderId.textProperty().addListener((observable, oldValue, newValue) -> updatePrice());
        quantity.textProperty().addListener((observable, oldValue, newValue) -> updatePrice());


        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        deliveryColumn.setCellValueFactory(new PropertyValueFactory<>("delivery"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        loadDataFromDatabase();
    }



    public void GoToAdminMenu(ActionEvent actionEvent)throws IOException {
        Main m = new Main();
        m.changeScene("AdminMenu.fxml");
    }
    public void ViewOrder(ActionEvent actionEvent) {
        boolean isVisible = TakeOrderButton.isVisible();
        TakeOrderButton.setVisible(!isVisible);
        quantity.setVisible(!isVisible);
        OrderId.setVisible(!isVisible);
        if (isVisible) {
            alert.setText("");
        }
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




    public void TakeOrder(ActionEvent actionEvent) {
        String orderIdText = OrderId.getText();
        String quantityText = quantity.getText();

        if (orderIdText.isEmpty() || quantityText.isEmpty()) {
            alert.setText("Введите все данные!");
            return;
        }

        int orderId;
        int addQuantity;

        try {
            orderId = Integer.parseInt(orderIdText);
            addQuantity = Integer.parseInt(quantityText);

            if (addQuantity <= 0) {
                alert.setText("Количество должно быть больше 0!");
                return;
            }
        } catch (NumberFormatException e) {
            alert.setText("Некорректный формат данных!");
            return;
        }

        String query = "UPDATE items SET доставляется = доставляется + ? WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, addQuantity);
            preparedStatement.setInt(2, orderId);

            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                alert.setText("Количество успешно обновлено!");
            } else {
                alert.setText("Товар с таким ID не найден!");
            }
            loadDataFromDatabase();

        } catch (SQLException e) {
            e.printStackTrace();
            alert.setText("Ошибка при обновлении данных!");
        }
    }

    public void GoToAdminOrders(ActionEvent actionEvent) throws IOException {
        Main m = new Main();
        m.changeScene("AdminOrders.fxml");
    }
}
