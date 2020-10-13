package com.r3235.server.commands;

import com.r3235.tools.Response;
import com.r3235.server.CollectionControl;
import com.r3235.tools.Task;

public class CommandShow implements Command {
    @Override
    public Response executeTask(CollectionControl collectionControl, Task task) {
        StringBuilder msg;
        collectionControl.getLock().readLock().lock();
        if (collectionControl.getCollection().size() == 0) {
            msg = new StringBuilder("Коллекция пуста");
        } else {
            msg = new StringBuilder("Список коллекции:\n");
            collectionControl.getCollection().forEach(product -> msg.append(product.toString()).append("\n"));
        }
        collectionControl.getLock().readLock().unlock();
        return new Response(msg.toString());
    }
}
