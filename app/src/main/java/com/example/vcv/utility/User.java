package com.example.vcv.utility;

import com.google.firebase.database.IgnoreExtraProperties;

public class User {

    public String name;
    public String surname;
    public String telephone;

    public User() {

    }

    public User(String name, String surname, String telephone) {
        this.name = name;
        this.surname = surname;
        this.telephone = telephone;
    }
}
