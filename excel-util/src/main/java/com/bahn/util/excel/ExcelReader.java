package com.bahn.util.excel;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelReader<T> {
	
	protected int headerRow = 0;
	protected int dataRow = 1;
	
	private File file = null;
	private String sheetName = null;
	private Sheet sheet = null;
	private Class clazz = null;
	private Map<Integer,Field> columnFieldMap = new HashMap<Integer,Field>();
	private Map<Integer,String> columnNameMap = new HashMap<Integer,String>();
	private Map<Integer,String> cellTypeMap = new HashMap<Integer,String>();
//	private Map<>
	
	public ExcelReader(String file, String sheet) {
		this(new File(file),sheet);
		
	}
	
	public ExcelReader(File file, String sheet){
		this.sheetName = sheet; 
		this.file = file;
		
		this.clazz = getBeanClass();
		
		Field[] fields = clazz.getDeclaredFields();
		
		for (Field f : fields){
			ExcelColumnBinding a = f.getDeclaredAnnotation(ExcelColumnBinding.class);
			columnFieldMap.put(a.columnPos(), f);
			columnNameMap.put(a.columnPos(), a.columnName());
		}
		
		prepareCellTypeMap();
	}
	
	private void prepareCellTypeMap(){
		this.cellTypeMap.put(Cell.CELL_TYPE_BLANK, "Blank");
		this.cellTypeMap.put(Cell.CELL_TYPE_BOOLEAN, "Boolean");
		this.cellTypeMap.put(Cell.CELL_TYPE_ERROR, "Error");
		this.cellTypeMap.put(Cell.CELL_TYPE_FORMULA, "Formula");
		this.cellTypeMap.put(Cell.CELL_TYPE_NUMERIC, "Number");
		this.cellTypeMap.put(Cell.CELL_TYPE_STRING, "String");
		
	}
	
	private boolean checkFile() throws Exception{
		
		Workbook wb = new XSSFWorkbook (new FileInputStream(file));

		sheet = wb.getSheet(sheetName);
		
		if (sheet == null){
			throw new Exception ("Excel Sheet: " + sheetName + " Could not be found.");
		}
		
		Row row = this.sheet.getRow(headerRow);
		
		if (row == null){
			throw new Exception ("Excel Sheet: " + sheetName + " Could not be found.");
		}
		
		
		for (int i : columnNameMap.keySet()){
			String f = columnNameMap.get(i);
			if ( !f.equals(row.getCell(i).toString())){
				throw new Exception ("Column Mismatch @Col" + i + "\nExpected column name: " + f + ", got: " + row.getCell(i));
			}
		}
		
		return true;
	}
	
	private Object getCell(Field f, Cell cell) throws Exception{
		
		f.setAccessible(true);
		
		if (cell == null){
			return null;
		}
		int cellType = cell.getCellType();
		Class fClass = f.getType();
		
		if (fClass.equals(String.class) && cellType == Cell.CELL_TYPE_STRING){
			return cell.getStringCellValue();
		} else if (Number.class.isAssignableFrom(fClass) && cellType == Cell.CELL_TYPE_NUMERIC){
			Double d = cell.getNumericCellValue();
			if (fClass.equals(Integer.class)){
				return d.intValue();
			} else if (fClass.equals(Double.class)){
				return d;
			} else if (fClass.equals(Long.class)){
				return d.longValue();
			}
		} else if (fClass.equals(Date.class) && cellType == Cell.CELL_TYPE_NUMERIC){
			return cell.getDateCellValue();
		}
		else if (fClass.equals(Boolean.class) && cellType == Cell.CELL_TYPE_BOOLEAN){
			return cell.getBooleanCellValue();
		} else if (cellType == Cell.CELL_TYPE_BLANK){
			return null;
		}
		
		throw new Exception("Invalid Cell Format at row " + cell.getRowIndex() + " column " + cell.getColumnIndex() 
		+ ":\nExpected " + fClass.getSimpleName() + ", got " + cellTypeMap.get(cellType));
		
	}
		
	
	public List<T> readAsList() throws Exception{
		
		
		
		
		checkFile();
		
		
		List<T> beanList = new ArrayList<T>();
		
		for (int row = dataRow; row < sheet.getPhysicalNumberOfRows() ; row++ ){
			Row r = sheet.getRow(row);
			T bean = (T)clazz.newInstance();
			for (int col : columnFieldMap.keySet()){
				Field f = columnFieldMap.get(col);
				f.setAccessible(true);
				Cell cell = r.getCell(col);
				
				f.set(bean, getCell(f , cell));
				
			}
			
			beanList.add(bean);
		}

		return beanList;
	}
	
	private Object getCellValue(Cell cell) throws Exception{
		switch (cell.getCellType()){
		case Cell.CELL_TYPE_STRING:
			return cell.getStringCellValue();
		case Cell.CELL_TYPE_BLANK:
			return null;
		case Cell.CELL_TYPE_BOOLEAN:
			return cell.getBooleanCellValue();
		case Cell.CELL_TYPE_NUMERIC:
			return cell.getNumericCellValue();
		}
		
		throw new Exception("Invalid Cell at row " + cell.getRowIndex() + " column " + cell.getColumnIndex());
	}
	
	private Class getBeanClass(){
		return (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
	}
	

}
