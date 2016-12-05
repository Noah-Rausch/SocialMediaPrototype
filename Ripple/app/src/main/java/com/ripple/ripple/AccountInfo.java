package com.ripple.ripple;

/**
 * Created by Noah on 6/24/2016.
 */
public class AccountInfo {
    private String username;
    private String idCode;
    private String email;
    private String password;

    public AccountInfo(String user, String em, String pass, String code ){
        this.username = user;
        this.email = em;
        this.password = pass;
        this.idCode = code;
    }

    public AccountInfo(){

    }

    public String getUsername() {
        return username;
    }

    public String getIdCode() {
        return idCode;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }
}
