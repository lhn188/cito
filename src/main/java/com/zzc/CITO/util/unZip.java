package com.zzc.CITO.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.springframework.stereotype.Repository;

@Repository
public class unZip {
	
	private static String ZIP_FILENAME = "F:\\CITO\\CITOProject\\input_analysis";//需要解压缩的文件名
    private static String UN_ZIP_DIR = "F:\\CITO\\CITOProject\\input_analysis\\";//要解压的文件目录
    private static final int BUFFER = 1024 ;//缓存大小  
    
    public static void setDir(String fileName,String unZipdir) {
    	ZIP_FILENAME=fileName;
    	UN_ZIP_DIR=unZipdir;
    }
      
    /** 
    * 解压缩功能. 
    * 将ZIP_FILENAME文件解压到ZIP_DIR目录下. 
    * @throws Exception 
    */  
    public static void upZipFile() throws Exception{  
        ZipFile zfile=new ZipFile(ZIP_FILENAME);  
        Enumeration zList=zfile.entries();  
        ZipEntry ze=null;  
        byte[] buf=new byte[1024];  
        while(zList.hasMoreElements()){  
            ze=(ZipEntry)zList.nextElement();         
            if(ze.isDirectory()){  
                File f=new File(UN_ZIP_DIR+ze.getName());  
                f.mkdir();  
                continue;  
            }  
            OutputStream os=new BufferedOutputStream(new FileOutputStream(getRealFileName(UN_ZIP_DIR, ze.getName())));  
            InputStream is=new BufferedInputStream(zfile.getInputStream(ze));  
            int readLen=0;  
            while ((readLen=is.read(buf, 0, 1024))!=-1) {  
                os.write(buf, 0, readLen);  
            }  
            is.close();  
            os.close();   
        }  
        zfile.close();  
    }  
  
    /** 
    * 给定根目录，返回一个相对路径所对应的实际文件名. 
    * @param baseDir 指定根目录 
    * @param absFileName 相对路径名，来自于ZipEntry中的name 
    * @return java.io.File 实际的文件 
    */  
    public static File getRealFileName(String baseDir, String absFileName){  
        String[] dirs=absFileName.split("/");  
        File ret=new File(baseDir);  
        if(dirs.length>1){  
            for (int i = 0; i < dirs.length-1;i++) {  
                ret=new File(ret, dirs[i]);  
            }  
            if(!ret.exists())  
                ret.mkdirs();  
            ret=new File(ret, dirs[dirs.length-1]);  
            return ret;  
        }  
        return ret;  
    }  
  
}  