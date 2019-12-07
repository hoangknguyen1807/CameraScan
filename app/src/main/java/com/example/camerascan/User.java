package com.example.camerascan;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("id")
    @Expose
    private int id;

    @SerializedName("email")
    @Expose
    private String email;


    @SerializedName("name")
    @Expose
    private String name;


    @SerializedName("password")
    @Expose
    private String password;

    public User() {
    }
    public User(String email,String password) {

        this.email = email;

        this.password = password;
    }
    public User(int id, String email, String name, String password) {
        this.id = id;
        this.email = email;
        this.name=name;
        this.password = password;
    }

    public User(String email, String name, String password) {

        this.email = email;
        this.name=name;
        this.password = password;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
