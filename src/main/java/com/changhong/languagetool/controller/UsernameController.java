package com.changhong.languagetool.controller;


import com.changhong.languagetool.bean.User;
import com.changhong.languagetool.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UsernameController {

    private final UserMapper userMapper;

    @Autowired
    public UsernameController(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @GetMapping("/alluser")
    public List<User> getAllUser(){
        return userMapper.getAllUser();
    }


}
