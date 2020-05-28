package com.example.vcv.utility;

/**
 * @author Mattia Da Campo e Andrea Dalle Fratte
 * @version 1.0
 */
public class User {
    public String name;
    public String surname;
    public String telephone;
    public String badgeNumber;
    public String email;

    /**
     * Empty constructor
     */
    public User() {

    }

    /**
     * Constructor
     *
     * @param name
     * @param surname
     * @param telephone
     * @param badgeNumber
     * @param email
     */
    public User(String name, String surname, String telephone, String badgeNumber, String email) {
        this.name = name;
        this.surname = surname;
        this.telephone = telephone;
        this.badgeNumber = badgeNumber;
        this.email = email;
    }
}
