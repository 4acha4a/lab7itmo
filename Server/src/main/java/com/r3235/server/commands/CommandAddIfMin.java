package com.r3235.server.commands;

import com.r3235.tools.CommandType;
import com.r3235.collection.Product;
import com.r3235.tools.Response;
import com.r3235.server.CollectionControl;
import com.r3235.tools.Task;

import java.sql.SQLException;
import java.util.HashSet;
import java.util.LinkedList;

public class CommandAddIfMin implements Command {
    CommandAdd commandAdd;

    public CommandAddIfMin(CommandAdd commandAdd) {
        this.commandAdd = commandAdd;

    }

    @Override
    public Response executeTask(CollectionControl collectionControl, Task task) {

        Product addProduct = (Product) task.getArgument();
        collectionControl.getLock().readLock().lock();
        LinkedList<Product> products = collectionControl.getCollection();
        if (products.size() != 0) {
            Product minElem = products.stream().min(Product::compareTo).get();
            collectionControl.getLock().writeLock().unlock();
            if (addProduct.compareTo(minElem) < 0) {
                Task addTask = new Task(CommandType.ADD, addProduct);
                addTask.setLoginPassword(task.getLogin(), task.getPassword());
                return commandAdd.executeTask(collectionControl,addTask);
            } else {
                return new Response("Элемент не минимальный!");
            }
        } else {
            collectionControl.getLock().readLock().unlock();
            return new Response("Коллекция пуста, минимальный элемент отсутствует.");
        }
    }

}
