package com.r3235.server;


import com.r3235.tools.CommandType;
import com.r3235.tools.Task;

import java.io.*;

import java.util.Properties;

/**
 * Main-класс
 */
public class MainConsole {
    /**
     * Стартовая точка программы. Считывает путь к файлу из переменной окружения и запускает обработчик команд.
     *
     * @param args Аргументы командной строки (не спользуются)
     */
    public static void main(String[] args) {
        File propFile = new File("setting.properties");
        System.out.println("Проверка файла настроек.");
        if (!propFile.exists()) {
            System.out.println("Создание файла настроек 'setting.properties'");
            try (InputStream in = MainConsole.class
                    .getClassLoader()
                    .getResourceAsStream("setting.properties");
                 OutputStream out = new FileOutputStream("setting.properties")) {
                int data;
                while ((data = in.read()) != -1) {
                    out.write(data);
                }
            } catch (IOException e) {
                System.out.println("Ошибка создания файла. Возможно, отсутствуют необходимые права.");
            }
            System.out.println("Был создан файл 'setting.properties'. внесите туда данные и запустите программу снова.");
            System.exit(0);
        }
        System.out.println("Файл 'setting.properties' существует. ");
        if (!propFile.canRead()) {
            System.out.println("Невозможно прочитать файл 'setting.properties' из-за оссутствия прав на чтение");
        } else
            try (InputStream inputStream = new FileInputStream("setting.properties")) {
                Properties properties = new Properties();
                properties.load(inputStream);
                System.out.println(properties);
                System.out.println("Чтение ключа port (int)");
                int serv_port = Integer.parseInt(properties.getProperty("port"));
                System.out.println("Чтение ключа port_b (int)");
                int port = Integer.parseInt(properties.getProperty("port_b"));
                System.out.println("Чтение ключа host_b (string)");
                String host = properties.getProperty("host_b");
                System.out.println("Чтение ключа name_b (string)");
                String name = properties.getProperty("name_b");
                System.out.println("Чтение ключа user_b (string)");
                String username = properties.getProperty("user_b");
                System.out.println("Чтение ключа password_b (string)");
                String password = properties.getProperty("password_b");

                SQLManager sqlManager = new SQLManager();
                if (!sqlManager.initDatabaseConnection(host, port, name, username, password) ||
                        !sqlManager.initTables()) {
                    System.out.println("Ошибка инициализации базы данных ;(");
                    System.exit(0);
                }

                Mediator mediator = new Mediator(sqlManager);
                if (mediator.init(serv_port)) mediator.start();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NumberFormatException e) {
                System.out.println("Ошибка ввода числа");

            }
        System.out.println("Работа программы завершена");

    }
}

