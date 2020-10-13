package com.r3235.server.commands;

import com.r3235.tools.CommandType;
import com.r3235.collection.Product;
import com.r3235.tools.Response;
import com.r3235.server.CollectionControl;
import com.r3235.tools.Task;

public class CommandHelp implements Command {
    @Override
    public Response executeTask(CollectionControl collectionControl, Task task) {
        String msg = "Доступные команды:\n";
        for (CommandType commandType : CommandType.values()) {
            msg = msg + commandType.getHelp() + "\n";
        }
        return new Response(msg);
    }
}
