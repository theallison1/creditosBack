package com.example.demo.repository;


import com.example.demo.model.Deudor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeudorRepository extends JpaRepository<Deudor, Long> {
}