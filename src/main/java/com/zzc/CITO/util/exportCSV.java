package com.zzc.CITO.util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CountDownLatch;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import com.zzc.CITO.Base.TEdge;

public class exportCSV implements Runnable {
	
	 private String filename;
     private Set<TEdge> list;
     private CountDownLatch countDownLatch;

     public exportCSV(String filename, Set<TEdge> list) {
         this.filename = filename;
         this.list = list;
     }

	@Override
	public void run() {
		try {
            CSVPrinter printer = new CSVPrinter(new FileWriter(filename), CSVFormat.EXCEL.withHeader("FROM", "TO","type"));
            for (TEdge edge : list) {
                printer.printRecord(edge.getFromIndex(),edge.getToIndex(),edge.getRtype());
            }
            printer.close();
            //countDownLatch.countDown();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

}
