package com.bahn;

import java.io.File;

import com.bahn.bean.PhotoBackup;
import com.bahn.util.excel.ExcelReader;

public class BackupExcelReader extends ExcelReader<PhotoBackup> {

	public BackupExcelReader(File file, String sheet,boolean checkColumnNames) {
		super(file, sheet, checkColumnNames);
		// TODO Auto-generated constructor stub
	}

}
