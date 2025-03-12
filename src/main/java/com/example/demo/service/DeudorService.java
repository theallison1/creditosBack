package com.example.demo.service;

import com.example.demo.model.Deudor;
import com.example.demo.repository.DeudorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DeudorService {

    @Autowired
    private DeudorRepository deudorRepository;

    public Deudor saveDeudor(Deudor deudor) {
        return deudorRepository.save(deudor);
    }

    public List<Deudor> getAllDeudores() {
        return deudorRepository.findAll();
    }


    public Optional<Deudor> findById(Long id) {
        return deudorRepository.findById(id);
    }
}