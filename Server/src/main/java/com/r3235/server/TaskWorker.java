package com.r3235.server;

import com.r3235.server.commands.*;
import com.r3235.tools.CommandType;
import com.r3235.tools.Response;
import com.r3235.tools.Task;

import java.util.HashMap;

/**
 * Класс обработки заданий.
 */
public class TaskWorker {
    CollectionControl collectionControl;

    HashMap<CommandType, Command> commandList = new HashMap<>();

    {
        commandList.put(CommandType.CLEAR, new CommandClear());
        commandList.put(CommandType.EXECUTE_SCRIPT, new CommandExecuteScript(this));
        commandList.put(CommandType.HELP, new CommandHelp());
        commandList.put(CommandType.ADD, new CommandAdd());
        commandList.put(CommandType.PRINT_DESCENDING, new CommandPrintDescending());
        commandList.put(CommandType.REMOVE_BY_ID, new CommandRemoveById());
        commandList.put(CommandType.UPDATE, new CommandUpdate());
        commandList.put(CommandType.SHOW, new CommandShow());
        commandList.put(CommandType.REMOVE_LOWER, new RemoveLower());
        commandList.put(CommandType.INFO, new CommandInfo());
        commandList.put(CommandType.ADD_IF_MIN, new CommandAddIfMin(new CommandAdd()));
        commandList.put(CommandType.MAX_BY_ID, new CommandMaxById());
        commandList.put(CommandType.REG, new CommandReg());
        commandList.put(CommandType.LOGIN, new CommandLogin());
    }

    public TaskWorker(CollectionControl collectionControl) {
        this.collectionControl = collectionControl;
    }

    /**
     * Выполняет задание.
     *
     * @param task Задание, сформированное из команды.
     * @return Ответ, результат выполнения.
     */
    public Response executeTask(Task task) {
        if (task.getLogin() != null && task.getPassword() != null && task.getType() != null) {
            if (task.getType() == CommandType.REG)
            {task.setPassword(sha1(task.getPassword()));
                return commandList.get(CommandType.REG).executeTask(collectionControl, task);}
            else if (collectionControl.getSqlManager().checkAccount(task.getLogin(), sha1(task.getPassword()))||
                    task.getType() == CommandType.EXECUTE_SCRIPT||task.getType() == CommandType.HELP) {
                CommandType commandType = task.getType();
                Command command = commandList.get(commandType);
                Response response = command.executeTask(collectionControl, task);
                return response;
            } else {
                return new Response("Неверный пароль для пользователя " + task.getLogin() + " или пользователь не существует");
            }
        }
        return new Response("Невозможно обработать сообщение.");
    }

    public static String sha1(String bb) {
        String result = "";
        byte[] b = bb.getBytes();
        for (int i = 0; i < b.length; i++) {
            result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
        }
        return result;

    }
}
