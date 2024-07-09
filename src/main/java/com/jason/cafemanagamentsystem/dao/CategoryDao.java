package com.jason.cafemanagamentsystem.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.jason.cafemanagamentsystem.models.Category;

public interface CategoryDao extends JpaRepository<Category, Integer>{
    
    List<Category> getAllCategory();
}
