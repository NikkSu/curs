package com.fx.curs;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.converter.DoubleStringConverter;
import javafx.util.converter.IntegerStringConverter;

import java.io.IOException;
import java.sql.*;

public class RedactorContr {

    @FXML
    private TextField name;

    @FXML
    private TextField category;

    @FXML
    private TextField amount;

    @FXML
    private TextField price;

    @FXML
    private Button buttonAdd;

    @FXML
    private Button deleteButton;

    @FXML
    private Button redactbutton;

    @FXML
    private Label alert;

    @FXML
    private TextField deleteId;

    @FXML
    private TextField redactId;

    @FXML
    private Button viewRedactButton;

    @FXML
    private TableView<Item> table;

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
    private TableColumn<Item, String> placeColumn;
    private final String DB_URL = "jdbc:mysql://localhost:3306/my_database";
    private final String DB_USER = "root";
    private final String DB_PASSWORD = "1903";

    @FXML
    private void UserLogOut(ActionEvent actionEvent) throws IOException {
        Main m = new Main();
        m.changeScene("AdminMenu.fxml");
    }

    public void GoToAccounts(ActionEvent actionEvent) throws IOException {
        Main m = new Main();
        m.changeScene("Accounts.fxml");
    }

    public void GoToCatalog(ActionEvent actionEvent) throws IOException {
        Main m = new Main();
        m.changeScene("Catalog.fxml");
    }

    public void GoToOrders(ActionEvent actionEvent) throws IOException {
        Main m = new Main();
        m.changeScene("Order.fxml");
    }

    public void ViewAdder(ActionEvent actionEvent) {
        boolean isVisible = name.isVisible();
        name.setVisible(!isVisible);
        category.setVisible(!isVisible);
        amount.setVisible(!isVisible);
        price.setVisible(!isVisible);
        buttonAdd.setVisible(!isVisible);

        if (isVisible) {
            alert.setText("");
        }
    }

