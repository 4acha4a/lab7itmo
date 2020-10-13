package com.r3235.server.commands;

import com.r3235.collection.Product;
import com.r3235.tools.Response;
import com.r3235.server.CollectionControl;
import com.r3235.tools.Task;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class CommandRemoveById implements Command {
    @Override
    public Response executeTask(CollectionControl collectionControl, Task task) {
        int userId = collectionControl.getSqlManager().getUserId(task.getLogin());
        if (userId == -1) return new Response("Ошибка авторизации!");

        collectionControl.getLock().writeLock().lock();
        LinkedList<Product> products = collectionControl.getCollection();
        int startSize = products.size();
        if (products.size() > 0) {
            long id = (long) task.getArgument();
            try {
                PreparedStatement statement = collectionControl.getSqlManager().getConnection().prepareStatement(
                        "delete from products where user_id = ? and id = ? returning products.id"
                );
                statement.setInt(1, userId);
                statement.setLong(2, id);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next())
                    products.removeAll((products.parallelStream().filter(product -> product.getId() == id)
                            .collect(Collectors.toCollection(LinkedList::new))));
            } catch (SQLException e) {
                return new Response(" Ошибка работы с базой данных");
            }
            if (startSize == products.size()) {
                collectionControl.getLock().writeLock().unlock();
                return new Response("Элемент с id " + id + " не существует. Или принадлежит не Вам.");
            }

            collectionControl.getLock().writeLock().unlock();
            return new Response("Элемент коллекции успешно удалён.");
        } else {
            collectionControl.getLock().writeLock().unlock();
            return new Response("Коллекция пуста.");
        }
    }
}
