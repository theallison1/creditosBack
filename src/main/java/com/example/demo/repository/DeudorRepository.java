package com.example.demo.repository;


import com.example.demo.model.Deudor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DeudorRepository extends JpaRepository<Deudor, Long> {
    List<Deudor> findByMontoPendienteGreaterThan(double montoPendiente);

}