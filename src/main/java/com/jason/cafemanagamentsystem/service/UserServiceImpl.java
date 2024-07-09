package com.jason.cafemanagamentsystem.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.google.common.base.Strings;
import com.jason.cafemanagamentsystem.constants.CafeConstant;
import com.jason.cafemanagamentsystem.dao.UserDao;
import com.jason.cafemanagamentsystem.jwt.CustomerUserDetailsService;
import com.jason.cafemanagamentsystem.jwt.JwtFilter;
import com.jason.cafemanagamentsystem.jwt.JwtUtil;
import com.jason.cafemanagamentsystem.models.User;
import com.jason.cafemanagamentsystem.utils.CafeUtil;
import com.jason.cafemanagamentsystem.utils.EmailUtil;
import com.jason.cafemanagamentsystem.wrapper.UserWrapper;

import javax.xml.bind.DatatypeConverter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserDao userDao;

    @Autowired
    CustomerUserDetailsService customerUserDetailsService;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JwtFilter jwtFilter;

    @Autowired
    EmailUtil emailUtil;

    @Override
    public ResponseEntity<String> signUp(Map<String, String> requestMap) {
        log.info("Inside signUp method of UserServiceImpl", requestMap);
        try {
            if (validateSignUpMap(requestMap)) {
                User user = userDao.findByEmailId(requestMap.get("email"));
                if (Objects.isNull(user)) {
                    userDao.save(getUserFromMap(requestMap));
                    return CafeUtil.getResponseEntity(CafeConstant.SUCESS_REGISTER, HttpStatus.OK);

                } else {
                    return CafeUtil.getResponseEntity(CafeConstant.EMAIL_ALREADY_EXISTS, HttpStatus.BAD_REQUEST);
                }
            } else {
                log.error("Invalid request map");
                return CafeUtil.getResponseEntity(CafeConstant.INVALID_DATA, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtil.getResponseEntity(CafeConstant.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private boolean validateSignUpMap(Map<String, String> requestMap) {
        if (requestMap.containsKey("name") && requestMap.containsKey("email") && requestMap.containsKey("password")
                && requestMap.containsKey("phone")) {
            return true;
        }
        return false;
    }

    private User getUserFromMap(Map<String, String> requestMap) {
        User user = new User();
        user.setName(requestMap.get("name"));
        user.setPhone(requestMap.get("phone"));
        user.setEmail(requestMap.get("email"));
        // user.setPassword(requestMap.get("password"));
        user.setPassword(passwordEncoder.encode(requestMap.get("password")));
        user.setRole("USER");
        user.setStatus("false");
        return user;
    }

    @Override
    public ResponseEntity<String> login(Map<String, String> requestMap) {
        log.info("inside login");
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(requestMap.get("email"), requestMap.get("password")));
            if (auth.isAuthenticated()) {
                if (customerUserDetailsService.getUserDetail().getStatus().equalsIgnoreCase("true")) {
                    return new ResponseEntity<String>("{\"token\":\"" +
                            jwtUtil.generateToken(customerUserDetailsService.getUserDetail().getEmail(),
                                    customerUserDetailsService.getUserDetail().getRole())
                            + "\"}", HttpStatus.OK);
                } else {
                    return new ResponseEntity<String>("{\"message\":\"Wait for admin approval\"}",
                            HttpStatus.BAD_REQUEST);
                }
            }

        } catch (Exception e) {
            log.error(null, e);
        }

        return new ResponseEntity<String>("{\"message\":\"Invalid email or password\"}", HttpStatus.BAD_REQUEST);
    }
    // @Override
    // public ResponseEntity<String> login(Map<String, String> requestMap) {
    // log.info("inside login");
    // try {
    // Authentication auth = authenticationManager.authenticate(
    // new UsernamePasswordAuthenticationToken(requestMap.get("email"),
    // requestMap.get("password")));
    // if (auth.isAuthenticated()) {
    // if
    // (customerUserDetailsService.getUserDetail().getStatus().equalsIgnoreCase("true"))
    // {
    // return new ResponseEntity<String>("{\"message\":\"Login successful\"}",
    // HttpStatus.OK);
    // } else {
    // return new ResponseEntity<String>("{\"message\":\"Wait for admin
    // approval\"}",
    // HttpStatus.BAD_REQUEST);
    // }
    // }
    // } catch (Exception e) {
    // log.error(null, e);
    // }

    @Override
    public ResponseEntity<List<UserWrapper>> getAllUser() {
        try {
            if (jwtFilter.isAdmin()) {
                return new ResponseEntity<>(userDao.getAllUser(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(new ArrayList<>(), HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new ResponseEntity<>(new ArrayList<>(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Override
    public ResponseEntity<String> update(Map<String, String> requestMap) {
        try {
            if (jwtFilter.isAdmin()) {
                Optional<User> optional = userDao.findById(Integer.parseInt(requestMap.get("id")));
                sendMailToAllAdmin(requestMap.get("status"), optional.get().getEmail(), userDao.getAllAdmin());
                if (!optional.isEmpty()) {
                    userDao.updateStatus(requestMap.get("status"), Integer.parseInt(requestMap.get("id")));
                    return CafeUtil.getResponseEntity(CafeConstant.SUCCESS_UPDATE, HttpStatus.OK);
                } else {
                    return CafeUtil.getResponseEntity(CafeConstant.USER_NOT_FOUND, HttpStatus.BAD_REQUEST);
                }

            } else {
                return CafeUtil.getResponseEntity(CafeConstant.UNAUTHORIZED_ACCESS, HttpStatus.UNAUTHORIZED);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtil.getResponseEntity(CafeConstant.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private void sendMailToAllAdmin(String status, String user, List<String> allAdmin) {
        allAdmin.remove(jwtFilter.getCurrentUser());
        if (status != null && status.equalsIgnoreCase("true")) {
            emailUtil.sendSimpleMessage(jwtFilter.getCurrentUser(), "Account Approved",
                    "USER:- " + user + "\n is approved by \nADMIN:-" + jwtFilter.getCurrentUser(), allAdmin);
        } else {
            emailUtil.sendSimpleMessage(jwtFilter.getCurrentUser(), "Account Disabled",
                    "USER:- " + user + "\n is disabled by \nADMIN:-" + jwtFilter.getCurrentUser(), allAdmin);
        }
    }

    @Override
    public ResponseEntity<String> checkToken() {
        return CafeUtil.getResponseEntity("true", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<String> changePassword(Map<String, String> requestMap) {
        try {
            User userObj = userDao.findByEmailId(jwtFilter.getCurrentUser());
            if (userObj != null) {
                if (passwordEncoder.matches(requestMap.get("oldPassword"), userObj.getPassword())) {
                    userObj.setPassword(passwordEncoder.encode(requestMap.get("newPassword"))); 
                    userDao.save(userObj);
                    return CafeUtil.getResponseEntity("Password changed successfully", HttpStatus.OK);
                }
                return CafeUtil.getResponseEntity(CafeConstant.INCORRECT_PASSWORD, HttpStatus.BAD_REQUEST);
            }
            return CafeUtil.getResponseEntity(CafeConstant.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            e.printStackTrace();
            return CafeUtil.getResponseEntity(CafeConstant.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<String> forgotPassword(Map<String, String> requestMap) {
        try {
            User user = userDao.findByEmail(requestMap.get("email"));
            if(!Objects.isNull(user) && !Strings.isNullOrEmpty(user.getEmail()))
            emailUtil.forgotMail(user.getEmail(),"Credentials by Cafe Management System", user.getPassword());
            return CafeUtil.getResponseEntity("Password sent to your email", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return CafeUtil.getResponseEntity(CafeConstant.SOMETHING_WENT_WRONG, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    
}
