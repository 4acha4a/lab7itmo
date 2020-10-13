package com.r3235.tools;

import java.io.Serializable;

/**
 * Класс задания программе.
 *
 * @param <T> Первый аргумент .
 * @param <P> Второй аргумент.
 */
public class Task<T, P> implements Serializable {
    private CommandType type;
    private T argument;
    private String login;
    private String password;

    public void setLoginPassword(String login,String password) {
        this.login = login;
        this.password = password;
    }

    public Task(CommandType type) {
        this.type = type;
    }

    public Task(CommandType type, T argument) {
        this.type = type;
        this.argument = argument;
    }

    public String getLogin() {
        return login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public T getArgument() {
        return argument;
    }


    public CommandType getType() {
        return type;
    }
}
