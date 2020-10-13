package com.r3235.server.commands;

import com.r3235.tools.Response;
import com.r3235.server.CollectionControl;
import com.r3235.tools.Task;
public class CommandInfo implements Command{
    public Response executeTask(CollectionControl collectionControl, Task task) {
        return new Response(collectionControl.toString());
    }
}
