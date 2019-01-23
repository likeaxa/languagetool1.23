package com.changhong.languagetool.controller;


import com.changhong.languagetool.service.DeleteDBService;
import com.changhong.languagetool.service.LoggingService;
import com.changhong.languagetool.service.ReadExcel;
import com.changhong.languagetool.service.WordTranstionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class WordTranstionCpoontroller {

    private  final
    WordTranstionService wordTranstionService;

    private final DeleteDBService deleteDBService;

    private final ReadExcel readExcel;

    private final LoggingService loggingService;
    @Autowired
    public WordTranstionCpoontroller(WordTranstionService wordTranstionService, ReadExcel readExcel, DeleteDBService deleteDBService, LoggingService loggingService) {
        this.wordTranstionService = wordTranstionService;
        this.readExcel = readExcel;
        this.deleteDBService = deleteDBService;
        this.loggingService = loggingService;
    }

    @GetMapping("/deleteDB")
    public String  deleteDB(){
        try {
            deleteDBService.deleteDBData();
            return "true";
        }catch (Exception e){
            e.printStackTrace();
            return  "false";
        }
    }

    //更新数据库
    @GetMapping("/updateword")
    public String updateWord(@RequestParam("value")String value,
                             @RequestParam("language")String  language){
        try {
            language = language.toLowerCase();
            value = readExcel.checkRes(value);
            wordTranstionService.modiftWordFromUser(value, language);
            loggingService.addLogging("updateWord");
            return "ok";
        } catch (Exception e) {
            e.printStackTrace();
            return "false";
        }
    }
    @GetMapping("/worldtranstion")
    public String worldTranstion(@RequestParam("value") String value,
                                 @RequestParam("language") String language,
                                 @RequestParam("type") String type) {
        try {
            language = language.toLowerCase();
            value = readExcel.checkRes(value);
            if (type.equalsIgnoreCase("inquire")) {
                return wordTranstionService.inquireWordFromUser(value, language);
            }
//            if (type.equalsIgnoreCase("modify")) {
//               wordTranstionService.modiftWordFromUser(value, language);
//               return "modify success";
//            }
            return "Not Found";
        } catch (Exception e) {
            e.printStackTrace();
            return "Not Found";
        }


    }

    @GetMapping("/getcountry")
    public List<String> getCountryLanguages() {
        return wordTranstionService.getCountryLanguage();
    }
}
