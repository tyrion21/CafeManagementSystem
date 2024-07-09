package com.jason.cafemanagamentsystem.rest;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.jason.cafemanagamentsystem.constants.CafeConstant;
import com.jason.cafemanagamentsystem.service.BillService;
import com.jason.cafemanagamentsystem.utils.CafeUtil;

@RestController
public class BillRestImpl implements BillRest{

    @Autowired
    BillService billService;
    @Override
    public ResponseEntity<String> generateReport(Map<String, Object> requestMap) {
        try {
            return billService.generateReport(requestMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtil.getResponseEntity(CafeConstant.SOMETHING_WENT_WRONG,HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
}
