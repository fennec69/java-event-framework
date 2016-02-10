package com.fennec.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by fennec on 11/01/2016.
 */
public class EventService {

    private static EventService INSTANCE = new EventService();
    private Map<Class<? extends Event>, Map<Subscriber, List<Filter>>> subscriptionMap = new ConcurrentHashMap<>();

    private EventService() {
    }

    public static EventService getInstance() {
        return INSTANCE;
    }

    public void publish(Event event) {

        Map<Subscriber, List<Filter>> subscription = subscriptionMap.get(event.getClass());

        if (subscription != null) {
            for (Subscriber subscriber : subscription.keySet()) {
                for (Filter filter : subscription.get(subscriber)) {
                    if (filter == null || filter.apply(event)) {
                        subscriber.inform(event);
                    }
                }
            }
        }
    }

    public void subscribe(Class<? extends Event> eventType, Filter filter, Subscriber subscriber) {

        if (!subscriptionMap.containsKey(eventType)) {
            subscriptionMap.put(eventType, new HashMap<Subscriber, List<Filter>>());
        }

        Map<Subscriber, List<Filter>> subscriberListMap = subscriptionMap.get(eventType);

        if (!subscriberListMap.containsKey(subscriber)) {
            subscriberListMap.put(subscriber, new ArrayList<Filter>());
        }

        if (!subscriberListMap.get(subscriber).contains(filter)) {
            subscriberListMap.get(subscriber).add(filter);
        }
    }

    public void unsubscribe(Subscriber subscriber) {
        for (Map<Subscriber, List<Filter>> subscriberListMap : subscriptionMap.values()) {
            subscriberListMap.remove(subscriber);
        }
    }
}
