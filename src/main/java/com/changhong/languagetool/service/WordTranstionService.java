package com.changhong.languagetool.service;


import com.changhong.languagetool.mapper.LanguageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class WordTranstionService {
    private final
    LanguageMapper languageMapper;

    @Autowired
    public WordTranstionService(LanguageMapper languageMapper) {
        this.languageMapper = languageMapper;
    }


    public List<String > getCountryLanguage(){
        try {
            List<String> res = languageMapper.selectCountryName();
            //砍掉id并且首字母变大写
            return  res.stream().skip(1).
                    map(s->s.substring(0, 1).toUpperCase() + s.substring(1))
                    .collect(Collectors.toList());
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }

    }


    public String inquireWordFromUser(String value, String language) {

        Map<String,String > inquire = new HashMap<>();
        inquire.put("column",language);
        inquire.put("index",value);
        Map<String, String> map = languageMapper.selectOtherLanguageByEnglish(inquire);

        if(map.get("value")!=null){
            if(map.get("value").equals("?")){
                return "Not Found";
            }
            return  map.get("value");
        }else {
            return  null;
        }
    }

    public void modiftWordFromUser(String values, String language) {
        //前台构建数据，::: 3个点前面的index,3个点
        //todo 更新前需要追加数据
        String[] split = values.split(":::");
        Map<String,String> map = new HashMap<>();
        map.put("index",split[0]);
        map.put("column",language);
        map.put("value",split[1]);


        languageMapper.updateColumn(map);

    }
}
