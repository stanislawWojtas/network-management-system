package com.stanislawwojtas.network_management_system.controllers;

import com.stanislawwojtas.network_management_system.models.DeviceUpdateRequestDto;
import com.stanislawwojtas.network_management_system.services.SseService;
import com.stanislawwojtas.network_management_system.services.TopologyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/devices")
public class DeviceController {

    private final TopologyService topologyService;
    private final SseService sseService;

    public DeviceController(TopologyService topologyService, SseService sseService) {
        this.topologyService = topologyService;
        this.sseService = sseService;
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> modifyDevice(@PathVariable int id, @RequestBody DeviceUpdateRequestDto dto){
        topologyService.updateDeviceStatus(id, dto.isActive());
        sseService.notifyAllSubscribers();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/reachable-devices")
    public SseEmitter getReachableDevices(@PathVariable int id){
        return sseService.subscribe(id);
    }
}
