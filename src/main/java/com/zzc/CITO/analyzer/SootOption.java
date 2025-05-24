package com.zzc.CITO.analyzer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import soot.Scene;
import soot.SootClass;
import soot.options.Options;

public class SootOption {
	public static Collection<SootClass> setOptions(String fileName){
		List<String> processdir= new ArrayList<String>();
		processdir.add(System.getProperty("user.dir")+"\\cito_modified"+"\\input_analysis_1b\\"+fileName);//获取文件地址  当前程序所在目录
		soot.G.reset();
		Options.v().set_app(true);

		Options.v().set_whole_program(true);

		Options.v().set_keep_line_number(true);

		Options.v().set_allow_phantom_refs(true);

		Options.v().set_process_dir(processdir);

		Options.v().setPhaseOption("jb", "enabled:true");

		Options.v().setPhaseOption("jb", "use-original-names:true");
		Options.v().set_no_bodies_for_excluded(true);
		Options.v().set_soot_classpath(System.getProperty("java.class.path"));
		System.out.println(processdir);		//�����ز����������
		Scene.v().loadNecessaryClasses();
		//Scene.v().loadBasicClasses();
		Collection<SootClass> coll=Scene.v().getApplicationClasses();// ��ȡ���еĴ�����
        
        return  coll;
    }
}

