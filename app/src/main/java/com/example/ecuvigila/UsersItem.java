package com.example.ecuvigila;

public class UsersItem {

    private String userCorreo;
    private String userName;
    private String userRol;

    public UsersItem() {
    }

    public UsersItem(String userName, String userCorreo, String userRol) {
        this.userCorreo = userCorreo;
        this.userName = userName;
        this.userRol = userRol;
    }

    public String getuserCorreo() {
        return userCorreo;
    }

    public void setuserCorreo(String userCorreo) {
        this.userCorreo = userCorreo;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }


    public String getuserRol() {
        return userRol;
    }

    public void setuserRol(String userRol) {
        this.userRol = userRol;
    }

}
