package com.example.ecuvigila;

public class UsersItem {

    private String userCorreo;
    private String userName;
    private String userPass;

    public UsersItem() {
    }

    public UsersItem(String userName, String userCorreo, String userPass) {
        this.userCorreo = userCorreo;
        this.userName = userName;
        this.userPass = userPass;
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


    public String getuserPass() {
        return userPass;
    }

    public void setuserPass(String userPass) {
        this.userPass = userPass;
    }

}
