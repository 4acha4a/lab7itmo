package com.r3235.server;

import com.r3235.collection.*;
import com.r3235.tools.Task;

import java.sql.*;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.LinkedList;

public class SQLManager {
    private Connection connection;

    /**
     * Проводит подключение к базе данных
     *
     * @param host         Адрес для подключения к базе
     * @param port         Порт подключения
     * @param dataBaseName Имя базы данных
     * @param user         Имя пользователя в базе
     * @param password     Пароль пользователя
     * @return Статус подключения
     */
    public boolean initDatabaseConnection(String host, int port, String dataBaseName, String user, String password) {
        System.out.println("Подключение базы SQL по введённым данным...");
        String databaseUrl = "jdbc:postgresql://" + host + ":" + port + "/" + dataBaseName;
        try {
            System.out.println("URL подключения базы: " + databaseUrl);
            connection = DriverManager.getConnection(databaseUrl, user, password);
            System.out.println("База данных '" + connection.getCatalog() + "' подключена! ");
            return true;
        } catch (SQLException e) {
            System.out.println("Ошибка подключения базы SQL: " + e.toString());
            return false;
        }

    }

    /**
     * Инициализирует таблицы в базе
     *
     * @return Статус инифиализации
     */
    public boolean initTables() {
        try {
            System.out.println("Инициализация таблиц:");
            Statement statement = connection.createStatement();
            //Таблица данных пользователей
            statement.execute("create table if not exists users (" +
                    "id serial primary key not null, username text unique , password_hash bytea)"
            );
            //таблица с unitOfMeasure
            statement.execute("CREATE TABLE if not exists unit_of_measures " +
                    "(id serial primary key not null ,unit_name varchar(20) NOT NULL UNIQUE )");
            try {
                for (UnitOfMeasure unitOfMeasure : UnitOfMeasure.values())
                    statement.execute("insert into unit_of_measures(unit_name) values('" + unitOfMeasure + "') ");
            } catch (SQLException ignore) {
            }
            //таблица с organizationType
            statement.execute("CREATE TABLE if not exists organization_type " +
                    "(id serial primary key not null ,organization_type_name text NOT NULL UNIQUE )");
            try {
                for (OrganizationType organizationType : OrganizationType.values())
                    statement.execute("insert into organization_type(organization_type_name) values('" + organizationType + "') ");
            } catch (SQLException ignore) {
            }
            //кривая таблица Product
            statement.execute("create table if not exists products " +
                    "(id serial primary key not null , name text, x int,y int, " +
                    "creationDate timestamp,price double precision, unitOfMeasure_id int, " +
                    "user_id integer, " +
                    "foreign key (unitOfMeasure_id) references unit_of_measures(id), " +
                    "foreign key (user_id) references users(id))"
            );

            statement.execute("create table if not exists organizations " +
                    "(id serial primary key not null, organization_name text, organization_fulllname text," +
                    "organization_type_id int,organization_x int,organization_y float ,organization_z float , street text, " +
                    "product_id int, " +
                    "foreign key (organization_type_id) references organization_type(id)," +
                    "foreign key (product_id) references products(id) on delete cascade)"
            );
            return true;
        } catch (SQLException e) {
            System.out.println("Ошибка инициализации базы данных. Смотрите ошибку, вы же сервер ставите, должны понимать!");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Ищет id пользователя по заданному имени
     *
     * @param loginName Логин или email Пользователя
     * @return id Пользователя или -1, если пользователь не найден
     */
    public int getUserId(String loginName) {
        int userId = -1;
        try {
            PreparedStatement s = connection
                    .prepareStatement("select id from users where (username =?)");
            s.setString(1, loginName);
            ResultSet resultSet = s.executeQuery();
            if (resultSet.next()) userId = resultSet.getInt("id");
        } catch (SQLException ignore) {
        }
        return userId;
    }

    public Connection getConnection() {
        return connection;
    }

    public boolean checkAccount(String login, String password) {
        try {
            PreparedStatement statement = connection.prepareStatement(
                    "select * from users where username =? and password_hash = ?"
            );
            statement.setString(1, login);
            statement.setBytes(2, password.getBytes());
            ResultSet resultSet = statement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            System.out.println(e);
            return false;
        }
    }

    public LinkedList<Product> loadCollection() {
        LinkedList<Product> products = new LinkedList<>();
        try {
            System.out.println("Загрузка коллекции из базы: " + connection.getCatalog());
            PreparedStatement statement = connection.prepareStatement(
                    "select  username, product_id, name, x, y, creationdate, price, unit_name," +
                            "       organization_name,organization_fulllname,organization_x,organization_y,organization_z," +
                            "       organization_type_name, user_id,o.id,street from products" +
                            "    join organizations o on products.id = o.product_id" +
                            "    join organization_type ot on ot.id = o.organization_type_id" +
                            "    join unit_of_measures uom on uom.id = products.unitofmeasure_id" +
                            "    join users u on u.id = products.user_id"
            );
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Organization organization = new Organization(
                        resultSet.getInt("id"),
                        resultSet.getString("organization_name"),
                        resultSet.getString("organization_fulllname"),
                        OrganizationType.valueOf(resultSet.getString("organization_type_name")),
                        new Address(resultSet.getString("street"), new Location(
                                resultSet.getInt("organization_x"),
                                resultSet.getFloat("organization_y"),
                                resultSet.getDouble("organization_z")
                        ))
                );
                Product product = new Product(resultSet.getInt("id"),
                        resultSet.getString("name"),
                        new Coordinates(resultSet.getLong("x"), resultSet.getInt("y")),
                        resultSet.getTimestamp("creationdate").toLocalDateTime().atZone(ZoneId.of("UTC")),
                        resultSet.getDouble("price"),
                        UnitOfMeasure.valueOf(resultSet.getString("unit_name")),
                        organization);
                product.setUserName(resultSet.getString("username"));
                products.add(product);
            }
            System.out.println("Коллекция загружена. Добавлено " + products.size() + " items.");
            return products;
        } catch (SQLException e) {
            System.out.println("SQL reading error");
            e.printStackTrace();
            return null;
        }

    }
}
