package com.zzc.CITO.controller;

import java.io.File;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.zzc.CITO.manager.Manager;
import com.zzc.CITO.util.unZip;

@Controller
public class homeController {

	@Autowired
	private unZip unzip;
	@Autowired
	private Manager manager;
	
	private String fileName="";
	
	@RequestMapping("/enter")
	public String enter() {
		return "display.html";
	}
	
	@PostMapping("fileUpload")
	@ResponseBody
	public void fileUpload(@RequestParam("file")MultipartFile file){	
		String path="";
		fileName="";
		if (file.isEmpty()) {			
            return ;
        }else{
        	fileName = file.getOriginalFilename();
        	String filepath = "F:\\CITO\\CITOProject\\input_analysis";
        	 File localFile = new File(filepath);
             if(!localFile .exists()) {
                 localFile.mkdirs();
            }
            path = filepath+fileName;
            try {
            	File server_file = new File(path);
				file.transferTo(server_file);
				
			} catch (Exception e) {
				System.out.println("上传错误");
			}
            System.out.println(path);
    		unzip.setDir(path, filepath);
    		try {
				unzip.upZipFile();
			} catch (Exception e) {
				System.out.println("解压错误");
			}
    		return ;
        }
	}

	@RequestMapping("staticPic")
	@ResponseBody
	public ModelAndView getPic(@RequestParam("Name") String sName) throws Exception {
		
		System.out.println("filename==============="+sName);
		manager.staticAnalysis(sName);
		String JsonC=JSON.toJSONString(manager.getClasses());
		String JsonE=JSON.toJSONString(manager.getEdges());
		ModelAndView mv=new ModelAndView();
		mv.addObject("Classes", manager.getClasses());
		mv.addObject("Edges", manager.getEdges());
		mv.setViewName("staticAnalysis.html");
		mv.addObject("RL", manager.runBy(1));
		mv.addObject("BCN", manager.runBy(2));
		mv.addObject("Info", manager.getInfo());
		return mv;
	}	
	
}
