package com.fx.curs;

import javafx.beans.property.*;

public class User {
    private final IntegerProperty id;
    private final SimpleStringProperty login;
    private final SimpleStringProperty password;
    private final BooleanProperty isAdmin;
    private final SimpleStringProperty email;
    private final BooleanProperty isEmployee;

    public User(int id, String login, String password, boolean isAdmin, String email, boolean isEmployee) {
        this.id = new SimpleIntegerProperty(id);
        this.login = new SimpleStringProperty(login);
        this.password = new SimpleStringProperty(password);
        this.isAdmin = new SimpleBooleanProperty(isAdmin);
        this.email = new SimpleStringProperty(email);
        this.isEmployee = new SimpleBooleanProperty(isEmployee);
    }


    public IntegerProperty idProperty() { return id; }
    public SimpleStringProperty loginProperty() { return login; }
    public SimpleStringProperty passwordProperty() { return password; }
    public BooleanProperty isAdminProperty() { return isAdmin; }
    public BooleanProperty isEmployeeProperty() { return isEmployee; }
    public SimpleStringProperty emailProperty() { return email; }
    public void setId(int id) { this.id.set(id);}
    public void setLogin(String login) { this.login.set(String.valueOf(login));}
    public void setPassword(String password) { this.password.set(String.valueOf(password));}
    public void setIsAdmin(boolean isAdmin) { this.isAdmin.set(isAdmin);}
    public void setEmail(String email) { this.email.set(String.valueOf(email));}
    public void setIsEmployee(boolean isEmployee) { this.isEmployee.set(isEmployee);}
    public int getId() { return id.get(); }
    public String getLogin() { return login.get(); }
    public String getPassword() { return password.get(); }
    public String getEmail() { return email.get(); }
    public boolean getIsAdmin() { return isAdmin.get(); }
    public boolean getIsEmployee() { return isEmployee.get(); }
}
