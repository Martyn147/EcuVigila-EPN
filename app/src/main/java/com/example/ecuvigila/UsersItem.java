package com.example.ecuvigila;

public class UsersItem {

    String userRelacion;
    String userName;
    String userContTlf;

    public UsersItem() {
    }

    public UsersItem(String userName, String userRelacion, String userContTlf) {
        this.userRelacion = userRelacion;
        this.userName = userName;
        this.userContTlf = userContTlf;
    }

    public String getUserRelacion() {
        return userRelacion;
    }

    public void setUserRelacion(String userCedula) {
        this.userRelacion = userCedula;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public String getUserContTlf() {
        return userContTlf;
    }

    public void setUserContTlf(String userContTlf) {
        this.userContTlf = userContTlf;
    }

}
