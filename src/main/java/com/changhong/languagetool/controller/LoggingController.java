package com.changhong.languagetool.controller;


import com.changhong.languagetool.bean.Logging;
import com.changhong.languagetool.mapper.LoggingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class LoggingController {


    private final LoggingMapper loggingMapper;

    @Autowired
    public LoggingController(LoggingMapper loggingMapper) {
        this.loggingMapper = loggingMapper;
    }

    @GetMapping("/logging")
    public List<Logging> getLogging(){
        try {
            List<Logging> top30Logging = loggingMapper.getTop30Logging();
            for(Logging logging : top30Logging){
                logging.setTime(logging.getTimes().getTime());
            }
           return top30Logging;

        }catch (Exception e){
            e.printStackTrace();
            return null;
        }


    }
}
