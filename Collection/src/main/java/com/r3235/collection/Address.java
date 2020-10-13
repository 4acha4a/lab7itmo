package com.r3235.collection;

import java.io.Serializable;

public class Address implements Serializable {
    private String street;
    private Location town;

    //Инкапсуляция
    public String getStreet() {return street;}
    public void setStreet(String street) {this.street = street;}

    public Location getTown() {return town;}
    public void setTown(Location town) {this.town = town;}

    public Address(String street, Location town){
        this.street = street;
        this.town = town;
    }
    public String toString() {
        return "_______________________" + "\n" +
                "Street: " + street + "\n" +
                "Location: " + town + "\n";
    }
}
