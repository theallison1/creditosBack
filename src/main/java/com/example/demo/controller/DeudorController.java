package com.example.demo.controller;

import com.example.demo.model.Deudor;
import com.example.demo.service.DeudorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/deudores")
public class DeudorController {

    @Autowired
    private DeudorService deudorService;

    @PostMapping
    public Deudor createDeudor(@RequestBody Deudor deudor) {
        return deudorService.saveDeudor(deudor);
    }

    @GetMapping
    public List<Deudor> getAllDeudores() {
        return deudorService.getAllDeudores();
    }
}