package com.jason.cafemanagamentsystem.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.method.P;
import org.springframework.transaction.annotation.Transactional;

import com.jason.cafemanagamentsystem.models.Product;
import com.jason.cafemanagamentsystem.wrapper.ProductWrapper;

public interface ProductDao extends JpaRepository<Product, Integer>{

    List<ProductWrapper> getAllProduct();
    
    @Modifying
    @Transactional
    Integer updateProductStatus(@Param("status") String string, @Param("id") Integer id);
    
    List<ProductWrapper> getProductByCategory(@Param("id") Integer id);

    ProductWrapper getProductById(@Param("id") Integer id);
}
