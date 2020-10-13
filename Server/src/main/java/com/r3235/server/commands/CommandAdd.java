package com.r3235.server.commands;

import com.r3235.collection.Organization;
import com.r3235.collection.Product;
import com.r3235.tools.ProductGenerator;
import com.r3235.tools.Response;
import com.r3235.server.CollectionControl;
import com.r3235.tools.Task;
import sun.nio.cs.Surrogate;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.LinkedList;

public class CommandAdd implements Command {
    @Override
    public Response executeTask(CollectionControl collectionControl, Task task) {
        int userId = collectionControl.getSqlManager().getUserId(task.getLogin());
        if (userId == 0) return new Response("Ошибка авторизации!");
        Product addProduct = (Product) task.getArgument();
        addProduct.setCreationDate(ZonedDateTime.now());
        if (!ProductGenerator.checkProduct(addProduct)) {
            return new Response("Объект не удовлетворяет требованиям.");
        } else {
            int idp = addProductSQL(addProduct, userId, collectionControl);
            long idm = addOwnerSQL(addProduct.getManufacturer(), idp, collectionControl);
            addProduct.setUserName(task.getLogin());
            addProduct.setId(idp);
            addProduct.getManufacturer().setId(idm);
            if (idp == -1 || idm == -1) return new Response("Ошибка добавления элеемнта в базу данных");
            else {
                collectionControl.getLock().writeLock().lock();

                if (collectionControl.getCollection().add(addProduct)) {
                    collectionControl.getLock().writeLock().unlock();
                    return new Response("Элемент c id " + idp + " успешно добавлен.\n "+ addProduct.toString());
                } else {
                    collectionControl.getLock().writeLock().unlock();
                    return new Response("Ошибка добавления элеемнта в коллекцию. Но. В базу он добавлени...)");
                }
            }
        }
    }

    private int addProductSQL(Product product, int userId, CollectionControl collectionControl) {
        int id = -1;
        try {
            PreparedStatement statement = collectionControl.getSqlManager().getConnection().prepareStatement(
                    "insert into products" +
                            "(name, x, y, creationdate, price, unitofmeasure_id, user_id) " +
                            "values (?,?,?,?,?,(select id from unit_of_measures where unit_name = ?),?) returning id"
            );
            statement.setString(1, product.getName());
            statement.setFloat(2, product.getCoordinates().getX());
            statement.setDouble(3, product.getCoordinates().getY());
            statement.setTimestamp(4, new Timestamp(product.getCreationDate().toEpochSecond() * 1000));
            statement.setDouble(5, product.getPrice());
            statement.setString(6, product.getUnitOfMeasure().toString());
            statement.setLong(7, userId);

            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                id = resultSet.getInt("id");
            }
        } catch (SQLException lal) {
            lal.printStackTrace();
        }
        return id;
    }

    private int addOwnerSQL(Organization organization, int id, CollectionControl collectionControl) {
        int idOrg = -1;
        try {
            PreparedStatement statement = collectionControl.getSqlManager().getConnection().prepareStatement(
                    "insert into organizations" +
                            "(organization_name, organization_fulllname, organization_type_id, " +
                            "organization_x, organization_y, organization_z, street, product_id)" +
                            "values (?,?,(select id from organization_type where organization_type_name = ?), ?,?,?,?,?) returning id"
            );
            statement.setString(1, organization.getName());
            statement.setString(2, organization.getFullName());
            statement.setString(3, organization.getType().toString());
            statement.setInt(4, organization.getPostalAddress().getTown().getX());
            statement.setFloat(5, organization.getPostalAddress().getTown().getY());
            statement.setDouble(6, organization.getPostalAddress().getTown().getZ());
            statement.setString(7, organization.getPostalAddress().getStreet());
            statement.setInt(8, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) idOrg = resultSet.getInt("id");
        } catch (SQLException lal) {
            lal.printStackTrace();
        }
        return idOrg;
    }


}
