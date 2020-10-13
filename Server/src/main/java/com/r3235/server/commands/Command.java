package com.r3235.server.commands;

import com.r3235.tools.Response;
import com.r3235.server.CollectionControl;
import com.r3235.tools.Task;

/**
 * Интерфейс обработчика команды.
 */
public interface Command {
    Response executeTask(CollectionControl collectionControl, Task task);

}
