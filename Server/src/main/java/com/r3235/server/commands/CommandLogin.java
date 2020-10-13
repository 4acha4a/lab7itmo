package com.r3235.server.commands;

import com.r3235.server.CollectionControl;
import com.r3235.tools.Response;
import com.r3235.tools.Task;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class CommandLogin implements Command {
    @Override
    public Response executeTask(CollectionControl collectionControl, Task task) {
        if (collectionControl.getSqlManager().checkAccount(task.getLogin(), sha1(task.getPassword())))
            return new Response("Авторизация успешна");
        else return new Response("Данные пользователя неверны");

    }

    private String sha1(String bb) {
        String result = "";
        byte[] b = bb.getBytes();
        for (int i = 0; i < b.length; i++) {
            result += Integer.toString((b[i] & 0xff) + 0x100, 16).substring(1);
        }
        return result;

    }
}
