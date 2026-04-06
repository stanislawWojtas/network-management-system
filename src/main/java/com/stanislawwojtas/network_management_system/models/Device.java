package com.stanislawwojtas.network_management_system.models;

import lombok.Data;

@Data
public class Device {
    private Integer id;
    private String name;
    private boolean active;
}
