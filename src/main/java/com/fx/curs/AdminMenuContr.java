package com.fx.curs;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import java.io.IOException;


public class AdminMenuContr  {

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
