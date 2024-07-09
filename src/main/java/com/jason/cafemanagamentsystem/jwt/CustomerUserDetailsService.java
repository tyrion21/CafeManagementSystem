package com.jason.cafemanagamentsystem.jwt;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.jason.cafemanagamentsystem.dao.UserDao;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

@Slf4j
@Service
public class CustomerUserDetailsService implements UserDetailsService{
    

    @Autowired
    UserDao userDao;

    private com.jason.cafemanagamentsystem.models.User userDetail;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Username: " + username);
        userDetail = userDao.findByEmailId(username); 
        if(!Objects.isNull(userDetail)){
            return new org.springframework.security.core.userdetails.User(userDetail.getEmail(), userDetail.getPassword(), new ArrayList<>());
        }else{
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }

    public com.jason.cafemanagamentsystem.models.User getUserDetail() {
        return userDetail;
    }
}
