package com.changhong.languagetool.controller;


import com.changhong.languagetool.mapper.LanguageMapper;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class LanguagesController {

    private final LanguageMapper languageMapper;

    @Autowired
    public LanguagesController(LanguageMapper languageMapper) {
        this.languageMapper = languageMapper;
    }

    @PostMapping("/language")
    public String updateLanguages(@RequestParam("olderLanguage")String olderLanguage,
                                        @RequestParam("newLanguage")String  newLanguage){

        if(olderLanguage==null||newLanguage==null||olderLanguage.equals("")||newLanguage.equals("")){
            return "语言更新失败-->更新语言不能为空";
        }
        Map<String ,String > map = new HashMap<>();
        map.put("older",olderLanguage);
        map.put("newer",newLanguage);
        try {
            languageMapper.updateLanguageName(map);
           return  "语言更新成功";
        }catch (Exception e){
            e.printStackTrace();
            return "语言更新失败";
        }

      }

      @DeleteMapping("/language")
    public  String deleteLanguage(@RequestParam("deleteLanguage")String deleteLanguage){

        if(deleteLanguage==null||deleteLanguage.equals("")){
            return "需要删除的语言不能为空";

        }else {
            try {
                languageMapper.dropOneLanguage(deleteLanguage.toLowerCase());
                return "删除语言成功";
            }catch (Exception e){
                e.printStackTrace();
                return "服务器出错-->删除失败";
            }
        }
      }
}
