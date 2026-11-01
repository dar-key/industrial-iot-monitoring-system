package com.factory.observer;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class Subject {
    private final ConcurrentHashMap<Class<? extends Event>, CopyOnWriteArrayList<Observer<?>>> registry = new ConcurrentHashMap<>();

    public <E extends Event> void register(Class<E> eventType, Observer<E> observer) {
        registry.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(observer);
    }

    public <E extends Event> void unregister(Class<E> eventType, Observer<E> observer) {
        List<Observer<?>> list = registry.get(eventType);
        if (list != null) {
            list.remove(observer);
        }
    }

    @SuppressWarnings("unchecked")
    public <E extends Event> void publish(E event) {
        List<Observer<?>> list = registry.get(event.getClass());
        if (list != null) {
            for (Observer<?> obs : list) {
                ((Observer<E>) obs).onEvent(event);
            }
        }
    }

    public <E extends Event> int getObserverCount(Class<E> eventType) {
        List<Observer<?>> list = registry.get(eventType);
        return list == null ? 0 : list.size();
    }
}
