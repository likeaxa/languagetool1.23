package com.changhong.languagetool.service;

import com.changhong.languagetool.bean.Logging;
import com.changhong.languagetool.mapper.LoggingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;

@Service
public class LoggingService {

    private final LoggingMapper loggingMapper;

    @Autowired
    public LoggingService(LoggingMapper loggingMapper) {
        this.loggingMapper = loggingMapper;
    }

    public void addLogging(String type){
        //Todo 日志追加
        Logging top1Logging = loggingMapper.getTop1Logging();
        Logging logging = new Logging();
        Double aDouble = Double.valueOf(top1Logging.getVersion());
        if(type.equals("allFile")){
            short i = aDouble.shortValue();
            i++;
            String s = String.valueOf(i);
            aDouble=Double.valueOf(s);
            aDouble +=0.000;
        }else {
            aDouble+=0.001;
        }
        BigDecimal bg = new BigDecimal(aDouble).setScale(3, RoundingMode.UP);
        aDouble = bg.doubleValue();
        logging.setType(type);
        logging.setTimes(new Date());
        logging.setVersion(aDouble.toString());
        loggingMapper.insertLogging(logging);
    }
}
