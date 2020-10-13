package com.r3235.server.commands;

import com.r3235.server.TaskWorker;
import com.r3235.tools.Response;
import com.r3235.server.CollectionControl;
import com.r3235.tools.Task;

import java.util.ArrayList;

public class CommandExecuteScript implements Command {
    TaskWorker taskWorker;

    public CommandExecuteScript(TaskWorker taskWorker) {
        this.taskWorker = taskWorker;
    }

    @Override
    public Response executeTask(CollectionControl collectionControl, Task task) {
        String msg = "";
        ArrayList<Task> taskList = (ArrayList<Task>) task.getArgument();
        for (Task littleTask : taskList) {
            littleTask.setLoginPassword(task.getLogin(),task.getPassword());
            msg = msg + taskWorker.executeTask(littleTask).getMsg();
        }
        return new Response(msg);
    }
}
