package com.r3235.server;

import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import com.r3235.collection.*;
import org.postgresql.core.SqlCommand;

/**
 * Класс доступа к коллекции
 */
public class CollectionControl {
    private final ReadWriteLock lock = new ReentrantReadWriteLock();
    LinkedList<Product> collection;
    SQLManager sqlManager;

    public CollectionControl(SQLManager sqlManager) {
        this.sqlManager = sqlManager;
    }

    public boolean loadCollection() {
        collection = sqlManager.loadCollection();
        if (collection == null)
            return false;
        return true;
    }

    public SQLManager getSqlManager() {
        return sqlManager;
    }

    public LinkedList<Product> getCollection() {
        return collection;
    }

    public void setCollection(LinkedList<Product> collection) {
        this.collection = collection;
    }

    /**
     * Выводит информацию о коллекции.
     */
    @Override
    public String toString() {
        return "Информация о коллекции:\n" +
                "Тип коллекции: " + collection.getClass().toString();
    }

    public ReadWriteLock getLock() {
        return lock;
    }
}

