package com.r3235.server.commands;

import com.r3235.server.CollectionControl;
import com.r3235.tools.Response;
import com.r3235.tools.Task;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CommandReg implements Command {
    @Override
    public Response executeTask(CollectionControl collectionControl, Task task) {
        try {
            PreparedStatement statement = collectionControl.getSqlManager().getConnection().prepareStatement(
                    "insert into users (username, password_hash) values (?, ?) "
            );
            statement.setString(1, task.getLogin());
            statement.setBytes(2, task.getPassword().getBytes());
            statement.execute();
        } catch (SQLException e) {
            return new Response("Пользователь " + task.getLogin() + " уже существует!");
        }
        return new Response("Пользователь с именем " + task.getLogin() + " создан.");
    }
}
