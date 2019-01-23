package com.changhong.languagetool.service;

import com.changhong.languagetool.bean.Language;
import com.changhong.languagetool.mapper.LanguageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DeleteDBService {


    private final LanguageMapper languageMapper;

    @Autowired
    public DeleteDBService(LanguageMapper languageMapper) {
        this.languageMapper = languageMapper;
    }

    public void deleteDBData() {

        String sql = "create table languages(\n" +
                "\tid  int not null auto_increment PRIMARY key \n" +
                ")ENGINE  =MyISAM ;";
        try {
            languageMapper.deleteTable("languages");
            languageMapper.addOneLineData(sql);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
