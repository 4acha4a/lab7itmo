package com.r3235.tools;

import java.io.Serializable;

/**
 * Класс ответа программы на команду пользователя.
 */
public class Response implements Serializable {
    private final String msg;

    public Response(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}

