package com.stanislawwojtas.network_management_system.services;

import com.stanislawwojtas.network_management_system.models.Connection;
import com.stanislawwojtas.network_management_system.models.Device;
import com.stanislawwojtas.network_management_system.models.TopologyConnections;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TopologyService {

    private final Map<Integer, Device> devices = new ConcurrentHashMap<>();
    private final Map<Integer, List<Integer>> connections = new ConcurrentHashMap<>();

    @PostConstruct()
    public void loadData(){
        try{
            ObjectMapper mapper = new ObjectMapper();
            TopologyConnections data = mapper.readValue(new ClassPathResource("topology.json").getInputStream(), TopologyConnections.class);

            for (Device device : data.getDevices()){
                devices.put(device.getId(), device);
                connections.putIfAbsent(device.getId(), new ArrayList<>());
            }

            for (Connection connection : data.getConnections()){
                Integer a = connection.getFrom();
                Integer b = connection.getTo();

                connections.putIfAbsent(a, new ArrayList<>());
                connections.putIfAbsent(b, new ArrayList<>());

                connections.get(a).add(b);
                connections.get(b).add(a);
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void updateDeviceStatus(Integer id, boolean newActiveStatus){
        if(devices.containsKey(id)){
            devices.get(id).setActive(newActiveStatus);
        }
    }

    public Set<Integer> getReachableDevices(int id){
        Set<Integer> reachableDevices = new HashSet<>();
        Queue<Integer> queue = new LinkedList<>();
        Set<Integer> visited = new HashSet<>();

        queue.add(id);
        visited.add(id);

        while(!queue.isEmpty()){
            int node = queue.poll();
            for(int neighbour : connections.getOrDefault(node, Collections.emptyList())){
                if(!visited.contains(neighbour)){
                    visited.add(neighbour);
                    Device device = devices.get(neighbour);
                    if(device != null && device.isActive()){
                        reachableDevices.add(neighbour);
                        queue.add(neighbour);
                    }
                }
            }
        }

        return reachableDevices;
    }
}
