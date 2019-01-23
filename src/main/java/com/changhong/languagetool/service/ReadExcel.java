package com.changhong.languagetool.service;


import com.changhong.languagetool.mapper.LanguageMapper;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ReadExcel {

   final LanguageMapper languageMapper;

    @Autowired
    public ReadExcel(LanguageMapper languageMapper) {
        this.languageMapper = languageMapper;
    }


    public void readExcelFile(MultipartFile file) throws  Exception {


        if (file == null) {
            return ;
        }
        String fileName = file.getOriginalFilename();
        Workbook workbook = null;
        System.out.println(fileName);
        try {
            //判断什么类型文件
            if (fileName.endsWith(".xls")) {
                workbook = new HSSFWorkbook(file.getInputStream());
            } else if (fileName.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(file.getInputStream());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ;
        }
        List<Integer> bugLine = new ArrayList<>();
        if (workbook == null) {
            return ;
        } else {
            //获取所有的工作表的的数量
            int numOfSheet = workbook.getNumberOfSheets();
//            System.out.println(numOfSheet+"--->numOfSheet");
            //遍历这个表
            for (int i = 0; i < numOfSheet; i++) {
                //获取一个sheet也就是一个工作本。
                Sheet sheet = workbook.getSheetAt(i);
                if(sheet == null) continue;

                int lastRowNum = sheet.getLastRowNum();
                if(lastRowNum == 0) continue;
                //第0行是国家语言 第一行开始是单词 先把国家的语言给存起来 然后再组合
                System.out.println("lastRowNum -->" +lastRowNum);

                //获取数据库中的列，比较有没有新加的，有的化同步到数据库
                List<String>  country = getCountryFromsheet(sheet);
                country.forEach(System.out::println);
                List<String> dbCountry = languageMapper.selectCountryName();
                WriterColumnToDDB(country,dbCountry);

                //在从数据库拿数据，可能有新的国家语言
                dbCountry = languageMapper.selectCountryName();

                //把 id 字段移除,
                Row row ;
                dbCountry.remove(0);
                for (int j  = 1; j <= lastRowNum; j++) {
                     row = sheet.getRow(j);
                     if(row == null) {
                         continue;
                     }


                    String sqlDate = " insert into languages (";
                    String sqlValue = " value (";

                    for (int k = 0 ;k < dbCountry.size() - 1 ; k++ ){
                        sqlDate = sqlDate + dbCountry.get(k) + ",";
                    }
                    sqlDate = sqlDate + dbCountry.get(dbCountry.size() - 1) + ")";

                    short lastCellNum = row.getLastCellNum();
                   // System.out.println(lastCellNum);
                    for (int k = 0; k <= lastCellNum; k++) {

                        if(row.getCell(k)==null) {
                       //     System.out.println("LastCellNum--->" + lastCellNum);
                      //      System.out.println("k--->"+k);
                            if(k == (lastCellNum-1) ){
                                if(++k<country.size()){
                                    sqlValue += ",";
                                }
                                while(k++<country.size()){
                                    sqlValue = sqlValue + "\"?\" ," ;
                                }
                                if(sqlValue.endsWith(",")){
                                    sqlValue = sqlValue.substring(0,sqlValue.length()-1);
                                }
                                sqlValue = sqlValue  + ");";
                            }else {
                                sqlValue = sqlValue + "\"?\" ," ;
                            }
                            continue;
                        }
                        row.getCell(k).setCellType(Cell.CELL_TYPE_STRING);
                        String res = row.getCell(k).getStringCellValue().trim();
                        res = checkRes(res);
                        if(res.equals("")){
                            sqlValue = sqlValue + "\"?\"" ;
                        }else {
                            sqlValue = sqlValue +"\"" + res + "\"";
                        }
                        if(k == (lastCellNum-1) ){
                            if(++k<country.size()){
                                sqlValue += ",";
                            }
                            while(k<country.size()){
                                sqlValue = sqlValue + "\"?\" ," ;
                                k++;
                            }
                            if(sqlValue.endsWith(",")){
                                sqlValue = sqlValue.substring(0,sqlValue.length()-1);
                            }

                            sqlValue = sqlValue  + ");";
                        }else {
                            sqlValue = sqlValue  + ",";
                        }
                    }
                    try {
                        languageMapper.addOneLineData(sqlDate+sqlValue);
                       // System.out.println("第"+j+"行的数据导入成功" + sqlDate+sqlValue);
                    }catch (Exception e){
                        bugLine.add(j);
                        System.out.println("第"+(1+j)+"行的数据导入失败" + sqlDate+sqlValue);
                        e.printStackTrace();
                    }

                   // System.out.println();

                }
            }
            bugLine.forEach(System.out::println);


            return ;
        }
    }

    public String  checkRes(String res) {
        //res 最后可能带有"," 或者"\"" 把他干掉
        if(res.endsWith(",")) {
            res = res.substring(0,res.length()-1);
        }
        //如果是开头和前后是""括起来的 干就完事了
        res = res.replace("\"","^^^");
        res = res.replace("\\","");
        return  res;
    }

    public void WriterColumnToDDB(List<String> country, List<String> dbCountry,String type) {
        for(String addCountryLanguage : country){
            //数据库没有对应的语言字段，需要新增加
            if(!dbCountry.contains(addCountryLanguage)){
                languageMapper.addCountryLanguageColumn(addCountryLanguage);
                //todo 把type列的数据拷贝到新创建的addCountryLanguage列
                Map<String ,String > map = new HashMap<>();
                map.put("older",type.toLowerCase());
                map.put("newer",addCountryLanguage);
                languageMapper.copyOlderToNew(map);
            }
        }


    }

    public void WriterColumnToDDB(List<String> country, List<String> dbCountry) {
        for(String addCountryLanguage : country){
            //数据库没有对应的语言字段，需要新增加
            if(!dbCountry.contains(addCountryLanguage)){
                //System.out.println(addCountryLanguage);
                languageMapper.addCountryLanguageColumn(addCountryLanguage);
            }
        }


    }

    public List<String> getCountryFromsheet(Sheet sheet) {
        List<String > country = new ArrayList<>();
        Row row = sheet.getRow(0);
        if(row == null) return  null;
        for (int k = 0; k < row.getLastCellNum(); k++) {
            if(row.getCell(k) == null){
                //country.add("");
                continue;
            }
            row.getCell(k).setCellType(Cell.CELL_TYPE_STRING);
            String res = row.getCell(k).getStringCellValue().toLowerCase().trim();
            //System.out.println(res);
            if(res.equals("")) continue;
            country.add(res);
        }
        return  country;
    }
}
