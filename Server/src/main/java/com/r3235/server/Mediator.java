package com.r3235.server;

import com.r3235.Connector.UdpReader;
import com.r3235.Connector.UdpSender;
import com.r3235.tools.Response;
import com.r3235.tools.Task;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.channels.DatagramChannel;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Mediator {
    private final TaskWorker taskWorker;
    private final CollectionControl collectionControl;
    private final ExecutorService readPool = Executors.newFixedThreadPool(50);
    private final ExecutorService executePool = Executors.newFixedThreadPool(50);
    private UdpSender udpSender;
    private UdpReader udpReader;

    public Mediator(SQLManager sqlManager) {
        collectionControl = new CollectionControl(sqlManager);
        taskWorker = new TaskWorker(collectionControl);
    }

    public boolean init(int port) {
        try {
            System.out.println("Загрузка коллекции");
            if (!collectionControl.loadCollection()) return false;
            System.out.println("Создание канала...");
            DatagramChannel datagramChannel = DatagramChannel.open();
            InetSocketAddress inetSocketAddress = new InetSocketAddress(port);
            datagramChannel.bind(inetSocketAddress);
            System.out.println("Создание отправщика...");
            udpSender = new UdpSender(datagramChannel);
            System.out.println("Создание приёмника...");
            udpReader = new UdpReader(datagramChannel);
            initUdpReader();
            return true;
        } catch (SocketException e) {
            System.out.println("Ошибка создагия сокета. Возможно, порт занят.");
        } catch (IOException e) {
        }
        return false;
    }

    void start() {
        System.out.println("Запуск считывания команд команд.");
        udpReader.startListening();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext()) {
            String stringTask = scanner.nextLine();
            if (stringTask.equals("exit")) break;
            else System.out.println("Доступные комадны сервера: 'exit'.");
            System.out.print("->>");
        }
    }

    private void initUdpReader() {
        udpReader.setExecutor(this::threadRead);
    }

    /**
     * Запускает поток обработки запроса
     */
    private void threadRead(byte[] data, SocketAddress inputAddress) {
        readPool.execute(() -> {
            try (
                    ObjectInputStream objectInputStream = new ObjectInputStream(
                            new ByteArrayInputStream(data))
            ) {
                Task task = (Task) objectInputStream.readObject();
                objectInputStream.close();
                if (task != null)
                    threadProcessing(task, inputAddress);
                else System.out.println("Сообщение клиента " + inputAddress + " пустое.");
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Ошибка десериализации сообщения клиента.");
            }
        });
    }

    /**
     * Запускает поток обработки запроса
     */
    private void threadProcessing(Task task, SocketAddress inputAddress) {
        executePool.execute(() -> {
            Response response = taskWorker.executeTask(task);
            System.out.println("Команда " + task.getType() + " от пользователя " + task.getLogin() + "." +
                    "(Адрес: " + inputAddress + ") выполнена.");
            threadSend(response, inputAddress);
        });
    }

    /**
     * Запускает поток отправки ответа
     */
    private void threadSend(Response response, SocketAddress inputAddress) {
        new Thread(() -> {
            System.out.println("Отправка ответа на адрес " + inputAddress + ".");
            udpSender.send(response, inputAddress);
        }).start();
    }
}
