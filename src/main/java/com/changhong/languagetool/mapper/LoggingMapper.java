package com.changhong.languagetool.mapper;


import com.changhong.languagetool.bean.Logging;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoggingMapper {

    @Insert("insert into logging(version,times,type) values(#{version},#{times},#{type})")
    int insertLogging(Logging logging);

    @Select("select * from logging  order by times desc limit 30")
    List<Logging> getTop30Logging();

    @Select("select * from logging  order by times desc limit 1")
    Logging getTop1Logging();
}