    public void AddProduct(ActionEvent actionEvent) {
        alert.setText("");

        if (name.getText().isEmpty() || category.getText().isEmpty() ||
                amount.getText().isEmpty() || price.getText().isEmpty()) {

            alert.setText("Все поля должны быть заполнены!");
            return;
        }

        String productName = name.getText();
        String productCategory = category.getText();
        int productQuantity;
        double productPrice;

        try {
            productQuantity = Integer.parseInt(amount.getText());
            productPrice = Double.parseDouble(price.getText());
        } catch (NumberFormatException e) {
            alert.setText("Поле 'Количество' должно быть целым числом, а 'Цена' - числом с точкой.");
            return;
        }

        String insertQuery = "INSERT INTO items (название, категория, количество, доставляется, цена) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(insertQuery)) {

            statement.setString(1, productName);
            statement.setString(2, productCategory);
            statement.setInt(3, productQuantity);
            statement.setInt(4, 0);
            statement.setDouble(5, productPrice);

            statement.executeUpdate();

            alert.setText("Продукт успешно добавлен!");

            name.clear();
            category.clear();
            amount.clear();
            price.clear();

        } catch (SQLException e) {
            e.printStackTrace();
            alert.setText("Не удалось добавить продукт в базу данных.");
        }
    }

    public void ViewDelete(ActionEvent actionEvent) {
        boolean isVisible = deleteId.isVisible() && deleteButton.isVisible();
        deleteId.setVisible(!isVisible);
        deleteButton.setVisible(!isVisible);

        if (isVisible) {
            alert.setText("");
        }
    }

    public void Delete(ActionEvent actionEvent) {
        alert.setText("");

        String idText = deleteId.getText();

        if (idText.isEmpty()) {
            alert.setText("Поле ID не может быть пустым!");
            return;
        }

        int productId;
        try {
            productId = Integer.parseInt(idText);
        } catch (NumberFormatException e) {
            alert.setText("ID должен быть числом!");
            return;
        }

        String deleteQuery = "DELETE FROM items WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(deleteQuery)) {

            statement.setInt(1, productId);
            int rowsAffected = statement.executeUpdate();

            if (rowsAffected > 0) {
                alert.setText("Продукт успешно удален!");
                deleteId.clear();
            } else {
                alert.setText("Продукт с таким ID не найден.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            alert.setText("Не удалось удалить продукт из базы данных.");
        }
    }

    @FXML
    public void initialize() {
        table.setEditable(true);

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        deliveryColumn.setCellValueFactory(new PropertyValueFactory<>("delivery"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        placeColumn.setCellValueFactory(new PropertyValueFactory<>("place"));
        nameColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        categoryColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        quantityColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        deliveryColumn.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
        priceColumn.setCellFactory(TextFieldTableCell.forTableColumn(new DoubleStringConverter()));
        placeColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameColumn.setOnEditCommit(event -> {
            Item item = event.getRowValue();
            item.setName(event.getNewValue());
            updateItemInDatabase(item);
        });

        categoryColumn.setOnEditCommit(event -> {
            Item item = event.getRowValue();
            item.setCategory(event.getNewValue());
            updateItemInDatabase(item);
        });

        quantityColumn.setOnEditCommit(event -> {
            Item item = event.getRowValue();
            item.setQuantity(event.getNewValue());
            updateItemInDatabase(item);
        });

        deliveryColumn.setOnEditCommit(event -> {
            Item item = event.getRowValue();
            item.setDelivery(event.getNewValue());
            updateItemInDatabase(item);
        });

        priceColumn.setOnEditCommit(event -> {
            Item item = event.getRowValue();
            item.setPrice(event.getNewValue());
            updateItemInDatabase(item);
        });
        placeColumn.setOnEditCommit(event -> {
            Item item = event.getRowValue();
            item.setPlace(event.getNewValue());
            updateItemInDatabase(item);
        });
    }

    public void VievRedact(ActionEvent actionEvent) {
        boolean isVisible = redactId.isVisible() && redactbutton.isVisible();
        redactId.setVisible(!isVisible);
        redactbutton.setVisible(!isVisible);
        table.setVisible(false);
        redactId.clear();
        if (isVisible) {
            alert.setText("");
        }
    }

    public void Redact(ActionEvent actionEvent) {
        alert.setText("");
        table.setVisible(true);

        try {
            int productId = Integer.parseInt(redactId.getText());
            loadProduct(productId);
        } catch (NumberFormatException e) {
            alert.setText("ID должен быть числом!");
        }
        redactId.clear();
    }

    private void loadProduct(int redactId) {
        String query = "SELECT * FROM items WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setInt(1, redactId);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                Item item = new Item(
                        resultSet.getInt("id"),
                        resultSet.getString("название"),
                        resultSet.getString("категория"),
                        resultSet.getInt("количество"),
                        resultSet.getInt("доставляется"),
                        resultSet.getDouble("цена"),
                        resultSet.getString("place")
                );

                table.getItems().clear();
                table.getItems().add(item);
                table.setVisible(true);
            } else {
                alert.setText("Продукт с указанным ID не найден.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            alert.setText("Ошибка при загрузке данных из базы.");
        }
    }

    private void updateItemInDatabase(Item item) {
        String updateQuery = "UPDATE items SET название = ?, категория = ?, количество = ?, доставляется = ?, цена = ?, place = ? WHERE id = ?";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(updateQuery)) {

            statement.setString(1, item.getName());
            statement.setString(2, item.getCategory());
            statement.setInt(3, item.getQuantity());
            statement.setInt(4, item.getDelivery());
            statement.setDouble(5, item.getPrice());
            statement.setString(6, item.getPlace());
            statement.setInt(7, item.getId());
            statement.executeUpdate();

            alert.setText("Изменения сохранены!");

        } catch (SQLException e) {
            e.printStackTrace();
            alert.setText("Не удалось сохранить изменения в базе данных.");
        }
    }

    public void GoToAdminOrders(ActionEvent actionEvent) throws IOException {
        Main m = new Main();
        m.changeScene("AdminOrders.fxml");
    }
}
