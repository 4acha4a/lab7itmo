package com.r3235.tools;

import com.r3235.collection.*;

import java.time.ZonedDateTime;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class ProductGenerator {
    static int id = 0;
    static long orgid = 0;

    public static Product generate() {
        Product product = null;
        try {
            Scanner scanner = new Scanner(System.in);
            String name;
            do {
                System.out.println("Введите имя Product:");
                name = scanner.nextLine();
            } while (name.isEmpty() || name == null);
            Long x = null;
            do {
                System.out.println("Введите координату X:");
                try {
                    x = Long.valueOf(scanner.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("Ошибка ввода числа");
                }
            } while (x == null);
            Integer y = null;
            do {
                System.out.println("Введите координату Y:");
                try {
                    y = Integer.valueOf(scanner.nextLine());
                    if (y > 346) {
                        System.out.println("Максимальное значение координаты Y - 346");
                        continue;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Ошибка ввода числа");
                    continue;
                }
                break;
            } while (y == null || y > 346);
            double price = -1;
            do {
                System.out.println("Введите поле price");
                try {
                    price = Double.parseDouble(scanner.nextLine());
                    if (price < 0) {
                        System.out.println("Значение price не может быть меньше 0");
                        continue;
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Ошибка ввода числа");
                    continue;
                }
                break;
            } while (price < 0);
            UnitOfMeasure unitOfMeasure = null;
            do {
                System.out.println("Введите поле UnitOfMeasure.");
                System.out.println("Доступные варианты:");
                for (UnitOfMeasure unitOfMeasure1 : UnitOfMeasure.values())
                    System.out.println(unitOfMeasure1.toString());
                try {
                    String string = scanner.nextLine();
                    unitOfMeasure = UnitOfMeasure.valueOf(string);
                } catch (IllegalArgumentException e) {
                    System.out.println("Ошибка ввода");
                    continue;
                }
                break;
            } while (unitOfMeasure == null);
            String orgName = null;
            do {
                System.out.println("Введите имя организации");
                orgName = scanner.nextLine();
            } while (orgName == null || orgName.isEmpty());
            String fullOrgName = null;
            do {
                System.out.println("Введите ПОЛНОЕ имя организации (для ввода null используйте пустую строку):");
                try {
                    String string = scanner.nextLine();
                    if (string.isEmpty()) break;
                    fullOrgName = string;
                } catch (NumberFormatException e) {
                    System.out.println("Ошибка ввода числа");
                    continue;
                }
                break;
            } while (fullOrgName.length() > 836);
            OrganizationType type = null;
            while (true) {
                System.out.println("Введите поле Organization Type.");
                System.out.println("Доступные варианты:");
                for (OrganizationType type1 : OrganizationType.values()) System.out.println(type1.toString());
                try {
                    String string = scanner.nextLine();
                    type = OrganizationType.valueOf(string);
                } catch (IllegalArgumentException e) {
                    System.out.println("Ошибка ввода");
                    continue;
                }
                break;
            }

            String street = "";
            do {
                System.out.println("Введите поле street:");
                street = scanner.nextLine();
            } while (street.isEmpty());

            Integer Lx = null;
            do {
                try {
                    System.out.println("Введите координату X организации");
                    Lx = Integer.valueOf(scanner.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("Ошибка ввода числа");
                }
            } while (Lx == null);

            Float Ly = null;
            do {
                try {
                    System.out.println("Введите координату Y организации");
                    Ly = Float.valueOf(scanner.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("Ошибка ввода числа");
                }
            } while (Ly == null);

            double Lz;
            System.out.println("Введите координату Z организации");
            Lz = Double.parseDouble(scanner.nextLine());

            Location town = new Location(Lx, Ly, Lz);
            Address postalAddress = new Address(street, town);
            Organization manifacturer = new Organization(orgid++, orgName, fullOrgName, type, postalAddress);
            Coordinates coordinates = new Coordinates(x, y);
            product = new Product(id++, name, coordinates, ZonedDateTime.now(), price, unitOfMeasure, manifacturer);
        } catch (NoSuchElementException e) {
            System.out.println("Ввод был завершён...");
        }
        return product;
    }

    public static boolean checkProduct(Product product) {
        boolean status = true;
        if (product.getName() == null || product.getName().isEmpty()) {
            System.out.println("Product Name не может быть null или пустым");
            status = false;
        }
        if (product.getCoordinates() == null || product.getCoordinates().getX() == null ||
                product.getCoordinates().getY() == null || product.getCoordinates().getY() > 346) {
            System.out.println("Product Coordinates  не может быть null, y <= 346");
            status = false;
        }
        if (product.getPrice() < 0) {
            System.out.println("Product Price не может быть < 0");
            status = false;
        }
        if (product.getUnitOfMeasure() == null) {
            System.out.println("Product UnitOfMeasure не может быть null");
            status = false;
        }
        if (product.getManufacturer() != null) {
            if (product.getManufacturer().getName() == null || product.getManufacturer().getName().isEmpty()) {
                System.out.println("Product Organization's Name не может быть null или пустым");
                status = false;
            }
            if (product.getManufacturer().getType() == null) {
                System.out.println("Product  Organization Type не может быть null");
                status = false;
            }
            if (product.getManufacturer().getFullName().isEmpty()) {
                System.out.println("Product Organization's FullName не может быть пустым");
                status = false;
            }
            if (product.getManufacturer().getPostalAddress().getTown() == null) {
                if (product.getManufacturer().getPostalAddress().getTown().getX() == null) {
                    System.out.println("Product Organization's Address's Location's X не может быть null");
                }
                if (product.getManufacturer().getPostalAddress().getTown().getY() == null) {
                    System.out.println("Product Organization's Address's Location's Y не может быть null");
                }
                if (product.getManufacturer().getPostalAddress().getTown().getZ() == null) {
                    System.out.println("Product Organization's Address's Location's Z не может быть null");
                }
            }
        }

        return status;
    }
}

