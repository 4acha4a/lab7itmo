package com.r3235.server.commands;

import com.r3235.collection.Product;
import com.r3235.tools.Response;
import com.r3235.server.CollectionControl;
import com.r3235.tools.Task;
import java.util.ArrayList;
import java.util.Comparator;

public class CommandMaxById implements Command {
    @Override
    public Response executeTask(CollectionControl collectionControl, Task task) {
        collectionControl.getLock().readLock().lock();
        String msg;
        if (collectionControl.getCollection().size() == 0) {
            msg = "Коллекция пуста";
        } else {
            ArrayList<Product> collection = new ArrayList(collectionControl.getCollection());
            Product maxP = collection.stream().max(Comparator.comparing(product -> product.getId())).get();
            msg = "Элемент с максимальным id:\n"+maxP.toString();
        }
        collectionControl.getLock().readLock().unlock();
        return new Response(msg);
    }
}
