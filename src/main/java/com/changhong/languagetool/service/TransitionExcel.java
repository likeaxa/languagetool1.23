package com.changhong.languagetool.service;


import com.changhong.languagetool.mapper.LanguageMapper;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.ws.Action;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TransitionExcel {


    final
    ReadExcel readExcel ;
    final
    WriteExcel writeExcel;
    final
    LanguageMapper languageMapper;
    @Autowired
    public TransitionExcel(ReadExcel readExcel, WriteExcel writeExcel, LanguageMapper languageMapper) {
        this.readExcel = readExcel;
        this.writeExcel = writeExcel;
        this.languageMapper = languageMapper;
    }
    private List<String> countrys ;
    public void transitionFile(InputStream is, OutputStream os) throws IOException{

        List<Map<String ,String >>  tranData = tranDataByInputStream(is);
//test
//        for(Map<String,String > aa : tranData){
//            System.out.print(aa.get("english"));
//            System.out.print(aa.get("slovene"));
//            System.out.print(aa.get("arabic"));
//            System.out.println();
//        }
        Workbook wb = getTranFile(tranData);
        wb.write(os);
        wb.close();
        is.close();

    }

    private Workbook getTranFile(List<Map<String, String>> tranData) {

        List<Map<String, String>> allData = tranData;
        List<String> allLanguageName = countrys;
        Workbook wb = new SXSSFWorkbook(100);
        Sheet sheet = wb.createSheet("sheet");

        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setFillPattern(HSSFCellStyle.BIG_SPOTS);
        cellStyle.setFillForegroundColor(IndexedColors.BRIGHT_GREEN.getIndex());
        Row row = sheet.createRow(0);
        int j = 0;
        for (String oneLanguage : allLanguageName) {
            Cell cell = row.createCell(j++);
            //oneLanguage = writeExcel.firstCharToCapture(oneLanguage);
            cell.setCellValue(oneLanguage);
        }
        j = 1;//第j行
        for (Map<String, String> lineData : allData) {
            row = sheet.createRow(j++);
            int k = 0; // 第k列
            for (String oneLanguage : allLanguageName) {
                String cellLanguage = lineData.get(oneLanguage);
                Cell cell = row.createCell(k++);
                if(cellLanguage.equalsIgnoreCase("?")){
                    cellLanguage = "";
                }

                cellLanguage = cellLanguage.replace("^^^","\"");
                cell.setCellValue(cellLanguage);
                //  System.out.print(cell +" " );
            }
            // System.out.println();
        }
        return  wb ;
    }

    private List<Map<String, String>> tranDataByInputStream(InputStream is) throws IOException{

        List<Map<String, String>> result = new ArrayList<>();

        Workbook workbook =new XSSFWorkbook(is);

        //获取所有的工作表的的数量
        int numOfSheet = workbook.getNumberOfSheets();
//            System.out.println(numOfSheet+"--->numOfSheet");
        //遍历这个表
        for (int i = 0; i < numOfSheet; i++){
            Sheet sheet = workbook.getSheetAt(i);
            if(sheet == null) continue;

            int lastRowNum = sheet.getLastRowNum();
             countrys = readExcel.getCountryFromsheet(sheet);
            //遍历行
            for(int j = 1;j<=lastRowNum;j++){
                Row  row = sheet.getRow(j);
                Map<String ,String> res = new HashMap<>();
                String  index = "";
                // 遍历列 第0列是english 所以从第1列开始

                for(int k = 0 ; k < countrys.size(); k++){
                    String  country = countrys.get(k);
                    Cell cell = row.getCell(k);
                    if(null == country ||country.equals("")){
                        continue;
                    }
                    if(cell == null){
                        //最后几行的问题 需要查询数据库
                         getResultFromData(country,index,res);
                    }else {
                        row.getCell(k).setCellType(Cell.CELL_TYPE_STRING);
                        String temp = row.getCell(k).getStringCellValue().trim();
                        temp = readExcel.checkRes(temp);
                        if(k == 0) {
                            index = temp;
                        }
                        if(temp.equalsIgnoreCase("")){
                            //需要查询数据库 没有数据
                           getResultFromData(country,index,res);
                        }else {
                          //  这一列有数据 保留
                            res.put(country,temp);
                        }

                    }
                }
                result.add(res);

            }
        }
        return  result;
    }

    private void getResultFromData(String country, String index, Map<String, String> res) {
        Map<String,String> selectData = new HashMap<>();
        selectData.put("column",country);
        selectData.put("index",index);
        System.out.println(country+"--->"+index);
        Map<String, String> selectRes = languageMapper.selectOtherLanguageByEnglish(selectData);
        if(selectRes == null){
            res.put(country,"?");
            return;
        }
        String val = selectRes.get("value");
        if(null==val||val.equals("?")){
            res.put(country,"?");
        }else{
            res.put(country,val);
        }
    }


    public void test(InputStream is, OutputStream equalsOS, OutputStream disequalsOS) throws  IOException {

        Workbook workbook =new XSSFWorkbook(is);
        Workbook equalsOSwb = new SXSSFWorkbook(100);
        Sheet equalsSheet = equalsOSwb.createSheet("sheet");

        Workbook disequalsOSwb = new SXSSFWorkbook(100);
        Sheet disequalsSheet = disequalsOSwb.createSheet("sheet");
        //获取所有的工作表的的数量
        int numOfSheet = workbook.getNumberOfSheets();


        Sheet  isSheet = workbook.getSheetAt(0);

        Row isRow = isSheet.getRow(0);


        copyRowTORow(equalsSheet.createRow(0),isRow);
        copyRowTORow(disequalsSheet.createRow(0),isRow);
        int isSheetRow = isSheet.getLastRowNum();
        int equFlag = 1;
        int disequFlag =1;
        for(int i = 1 ; i <= isSheetRow ; i++){
            boolean flag = false;
            isRow  = isSheet.getRow(i);
            if(isRow==null) continue;
            for(int j = 1;j <= isSheetRow;j++){
                if(i==j) continue;
                Row temp = isSheet.getRow(j);
                if(temp==null) continue;
                Cell isCell = isRow.getCell(0);
                Cell tempCell = temp.getCell(0);
                if(isCell==null) continue;
                if(tempCell==null) continue;
                isCell.setCellType(Cell.CELL_TYPE_STRING);
                tempCell.setCellType(Cell.CELL_TYPE_STRING);
                String index = isCell.getStringCellValue().trim();
                String tempStr = tempCell.getStringCellValue().trim();
                if(index.equals(tempStr)){
                    copyRowTORow(equalsSheet.createRow(equFlag++),isRow);
                    System.out.println(i+1+"行有重复的");
                    flag=true;
                    break;
                }
            }
            if(flag) continue;
            copyRowTORow( disequalsSheet.createRow(disequFlag++),isRow);
        }

        System.out.println("扫描结束");
        equalsOSwb.write(equalsOS);
        disequalsOSwb.write(disequalsOS);

        equalsOSwb.close();
        disequalsOSwb.close();

    }

    private void copyRowTORow(Row newRow, Row source) {

        short lastCellNum = source.getLastCellNum();
        for(int i = 0 ; i<=lastCellNum;i++){
            Cell cell = source.getCell(i);
            if(cell==null) continue;
            Cell sourceCell = newRow.createCell(i);
            cell.setCellType(Cell.CELL_TYPE_STRING);
            sourceCell.setCellType(Cell.CELL_TYPE_STRING);
            String tempStr = cell.getStringCellValue().trim();
           // System.out.println(tempStr);
            sourceCell.setCellValue(tempStr);
        }
    }
}


