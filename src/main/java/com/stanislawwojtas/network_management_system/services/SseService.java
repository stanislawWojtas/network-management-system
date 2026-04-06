package com.stanislawwojtas.network_management_system.services;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class SseService {

    private final TopologyService topologyService;

    private static class Subscription {
        SseEmitter emitter;
        int startId;
        Set<Integer> currentReachable;

        Subscription(SseEmitter emitter, int startId, Set<Integer> currentReachable) {
            this.emitter = emitter;
            this.startId = startId;
            this.currentReachable = currentReachable;
        }
    }

    private final Map<String, Subscription> subscriptions = new ConcurrentHashMap<>();

    public SseService(TopologyService topologyService) {
        this.topologyService = topologyService;
    }

    public SseEmitter subscribe(int deviceId){
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        String subscriptionId = UUID.randomUUID().toString(); //generating unique id for every subscription
        Set<Integer> reachableDevices = topologyService.getReachableDevices(deviceId);

        subscriptions.put(subscriptionId, new Subscription(emitter, deviceId, reachableDevices));

        emitter.onCompletion(() -> subscriptions.remove(subscriptionId));
        emitter.onTimeout(() -> subscriptions.remove(subscriptionId));
        emitter.onError((e) -> subscriptions.remove(subscriptionId));

        //initial state
        try{
            Map<String, Object> initialEvent = new HashMap<>();
            initialEvent.put("type","INITIAL_STATE");
            initialEvent.put("deviceIds", reachableDevices);
            emitter.send(initialEvent);
        }catch (Exception e){
            subscriptions.remove(subscriptionId);
        }

        return emitter;
    }

    public void notifyAllSubscribers(){
        for(Map.Entry<String, Subscription> entry : subscriptions.entrySet()){
            Subscription sub = entry.getValue();

            Set<Integer> reachableDevices = topologyService.getReachableDevices(sub.startId);

            Set<Integer> added = new HashSet<>(reachableDevices);
            Set<Integer> removed = new HashSet<>(sub.currentReachable);

            added.removeAll(sub.currentReachable);
            removed.removeAll(reachableDevices);

            try{
                for(int id : added){
                    sendMessage(sub.emitter, "ADDED", id);
                }
                for(int id : removed){
                    sendMessage(sub.emitter, "REMOVED", id);
                }
                //update reachable devices
                sub.currentReachable =  reachableDevices;
            }catch (IOException e){
                //id error - delete client
                subscriptions.remove(entry.getKey());
            }
        }
    }

    private void sendMessage(SseEmitter emitter, String type, int deviceId) throws IOException {
        Map<String, Object> event = new HashMap<>();
        event.put("type", type);
        event.put("deviceId", deviceId);
        emitter.send(event);
    }

}
