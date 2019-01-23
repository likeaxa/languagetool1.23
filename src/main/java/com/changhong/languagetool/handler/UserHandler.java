package com.changhong.languagetool.handler;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class UserHandler implements HandlerInterceptor {

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

//            String username = (String)request.getSession().getAttribute("username");
//            if(username==null){
//                return false;
//            }
            return true;
        }
}
