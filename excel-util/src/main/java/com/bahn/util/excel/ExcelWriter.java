package com.bahn.util.excel;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelWriter<T> {
	protected int headerRow = 0;
	protected int dataRow = 1;

	private File file = null;
	private String sheetName = null;
	private Workbook wb = null;
	private Sheet sheet = null;
	private Class clazz = null;
	private Map<Field,Integer> columnFieldMap = new HashMap<Field,Integer>();
	private Map<Integer,String> columnNameMap = new HashMap<Integer,String>();
	private Map<Integer,String> cellTypeMap = new HashMap<Integer,String>();

	public ExcelWriter(String file, String sheet) {
		this(new File(file),sheet);

	}

	public ExcelWriter(File file, String sheet){
		this.sheetName = sheet; 
		this.file = file;

		this.clazz = getBeanClass();

		Field[] fields = clazz.getDeclaredFields();

		for (Field f : fields){
			ExcelColumnBinding a = f.getDeclaredAnnotation(ExcelColumnBinding.class);
			columnFieldMap.put(f, a.columnPos());
			columnNameMap.put(a.columnPos(), a.columnName());
		}

		prepareCellTypeMap();
	}

	private boolean checkFile() throws Exception {
		wb = new XSSFWorkbook ();
		sheet = wb.createSheet(sheetName);

		return true;
	}

	private void setCell(T object, Field f, Cell cell) throws Exception {
		Class fClass = f.getType();
		if (fClass.equals(String.class)){
			cell.setCellValue((String)f.get(object));
		} else if (fClass.equals(Boolean.class)){
			cell.setCellValue(f.getBoolean(object));
		} else if (fClass.equals(Date.class)){
			cell.setCellValue((Date)f.get(object));
			CellStyle style = wb.createCellStyle();
			style.setDataFormat((short)14);
			cell.setCellStyle(style);
		} else if (Number.class.isAssignableFrom(fClass)){
			Double d = ((Number)f.get(object)).doubleValue();
			cell.setCellValue(d);
		}
	}


	public void writeList(List<T> list) throws Exception {
		checkFile();
		Field[] fields = clazz.getFields();
		Row row = sheet.createRow(headerRow);
		int i=0;
		for (Field f : fields) {
			Cell cell = row.createCell(i++);
			cell.setCellValue(f.getName());
		}
		
		int rowNum = dataRow;
		int colNum;
		for (T object : list){
			row = sheet.createRow(rowNum);
			colNum = 0;
			for (Field f : fields) {
				Cell cell = row.createCell(colNum);
				setCell(object, f, cell);
				colNum++;
			}

			rowNum++;
		}
		try { 
			FileOutputStream out = new FileOutputStream(file); 
			wb.write(out);
			out.close(); 
		} 
		catch (Exception e) { 
			e.printStackTrace(); 
		} 

	}

	private void prepareCellTypeMap(){
		this.cellTypeMap.put(Cell.CELL_TYPE_BLANK, "Blank");
		this.cellTypeMap.put(Cell.CELL_TYPE_BOOLEAN, "Boolean");
		this.cellTypeMap.put(Cell.CELL_TYPE_ERROR, "Error");
		this.cellTypeMap.put(Cell.CELL_TYPE_FORMULA, "Formula");
		this.cellTypeMap.put(Cell.CELL_TYPE_NUMERIC, "Number");
		this.cellTypeMap.put(Cell.CELL_TYPE_STRING, "String");

	}

	private Class<?> getBeanClass(){
				return (Class<?>) ((ParameterizedType) getClass()
		                .getGenericSuperclass()).getActualTypeArguments()[0];



	}
}
