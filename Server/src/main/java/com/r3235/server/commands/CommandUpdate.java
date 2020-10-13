package com.r3235.server.commands;

import com.r3235.collection.Organization;
import com.r3235.collection.Product;
import com.r3235.tools.CommandType;
import com.r3235.tools.ProductGenerator;
import com.r3235.tools.Response;
import com.r3235.server.CollectionControl;
import com.r3235.tools.Task;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.ZoneOffset;
import java.util.HashSet;
import java.util.LinkedList;

public class CommandUpdate implements Command {
    @Override
    public Response executeTask(CollectionControl collectionControl, Task task) {
        int userId = collectionControl.getSqlManager().getUserId(task.getLogin());
        if (userId == -1) return new Response("Ошибка авторизации!");
        Product newProduct = (Product) task.getArgument();
        int id = newProduct.getId();
        if (!ProductGenerator.checkProduct(newProduct)) {
            return new Response("Элемент не удовлетворяет требованиям коллекции");
        } else {
            if (updateProductSQL(newProduct, id, collectionControl) != -1) {
                collectionControl.getLock().writeLock().lock();
                LinkedList<Product> products = collectionControl.getCollection();
                for (Product productForUpdate : products) {
                    if (productForUpdate.getId() == id)
                        updateProduct(productForUpdate, newProduct);
                    collectionControl.getLock().writeLock().unlock();
                    return new Response("Элемент успешно обновлён.");
                }
                collectionControl.getLock().writeLock().unlock();
                return new Response("Как такое могло произойти?! В базе обновлён, а в коллекци - нет?!");
            } else
                collectionControl.getLock().writeLock().unlock();
            return new Response("При замене элементов что-то пошло не так.\n" +
                    " Возможно, объект Вам не принаджежит");
        }
    }

    private int updateProductSQL(Product product, int id, CollectionControl collectionControl) {
        int uid =-1;
        if (updateOrganizationSQL(product.getManufacturer(), id, collectionControl) != -1)
            try {
                PreparedStatement statement = collectionControl.getSqlManager().getConnection().prepareStatement(
                        "UPDATE products " +
                                "SET name = ?, x=?, y=?, creationdate=?, price=?, unitofmeasure_id= (select id from unit_of_measures where unit_name = ?) " +
                                "WHERE id = ? returning id"
                );
                statement.setString(1, product.getName());
                statement.setFloat(2, product.getCoordinates().getX());
                statement.setDouble(3, product.getCoordinates().getY());
                statement.setTimestamp(4, new Timestamp(product.getCreationDate().toEpochSecond() * 1000));
                statement.setDouble(5, product.getPrice());
                statement.setString(6, product.getUnitOfMeasure().toString());
                statement.setInt(7, id);

                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    uid = resultSet.getInt("id");
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        return uid;
    }

    private int updateOrganizationSQL(Organization organization, int id, CollectionControl collectionControl) {
        int idOwner = -1;
        try {
            PreparedStatement statement = collectionControl.getSqlManager().getConnection().prepareStatement(
                    "UPDATE organizations SET " +
                            "organization_name=?, organization_fulllname=?, " +
                            "organization_type_id=(select id from organization_type where organization_type_name = ?), " +
                            "organization_x=?, organization_y=?, organization_z=?, street=? where product_id = ? returning id"
            );
            statement.setString(1, organization.getName());
            statement.setString(2, organization.getFullName());
            statement.setString(3, organization.getType().toString());
            statement.setInt(4, organization.getPostalAddress().getTown().getX());
            statement.setFloat(5, organization.getPostalAddress().getTown().getY());
            statement.setDouble(6, organization.getPostalAddress().getTown().getZ());
            statement.setString(7, organization.getPostalAddress().getStreet());
            statement.setLong(8, id);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) idOwner = resultSet.getInt("id");
            System.out.println(id);
        } catch (SQLException lal) {
            lal.printStackTrace();
        }
        return idOwner;
    }

    private void updateProduct(Product productForUpdate, Product newProduct) {
        productForUpdate.setName(newProduct.getName());
        productForUpdate.setCoordinates(newProduct.getCoordinates());
        productForUpdate.setPrice(newProduct.getPrice());
        productForUpdate.setUnitOfMeasure(newProduct.getUnitOfMeasure());
        newProduct.getManufacturer().setId(productForUpdate.getManufacturer().getId());
        productForUpdate.setManufacturer(newProduct.getManufacturer());
    }
}
