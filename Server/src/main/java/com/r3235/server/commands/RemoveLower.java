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

public class RemoveLower implements Command {
    @Override
    public Response executeTask(CollectionControl collectionControl, Task task) {
        {
            int userId = collectionControl.getSqlManager().getUserId(task.getLogin());
            if (userId == -1) return new Response("Ошибка авторизации!");

            try {
                PreparedStatement statement = collectionControl.getSqlManager().getConnection().prepareStatement(
                        "delete from products where user_id = ? and price < ? returning products.id"
                );
                statement.setInt(1, userId);
                statement.setDouble(2, ((Product) task.getArgument()).getPrice());
                ResultSet resultSet = statement.executeQuery();
                ArrayList<Integer> ids = new ArrayList<>();
                while (resultSet.next())
                    ids.add(resultSet.getInt("id"));
                collectionControl.getLock().writeLock().lock();
                LinkedList<Product> products = collectionControl.getCollection();
                products.removeAll((products.parallelStream().filter(product -> ids.indexOf(product.getId()) != -1)
                        .collect(Collectors.toCollection(LinkedList::new))));
                collectionControl.getLock().writeLock().unlock();
                return new Response("Все элеменды меньше " + ((Product) task.getArgument()).getPrice() + " по цене удалены.");
            } catch (SQLException e) {
                collectionControl.getLock().writeLock().unlock();
                return new Response("Ошибка поиска объектов пользователя в базе.");
            }
        }
    }
}
