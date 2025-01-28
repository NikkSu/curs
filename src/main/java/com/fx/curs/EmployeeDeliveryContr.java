package com.fx.curs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import static com.fx.curs.DatabaseConfig.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EmployeeDeliveryContr {

    @FXML
    private TableView<Item> tableView;
    @FXML
    private TableColumn<Item, Integer> idColumn;
    @FXML
    private TableColumn<Item, String> nameColumn;
    @FXML
    private TableColumn<Item, Integer> quantityColumn;
    @FXML
    private TableColumn<Item, Integer> deliveryColumn;
    @FXML
    private Button acceptButton;
    @FXML
    private Label alert;

    private ObservableList<Item> itemsList = FXCollections.observableArrayList();
    private Item selectedItem;



    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        deliveryColumn.setCellValueFactory(new PropertyValueFactory<>("delivery"));

        tableView.setItems(itemsList);
        tableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            selectedItem = newSelection;
            acceptButton.setDisable(selectedItem == null);
        });

        loadItems();
    }

    private void loadItems() {
        itemsList.clear();
        String query = "SELECT id, название AS name, количество AS quantity, доставляется AS delivery FROM items";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                itemsList.add(new Item(
                        resultSet.getInt("id"),
                        resultSet.getString("name"),
                        resultSet.getInt("quantity"),
                        resultSet.getInt("delivery")
                ));
            }

        } catch (SQLException e) {
            alert.setText("Ошибка загрузки данных!");
            e.printStackTrace();
        }
    }

    @FXML
    private void accept(ActionEvent event) {
        if (selectedItem == null) {
            alert.setText("Выберите строку для обработки!");
            return;
        }

        String updateQuery = "UPDATE items SET количество = количество + ?, доставляется = 0 WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(updateQuery)) {

            statement.setInt(1, selectedItem.getDelivery());
            statement.setInt(2, selectedItem.getId());

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                alert.setText("Товар успешно обновлён!");
                loadItems();
            } else {
                alert.setText("Ошибка обновления товара!");
            }

        } catch (SQLException e) {
            alert.setText("Ошибка базы данных!");
            e.printStackTrace();
        }
    }



    public static class Item {
        private final int id;
        private final String name;
        private final int quantity;
        private final int delivery;

        public Item(int id, String name, int quantity, int delivery) {
            this.id = id;
            this.name = name;
            this.quantity = quantity;
            this.delivery = delivery;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public int getQuantity() {
            return quantity;
        }

        public int getDelivery() {
            return delivery;
        }
    }
    @FXML
    private void GoToRedactor(ActionEvent event) throws IOException {
        Main m = new Main();
        m.changeScene("Redactor.fxml");
    }

    public void GoToAdminMenu(ActionEvent actionEvent) throws IOException {
        Main m = new Main();
        m.changeScene("EmployeeMenu.fxml");
    }
    public void GoToOrders(ActionEvent actionEvent) throws IOException {
        Main m = new Main();
        m.changeScene("Order.fxml");
    }
    public void GoToEmployeeCatalog(ActionEvent actionEvent) throws IOException {
        Main m = new Main();
        m.changeScene("EmployeeCatalog.fxml");
    }
    public void GoToEmployeeOrder(ActionEvent actionEvent) throws IOException {
        Main m = new Main();
        m.changeScene("EmployeeOrder.fxml");
    }
    public void GoToDelivery(ActionEvent actionEvent) throws IOException {
        Main m = new Main();
        m.changeScene("EmployeeDelivery.fxml");
    }
}
