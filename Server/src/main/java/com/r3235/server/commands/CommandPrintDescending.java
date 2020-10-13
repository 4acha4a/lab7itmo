package com.r3235.server.commands;

import com.r3235.collection.Product;
import com.r3235.tools.Response;
import com.r3235.server.CollectionControl;
import com.r3235.tools.Task;
import java.util.ArrayList;

public class CommandPrintDescending implements Command {
    @Override
    public Response executeTask(CollectionControl collectionControl, Task task) {
        collectionControl.getLock().readLock().lock();
        StringBuilder msg;
        if (collectionControl.getCollection().size() == 0) {
            msg = new StringBuilder("Коллекция пуста");
        } else {
            msg = new StringBuilder("Сортировка по убыванию:\n");
            msg.append("id --- поле Price (цена) --- имя\n");
            ArrayList<Product> collection = new ArrayList(collectionControl.getCollection());
            collection.sort(Product::compareTo);
            for (Product product : collection) {
                msg.append(product.getId()).append(" --- ").append(product.getCreationDate()).append(" --- ").append(product.getName()).append("\n");
            }
        }
        collectionControl.getLock().readLock().unlock();
        return new Response(msg.toString());
    }
}
