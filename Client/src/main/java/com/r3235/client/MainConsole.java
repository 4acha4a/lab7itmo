package com.r3235.client;

import com.r3235.Connector.PingChecker;
import com.r3235.Connector.UdpReader;
import com.r3235.Connector.UdpSender;
import com.r3235.tools.CommandType;
import com.r3235.tools.Response;
import com.r3235.tools.Task;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.*;
import java.nio.channels.UnresolvedAddressException;
import java.util.Scanner;

/**
 * Main-класс
 */
public class MainConsole {
    /**
     * Стартовая точка программы. Считывает путь к файлу из переменной окружения и запускает обработчик команд.
     *
     * @param args Аргументы командной строки (не спользуются)
     */
    public static void main(String[] args) throws SocketException {
        System.out.println("ВНИМАНИЕ! Если вы перестаните получать ответы от сервера, значит с ним что-то случилось.");
        System.out.println("Для записи объектов в скриптах перечислите их поля через ';' в той же строке, что и команда.");
        System.out.println("Запуск клиента:");
        System.out.println("Запуск программы");
        TaskManager taskManager = new TaskManager();
        Scanner scanner = new Scanner(System.in);
        InetSocketAddress socketAddress = null;
        do {
            System.out.println("Для началаведите адрес сервера в формате \"адрес:порт\" или 'exit' для выхода.");
            System.out.print("->>");
            String inputString = scanner.nextLine();
            if (inputString.equals("exit")) {
                System.exit(0);
            } else {
                try {
                    String[] trimString = inputString.trim().split(":", 2);
                    String address = trimString[0];
                    int port = Integer.parseInt(trimString[1]);
                    if (port < 0 || port > 65535) {
                        System.out.println("Порт - число от 0 до 65535.");
                        continue;
                    }
                    socketAddress = new InetSocketAddress(address, port);
                    System.out.println("Клиент запущен успешно. Данные сервера:\n Потр: " + port + ". Адрес: " + socketAddress);
                    PingChecker pingChecker = new PingChecker();
                    if (pingChecker.ping(new Task(CommandType.HELP), socketAddress) != -1) {
                        System.out.println("Проверка соединения успешна.");
                        break;
                    } else {
                        System.out.println("Проблемы соедидения с сервером");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Ошибка в записи номера порта.");
                } catch (IndexOutOfBoundsException | UnresolvedAddressException e) {
                    System.out.println("Адрес введён некорректно.");
                }
                System.out.print("->>");
            }
            if (!scanner.hasNextLine()) System.exit(0);
        } while (scanner.hasNextLine());

        //запуск считывателя
        DatagramSocket datagramSocket = new DatagramSocket();
        UdpReader udpReader = new UdpReader(datagramSocket);
        udpReader.setExecutor((data, inputAddress) -> {
            try (
                    ObjectInputStream objectInputStream = new ObjectInputStream(
                            new ByteArrayInputStream(data))
            ) {
                Response response = (Response) objectInputStream.readObject();
                objectInputStream.close();
                if (response != null) {
                    if (response.getMsg() != null) {
                        System.out.println();
                        System.out.println(response.getMsg());
                        System.out.print("->>");
                    }
                } else System.out.println("Сообщение сервера пустое.");

            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Ошибка десериализации сообщения сервера.");
            }
        });
        udpReader.startListening();
        UdpSender udpSender = new UdpSender(datagramSocket);
        System.out.println("Оно всё ещё не сломалось. Странно, это значит, что можно работать дальше.");
        System.out.print("->>");
        while (scanner.hasNextLine()) {
            String stringTask = scanner.nextLine();
            Task task = taskManager.getTask(stringTask, false);
            if (task != null) {
                System.out.println("Отправка команды");
                udpSender.send(task, socketAddress);
            }
            System.out.print("->>");
        }
        System.out.println("Работа программы завершена");
    }
}
