package com.r3235.tools;

import com.r3235.collection.*;

import java.time.ZonedDateTime;

public class ReadCSV {
    public static Product toCSV(String dataa) {
        String[] data = dataa.split(";");
        try {
            String name = data[0];
            Long x = Long.valueOf(data[1]);
            Integer y = Integer.valueOf(data[2]);
            double price = Double.parseDouble(data[3]);
            UnitOfMeasure unitOfMeasure = null;
            try {
                unitOfMeasure = UnitOfMeasure.valueOf(data[4]);
            } catch (Exception ignore) {
            }
            String orgName = data[5];
            String fullorgName = data[6];
            OrganizationType type = null;
            try {
                type = OrganizationType.valueOf(data[7]);
            } catch (Exception ignore) {
            }
            String street = data[8];
            Integer Lx = Integer.valueOf(data[9]);
            Float Ly = Float.valueOf(data[10]);
            double Lz = Double.parseDouble(data[11]);
            Location town = new Location(Lx, Ly, Lz);
            Address postalAddress = new Address(street, town);
            Organization org = new Organization(0, orgName, fullorgName, type, postalAddress);
            Coordinates cords = new Coordinates(x, y);
            return new Product(0, name, cords, ZonedDateTime.now(), price, unitOfMeasure, org);
        } catch (Exception e) {
            System.out.println("Ошибка парсинга объект не считан.");
            System.out.println("Нужно указать 12 аргументов через ';': \n" +
                    "name(String);x(Long);y(Int);price(dauble);UnitOfMeasure;\n" +
                    "orgName(String);fullorgName(String);OrganizationType;street(String);x(int);y(float);z(double)");
            return null;
        }
    }
}
