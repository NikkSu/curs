package com.fx.curs;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;
import java.io.IOException;
import java.sql.*;
import static com.fx.curs.DatabaseConfig.*;
public class OrderCreatorContr {



    @FXML
    private TextField number;
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
    private Button AddButton;

    @FXML
    private Label alert;

    private int currentUserId;

    @FXML
    public void initialize() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        categoryColumn.setCellValueFactory(new PropertyValueFactory<>("category"));
        quantityColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        deliveryColumn.setCellValueFactory(new PropertyValueFactory<>("delivery"));
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));

        loadDataFromDatabase();

        AddButton.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull());
        number.disableProperty().bind(tableView.getSelectionModel().selectedItemProperty().isNull());
        loadCurrentUserId();
    }

    private void loadCurrentUserId() {
        String query = "SELECT id FROM users WHERE login = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setString(1, Main.currentUser);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                currentUserId = resultSet.getInt("id");
                showTemporaryMessage("Текущий пользователь ID: " + currentUserId);
            } else {
                showTemporaryMessage("Ошибка: текущий пользователь не найден в базе данных.");
            }

        } catch (SQLException e) {
            showTemporaryMessage("Ошибка при получении ID пользователя.");
            e.printStackTrace();
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

            tableView.setItems(items);

        } catch (SQLException e) {
            showTemporaryMessage("Ошибка при загрузке данных из базы.");
            e.printStackTrace();
        }
    }

    @FXML
    private void AddToCatalog(ActionEvent event) {
        Item selectedItem = tableView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            showTemporaryMessage("Выберите товар для добавления.");
            return;
        }

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String getLastOrderQuery = "SELECT id, isPlaced FROM orders WHERE user_id = ? ORDER BY order_date DESC LIMIT 1";
            int orderId = -1;
            boolean isPlaced = true;

            try (PreparedStatement lastOrderStatement = connection.prepareStatement(getLastOrderQuery)) {
                lastOrderStatement.setInt(1, currentUserId);
                ResultSet resultSet = lastOrderStatement.executeQuery();

                if (resultSet.next()) {
                    orderId = resultSet.getInt("id");
                    isPlaced = resultSet.getBoolean("isPlaced");
                }
            }

            if (orderId == -1 || isPlaced) {
                String createOrderQuery = "INSERT INTO orders (user_id, order_date, status, isPlaced) VALUES (?, NOW(), 'обрабатывается', false)";
                try (PreparedStatement createOrderStatement = connection.prepareStatement(createOrderQuery, Statement.RETURN_GENERATED_KEYS)) {
                    createOrderStatement.setInt(1, currentUserId);
                    createOrderStatement.executeUpdate();

                    ResultSet generatedKeys = createOrderStatement.getGeneratedKeys();
                    if (generatedKeys.next()) {
                        orderId = generatedKeys.getInt(1);
                    } else {
                        throw new SQLException("Не удалось создать новый заказ.");
                    }
                }
            }


            String checkItemQuery = "SELECT quantity FROM order_items WHERE order_id = ? AND item_id = ?";
            try (PreparedStatement checkItemStatement = connection.prepareStatement(checkItemQuery)) {
                checkItemStatement.setInt(1, orderId);
                checkItemStatement.setInt(2, selectedItem.getId());

                ResultSet resultSet = checkItemStatement.executeQuery();
                if (resultSet.next()) {
                    int existingQuantity = resultSet.getInt("quantity");


                    String updateQuantityQuery = "UPDATE order_items SET quantity = ? WHERE order_id = ? AND item_id = ?";
                    try (PreparedStatement updateQuantityStatement = connection.prepareStatement(updateQuantityQuery)) {
                        updateQuantityStatement.setInt(1, existingQuantity + Integer.parseInt(number.getText()));
                        updateQuantityStatement.setInt(2, orderId);
                        updateQuantityStatement.setInt(3, selectedItem.getId());
                        updateQuantityStatement.executeUpdate();
                    }
                } else {

                    String addToOrderQuery = "INSERT INTO order_items (order_id, item_id, quantity) VALUES (?, ?, ?)";
                    try (PreparedStatement addToOrderStatement = connection.prepareStatement(addToOrderQuery)) {
                        addToOrderStatement.setInt(1, orderId);
                        addToOrderStatement.setInt(2, selectedItem.getId());
                        addToOrderStatement.setInt(3, Integer.parseInt(number.getText()));
                        addToOrderStatement.executeUpdate();
                    }
                }
            }
            number.clear();
            showTemporaryMessage("Товар успешно добавлен в заказ.");
        } catch (SQLException e) {
            number.clear();
            showTemporaryMessage("Ошибка при добавлении товара в заказ.");
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

    @FXML
    private void searchById(ActionEvent event) {
        String searchText = searchField.getText().trim();
        if (!searchText.matches("\\d+")) {
            showTemporaryMessage("Введите корректный ID для поиска.");
            return;
        }

        String query = "SELECT id, название, категория, количество, доставляется, цена FROM items WHERE id = ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, Integer.parseInt(searchText));
            executeSearch(preparedStatement);

        } catch (SQLException e) {
            showTemporaryMessage("Ошибка при выполнении поиска.");
            e.printStackTrace();
        }
    }

    @FXML
    private void searchByName(ActionEvent event) {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty()) {
            showTemporaryMessage("Введите название для поиска.");
            return;
        }

        String query = "SELECT id, название, категория, количество, доставляется, цена FROM items WHERE название LIKE ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, "%" + searchText + "%");
            executeSearch(preparedStatement);

        } catch (SQLException e) {
            showTemporaryMessage("Ошибка при выполнении поиска.");
            e.printStackTrace();
        }
    }

    @FXML
    private void searchByCategory(ActionEvent event) {
        String searchText = searchField.getText().trim();
        if (searchText.isEmpty()) {
            showTemporaryMessage("Введите категорию для поиска.");
            return;
        }

        String query = "SELECT id, название, категория, количество, доставляется, цена FROM items WHERE категория LIKE ?";

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, "%" + searchText + "%");
            executeSearch(preparedStatement);

        } catch (SQLException e) {
            showTemporaryMessage("Ошибка при выполнении поиска.");
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
            showTemporaryMessage("Поиск выполнен успешно.");
        }
    }

    @FXML
    public void clearSearch(ActionEvent event) {
        searchField.clear();
        loadDataFromDatabase();
        showTemporaryMessage("Поиск очищен.");
    }

    @FXML
    public void GoToAdminMenu(ActionEvent actionEvent) throws IOException {
        Main m = new Main();
        m.changeScene("AfterLogin.fxml");
    }
}
