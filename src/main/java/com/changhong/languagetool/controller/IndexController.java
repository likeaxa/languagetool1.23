package com.changhong.languagetool.controller;


import com.changhong.languagetool.bean.User;
import com.changhong.languagetool.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class IndexController {

    private final UserMapper userMapper;

    @Autowired
    public IndexController(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @RequestMapping("/fileupload")
    public String fileUpload(){
        return  "File";
    }

    @RequestMapping("/")
    public String fileUploads(){
        return  "File";
    }

    @RequestMapping("multifile")
    public String multifile(){
        return "multifile";
    }

    @GetMapping("/user")
    public  String login(@RequestParam("username")String  username,
                         @RequestParam("password")String  password,
                         HttpServletRequest request){

        User user = userMapper.getPasswordByUsername(username);
        if(null!=user&&user.getPassword().equals(password)){
           // request.getSession().setAttribute("username",username);
            return "adminFiles";
        }else {
            return "File";
        }
    }
}
