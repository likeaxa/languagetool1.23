package com.changhong.languagetool.service;


import com.changhong.languagetool.mapper.LanguageMapper;
import com.changhong.languagetool.utils.ResultMessage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UpdateFromExcelFile {

    private final ReadExcel readExcel;
    private final LanguageMapper languageMapper;

    @Autowired
    public UpdateFromExcelFile(ReadExcel readExcel, LanguageMapper languageMapper) {
        this.readExcel = readExcel;
        this.languageMapper = languageMapper;
    }

    public void updateDBFromFile(InputStream inputStream, String filename,String type) throws IOException {
        Workbook workbook = new XSSFWorkbook(inputStream);

        //获取所有的工作表的的数量
        int numOfSheet = workbook.getNumberOfSheets();
//            System.out.println(numOfSheet+"--->numOfSheet");
        //遍历这个表
        for (int i = 0; i < numOfSheet; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            if (sheet == null) continue;

            int lastRowNum = sheet.getLastRowNum();
            List<String> countrys = readExcel.getCountryFromsheet(sheet);
            List<String> dbCountry = languageMapper.selectCountryName();
            readExcel.WriterColumnToDDB(countrys, dbCountry,type);

            //在从数据库拿数据，可能有新的国家语言
            dbCountry = languageMapper.selectCountryName();

            //把 id 字段移除,
            dbCountry.remove(0);
            //遍历行
            for (int j = 1; j <= lastRowNum; j++) {
                Row row = sheet.getRow(j);
                String index = "";
                // 遍历列 第0列是english 所以从第1列开始
                for (int k = 0; k < countrys.size(); k++) {
                    String country = countrys.get(k);
                    Cell cell = row.getCell(k);
                    if (null == country || country.equals("") || cell == null) {
                        continue;
                    } else {
                        row.getCell(k).setCellType(Cell.CELL_TYPE_STRING);
                        String temp = row.getCell(k).getStringCellValue().trim();
                        temp = readExcel.checkRes(temp);
                        if (k == 0) {
                            index = temp;
                        }
                        if (!temp.equalsIgnoreCase("")) {
                            //  这一列有数据 保留
                            updateDataToDB(index, country, temp,type);
                        }

                    }
                }

            }
        }

    }
    private void updateDataToDB(String index, String country, String temp,String  type) {
        /**
         * 1判断是不是english国家，如果是英语国家，就判断当前的词条是不是存在，如果存在不理它，如果不存在，就创建
         * 2 如果不是english国家，就把目标国家的内容根据English=index修改成新的内容。
         */
        Map<String, String> data = new HashMap<>();
        //index = readExcel.checkRes(index);
        data.put("column", country);
        data.put("index", index);
        if(country.equalsIgnoreCase("english")) {
            // System.out.println(country+"--->"+index);
            Map<String, String> selectRes = languageMapper.selectOtherLanguageByEnglish(data);
            if (selectRes == null) {
                //创建词条，如果词条不存在
                languageMapper.insertConlumnFromEnglish(index);
            }
        }else {
            if(type.equals("检查覆盖")){
                Map<String, String> selectRes = languageMapper.selectOtherLanguageByEnglish(data);
                if (selectRes == null||selectRes.get("value").equalsIgnoreCase("?")) {
                    data.put("value",temp);
                    languageMapper.updateColumn(data);
                }else {
                   // String s = readExcel.checkRes(temp);
                    String  s = temp;
                    if(!s.equals(selectRes.get("value"))){
                        //Todo 记录下来
                        //System.out.println(s+":::"+selectRes.get("value"));
                        String message = index +"-->" +country+": 在词条库存在" +"提交数据 ："+s+"数据库数据："+selectRes.get("value");
                        ResultMessage.res.add(message);
                    }
                }
            }else if(type.equals("覆盖")){
                data.put("value",temp);
                languageMapper.updateColumn(data);
            }else {//新增的一列的数据给弄进数据库
                data.put("value",temp);
                languageMapper.updateColumn(data);
            }


        }
    }
}