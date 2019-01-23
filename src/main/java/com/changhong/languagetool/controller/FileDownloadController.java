package com.changhong.languagetool.controller;

import com.changhong.languagetool.service.WriteExcel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;


@Controller
public class FileDownloadController {

    final
    WriteExcel writeExcel;



    @Autowired
    public FileDownloadController(WriteExcel writeExcel) {
        this.writeExcel = writeExcel;
    }

    //下载到本地文件出现部分内容丢失
//    @RequestMapping("/download")
//    public String downLoad(HttpServletResponse response) throws UnsupportedEncodingException {
//        String filename="2.xlsx";
//        String filePath = "D:/download" ;
//        File file = new File(filePath + "/" + filename);
//        if(file.exists()){ //判断文件父目录是否存在
//            response.setContentType("application/vnd.ms-excel;charset=UTF-8");
//            response.setCharacterEncoding("UTF-8");
//           // response.setContentType("application/force-download");
//            response.setHeader("Content-Disposition", "attachment;fileName=" +   java.net.URLEncoder.encode(filename,"UTF-8"));
//            byte[] buffer = new byte[1024];
//            FileInputStream fis = null; //文件输入流
//            BufferedInputStream bis = null;
//
//            OutputStream os = null; //输出流
//            try {
//                os = response.getOutputStream();
//                fis = new FileInputStream(file);
//                bis = new BufferedInputStream(fis);
//                int i = bis.read(buffer);
//                while(i != -1){
//                    os.write(buffer);
//                    i = bis.read(buffer);
//                }
//
//            } catch (Exception e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//            System.out.println("----------file download---" + filename);
//            try {
//                bis.close();
//                fis.close();
//            } catch (IOException e) {
//                // TODO Auto-generated catch block
//                e.printStackTrace();
//            }
//        }
//        return null;
//    }

    //下载到本地文件出现部分内容丢失,采用别的方法，直接把数据库读取出来的数据解析完成放到response的流里面,不通过文件交流
    @RequestMapping("/download2")
    public String downLoadVasion2(HttpServletResponse response) throws UnsupportedEncodingException {
        String filename="dataBaseAllWord.xlsx";
        response.setContentType("application/vnd.ms-excel;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        // response.setContentType("application/force-download");
        response.setHeader("Content-Disposition", "attachment;fileName=" +   java.net.URLEncoder.encode(filename,"UTF-8"));
        OutputStream os = null; //输出流
            try {
                os = response.getOutputStream();
               writeExcel.writeExcelFromDB(os);

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            System.out.println("----------file download---" + filename);
        return null;
    }
    @PostMapping("/getExceptFile")
    public void downloadExceptFile(@RequestParam("fileUpdate") MultipartFile file ,
                                   HttpServletResponse response) throws IOException{
        String filename="exceptFile.xlsx";
        response.setContentType("application/vnd.ms-excel;charset=UTF-8");
        response.setCharacterEncoding("UTF-8");
        // response.setContentType("application/force-download");
        response.setHeader("Content-Disposition", "attachment;fileName=" +   java.net.URLEncoder.encode(filename,"UTF-8"));

        try {

            InputStream is = file.getInputStream();
            OutputStream os =  response.getOutputStream(); //输出流

            writeExcel.updateExceptFile(is , os);

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

}
