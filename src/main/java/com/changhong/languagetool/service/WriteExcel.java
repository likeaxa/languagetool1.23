package com.changhong.languagetool.service;


import com.changhong.languagetool.mapper.LanguageMapper;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class WriteExcel {

    private final LanguageMapper languageMapper;

    private final ReadExcel readExcel;

    private List<String> countrys;

    @Autowired
    public WriteExcel(LanguageMapper languageMapper, ReadExcel readExcel) {
        this.languageMapper = languageMapper;
        this.readExcel = readExcel;
    }

    public Boolean writeExcelFromDB(String fileName) throws IOException {

        Workbook wb = getWorkbookByFileName();
        FileOutputStream out = new FileOutputStream(new File(fileName));
        wb.write(out);
        out.close();
        wb.close();
        return true;
    }

    private Workbook getWorkbookByFileName() {
        String sqlCloumn = getSqlCloumn();
        List<Map<String, String>> allData = languageMapper.selectAllLanguage(sqlCloumn);
        List<String> allLanguageName = languageMapper.selectCountryName();
        Workbook wb = new SXSSFWorkbook(100);
        Sheet sheet = wb.createSheet("sheet");

        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setFillPattern(HSSFCellStyle.BIG_SPOTS);
        cellStyle.setFillForegroundColor(IndexedColors.BRIGHT_GREEN.getIndex());

        Row row = sheet.createRow(0);
        int j = 0;
        for (String oneLanguage : allLanguageName) {
            if (oneLanguage.equalsIgnoreCase("id")) continue;
            Cell cell = row.createCell(j++);
            oneLanguage = firstCharToCapture(oneLanguage);
            cell.setCellValue(oneLanguage);
        }

        j = 1;//第j行
        for (Map<String, String> lineData : allData) {
            row = sheet.createRow(j++);
            int k = 0; // 第k列
            for (String oneLanguage : allLanguageName) {
                if (oneLanguage.equalsIgnoreCase("id")) continue;
                String cellLanguage = lineData.get(oneLanguage);
                Cell cell = row.createCell(k++);
                if (null == cellLanguage || cellLanguage.equalsIgnoreCase("?") || cellLanguage.equalsIgnoreCase("")) {
                    //cellLanguage = "";
                    continue;
                }
                //一词多翻译，变绿色
//                if(cellLanguage.contains("###")){
//                    cellLanguage = cellLanguage.replace("###","/");
//                    cell.setCellStyle(cellStyle);
//                }
                cellLanguage = cellLanguage.replace("^^^", "\"");
                cell.setCellValue(cellLanguage);
                //  System.out.print(cell +" " );
            }
            // System.out.println();
        }
        return wb;
    }

    public Boolean writeExcelFromDB(OutputStream os) {

        try {
            Workbook wb = getWorkbookByFileName();
            wb.write(os);
            wb.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }


    }

    public String firstCharToCapture(String oneLanguage) {
        //name = name.substring(0, 1).toUpperCase() + name.substring(1);
        //return   name
        char[] cs = oneLanguage.toCharArray();
        cs[0] -= 32;
        return String.valueOf(cs);
    }


    public String getSqlCloumn() {
        // String res = "";

//        for (int i = 0; i < strings.size(); i++) {
//            String s = strings.get(i);
//            if (s.equals("id")) {
//                continue;
//            }
//            if (i == strings.size() - 1) {
//                res += s;
//                break;
//            }
//            res += s + ",";
//        }
        List<String> strings = languageMapper.selectCountryName();
        String res = strings.stream()
                .filter(s -> !s.equalsIgnoreCase("id"))
                .map(s -> s + ",")
                .reduce("", (s1, s2) -> s1 + s2);
        if (res.length() == 0) return "";
        return res.substring(0, res.length() - 1);
        // System.out.println(res);

        //return res;
    }

    public void updateExceptFile(InputStream is, OutputStream os)throws  IOException {

        List<Map<String, String>> tranData = GetExceptData(is);

        Workbook wb= getExceptFileFromData(tranData);

        wb.write(os);
        wb.close();
        is.close();

    }

    private Workbook getExceptFileFromData(List<Map<String, String>> tranData) {


        List<Map<String, String>> allData = tranData;
        List<String> allLanguageName = countrys;
        Workbook wb = new SXSSFWorkbook(100);
        Sheet sheet = wb.createSheet("sheet");
        CellStyle cellStyle = wb.createCellStyle();
        cellStyle.setFillPattern(HSSFCellStyle.BIG_SPOTS);
        cellStyle.setFillForegroundColor(IndexedColors.RED.getIndex() );
        Row row = sheet.createRow(0);
        int j = 0;
        for (String oneLanguage : allLanguageName) {
            Cell cell = row.createCell(j++);
            cell.setCellValue(oneLanguage);
        }
        j = 1;//第j行
        for (Map<String, String> lineData : allData) {
            row = sheet.createRow(j++);
            int k = 0; // 第k列
            for (String oneLanguage : allLanguageName) {
                String cellLanguage = lineData.get(oneLanguage);
                Cell cell = row.createCell(k++);
               if(cellLanguage.equalsIgnoreCase("")){
                   cell.setCellValue("");
               }else {
                   cellLanguage = cellLanguage.replace("^^^","\"");
                   cell.setCellValue(cellLanguage);
                   if(k!=1){
                       cell.setCellStyle(cellStyle);
                   }

               }

            }
        }
        return  wb ;
    }

    private List<Map<String, String>> GetExceptData(InputStream is) throws IOException {
        List<Map<String, String>> result = new ArrayList<>();

        Workbook workbook = new XSSFWorkbook(is);

        //获取所有的工作表的的数量
        int numOfSheet = workbook.getNumberOfSheets();
//            System.out.println(numOfSheet+"--->numOfSheet");
        //遍历这个表
        for (int i = 0; i < numOfSheet; i++) {
            Sheet sheet = workbook.getSheetAt(i);
            if (sheet == null) continue;
            int lastRowNum = sheet.getLastRowNum();
            countrys = readExcel.getCountryFromsheet(sheet);

            //遍历行
            for (int j = 1; j <= lastRowNum; j++) {
                Row row = sheet.getRow(j);
                Map<String, String> res = new HashMap<>();
                String index = "";
                // 遍历列 第0列是english 所以从第1列开始
                for (int k = 0; k < countrys.size(); k++) {
                    String country = countrys.get(k);
                    Cell cell = row.getCell(k);
                    if (null == country || country.equals("")) {
                        continue;
                    }
                    if (cell == null) {
                        res.put(country, "");
                    } else {
                        row.getCell(k).setCellType(Cell.CELL_TYPE_STRING);
                        String temp = row.getCell(k).getStringCellValue().trim();
                        temp = readExcel.checkRes(temp);
                        if (k == 0) {
                            index = temp;
                            res.put(country,index);
                            continue;
                        }
                        if (temp.equalsIgnoreCase("")) {
                            //没有数据库，直接加
                            res.put(country, "");
                        } else {
                            //  这一列有数据 比较数据库
                            getDataCompareDB(country, index, temp, res);
                        }

                    }
                }
                result.add(res);

            }


        }
        return result;
    }

    private void getDataCompareDB(String country, String index, String temp, Map<String, String> res) {

        Map<String, String> selectData = new HashMap<>();
        selectData.put("column", country);
        selectData.put("index", index);
        Map<String, String> selectRes = languageMapper.selectOtherLanguageByEnglish(selectData);

        if (selectRes != null) {
            String valueDB = selectRes.get("value");
            if (valueDB != null && !valueDB.equals(temp)) {
                res.put(country,"提交 : " + temp + "  词条库: " + valueDB);
            }else{
                res.put(country,"");
            }
        }
    }
}
