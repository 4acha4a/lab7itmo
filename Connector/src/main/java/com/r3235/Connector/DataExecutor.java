package com.r3235.Connector;

import java.net.SocketAddress;

/**
 * Слушатель приёмника.
 */
public interface DataExecutor {
    /**
     * Вызывается для обработки принятых данных.
     *
     * @param data         Данные
     * @param inputAddress Адрес отправителя
     */
    void execute(byte[] data, SocketAddress inputAddress);
}
