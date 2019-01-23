package com.changhong.languagetool.mapper;


import com.changhong.languagetool.bean.Language;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface LanguageMapper {


      @Update("update languages set ${newer} = ${older}")
      int copyOlderToNew(Map<String ,String > map);

      @Update("alter table languages DROP COLUMN ${_parameter}")
      int dropOneLanguage(String  language);


      @Update("alter table languages CHANGE ${older} ${newer} text")
      int  updateLanguageName(Map<String ,String> map );




      @Insert("insert into languages(english) values(#{english})")
      int insert(Map map);

      @Select(" select ${_parameter} from languages")
      List<Map<String  ,String >> selectAllLanguage(String  column);


      @Insert(" insert into languages(english) values(#{_parameter})")
      int insertConlumnFromEnglish(String index);
      //修改一列的内容;
      @Update("UPDATE languages SET ${column} = #{value} WHERE english = binary(#{index})")
      int updateColumn(Map<String,String >  data);


         @Select(" select * from test")
        List<Map<Object ,String >> test();
      //查询表中的列
      @Select(" select COLUMN_NAME " +
              "from INFORMATION_SCHEMA.Columns " +
              "where table_name='languages' and table_schema='languagetool' ")
      List<String> selectCountryName();


      //插入表的一个字段，只有一个参数时（int，string）需要使用_parameter来获取 注意#和$的区别
      @Update("ALTER TABLE languages" +
              "  ADD COLUMN ${_parameter} text DEFAULT null ")
      int addCountryLanguageColumn(String column);

      @Insert("${_parameter}")
      int  addOneLineData(String sqlData);

      @Delete("drop table  ${_parameter}")
      int deleteTable(String  ss);
      @Select("select distinct(${column}) as value \n" +
              "from languages \n" +
              " where english = binary(#{index});")
      Map<String ,String > selectOtherLanguageByEnglish(Map<String,String> ss);


}
