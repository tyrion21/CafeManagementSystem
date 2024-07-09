package com.jason.cafemanagamentsystem.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jason.cafemanagamentsystem.models.Bill;

public interface BillDao extends JpaRepository<Bill, Integer>{

    
} 