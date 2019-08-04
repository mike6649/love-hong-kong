package com.bahn.excel;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import com.bahn.bean.PhotoBackup;

public class RnDCSVReader {
	
	private File file;
	private Iterable<CSVRecord> records;
	
	public RnDCSVReader(File file) throws IOException{
		this.file = file;
		FileReader in = new FileReader(file);
		records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(in);
	}
	
	public List<PhotoBackup> readAsList(){
		List<PhotoBackup> list = new ArrayList<PhotoBackup>();
		for (CSVRecord record : records) {
			PhotoBackup obj = new PhotoBackup();
			obj.time = record.get(0);
			obj.type = record.get(1);
			obj.urlList = new ArrayList<String>();
			for (int i=2; i<record.size();i++){
				if (record.get(i)!=null && !record.get(i).isEmpty())
					obj.urlList.add(record.get(i));
			}
			
			list.add(obj);
		}
		return list;
	}

	public static void main(String[] args) {
		try {
			RnDCSVReader reader = new RnDCSVReader (new File("C:\\Users\\michael\\Downloads\\backup-pics.csv"));
			List<PhotoBackup> list = reader.readAsList();
		} catch (Exception e){
			e.printStackTrace();
		}
	}

}
