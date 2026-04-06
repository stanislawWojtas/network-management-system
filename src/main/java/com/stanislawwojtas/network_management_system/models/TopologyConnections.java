package com.stanislawwojtas.network_management_system.models;

import lombok.Data;

import java.util.List;

@Data
public class TopologyConnections {
    private List<Device> devices;
    private List<Connection> connections;
}
