package com.noknown.framework.common.util.excel;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.*;
import java.util.*;


/**
 * 对excel进行操作工具类
 **/
public class ExcelHandle {

	private Map<String, List<Map<String, Object>>> tempFileMap = new HashMap<>();
	private Map<String, Map<String, Cell>> cellMap = new HashMap<String, Map<String, Cell>>();
	private Map<String, FileInputStream> tempStream = new HashMap<String, FileInputStream>();
	private Map<String, Workbook> tempWorkbook = new HashMap<String, Workbook>();
	private Map<String, Workbook> dataWorkbook = new HashMap<String, Workbook>();

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws IOException {
		String tempFilePath = "/tmp/xlsTpl/订单详情.xlsx";
		ExcelHandle handle = new ExcelHandle();

		JSONObject dataMap = new JSONObject();

		JSONObject order = new JSONObject();

		order.put("name", "123");
		order.put("code", "123455");
		order.put("ctime", new Date());

		dataMap.put("order", order);
		JSONArray summary = new JSONArray();

		for (int i = 0; i < 5; i++) {
			JSONObject goods = new JSONObject();
			goods.put("paperType", "相纸" + i);
			goods.put("sizeType", (i + 5) + "寸");
			goods.put("total", 10 - i);
			summary.add(goods);
		}
		dataMap.put("summary", summary);


		handle.getTempWorkbook(tempFilePath);

		handle.writeData(tempFilePath, dataMap, 0);

		File file = new File("d:/tmp/data.xlsx");
		OutputStream os = new FileOutputStream(file);

		//写到输出流并关闭资源
		handle.writeAndClose(tempFilePath, os);

		os.flush();
		os.close();

        /*List<String> dataListCell = new ArrayList<String>();
        dataListCell.add("names");
        dataListCell.add("ages");
        dataListCell.add("sexs");
        dataListCell.add("deses");
        List<Map<String,Object>> dataList = new  ArrayList<Map<String,Object>>();
        Map<String,Object> map = new HashMap<String, Object>();
        map.put("names", "names");
        map.put("ages", 22);
        map.put("sexs", "男");
        map.put("deses", "测试");
        dataList.add(map);
        Map<String,Object> map1 = new HashMap<String, Object>();
        map1.put("names", "names1");
        map1.put("ages", 23);
        map1.put("sexs", "男");
        map1.put("deses", "测试1");
        dataList.add(map1);
        Map<String,Object> map2 = new HashMap<String, Object>();
        map2.put("names", "names2");
        map2.put("ages", 24);
        map2.put("sexs", "女");
        map2.put("deses", "测试2");
        dataList.add(map2);
        Map<String,Object> map3 = new HashMap<String, Object>();
        map3.put("names", "names3");
        map3.put("ages", 25);
        map3.put("sexs", "男");
        map3.put("deses", "测试3");
        dataList.add(map3);

        ExcelHandle handle = new  ExcelHandle();
        handle.writeListData(tempFilePath, dataListCell, dataList, 0);

        List<String> dataCell = new ArrayList<String>();
        dataCell.add("name");
        dataCell.add("age");
        dataCell.add("sex");
        dataCell.add("des");
        Map<String,Object> dataMap = new  HashMap<String, Object>();
        dataMap.put("name", "name");
        dataMap.put("age", 11);
        dataMap.put("sex", "女");
        dataMap.put("des", "测试");

        handle.writeData(tempFilePath, dataCell, dataMap, 0);

        File file = new File("d:/data.xlsx");
        OutputStream os = new FileOutputStream(file);
        //写到输出流并关闭资源
        handle.writeAndClose(tempFilePath, os);

        os.flush();
        os.close();

        System.out.println("读取写入的数据----------------------------------%%%");
        System.out.println("name:"+handle.getValue(tempFilePath, "name", 0, file));
        System.out.println("age:"+handle.getValue(tempFilePath, "age", 0, file));
        System.out.println("sex:"+handle.getValue(tempFilePath, "sex", 0, file));
        System.out.println("des:"+handle.getValue(tempFilePath, "des", 0, file));
        System.out.println("读取写入的列表数据----------------------------------%%%");
        List<Map<String,Object>> list = handle.getListValue(tempFilePath, dataListCell, 0, file);
        for(Map<String,Object> data:list){
            for(String key:data.keySet()){
                System.out.print(key+":"+data.get(key)+"--");
            }
            System.out.println("");
        }

        handle.readClose(tempFilePath);*/
	}

	private Object getDataValue(JSONObject dataMap, String key) {
		if (key.indexOf(".") > 0) {
			String objKey = key.substring(0, key.indexOf("."));
			Object objValue = dataMap.get(objKey);
			if (objValue instanceof JSONObject) {
				return getDataValue((JSONObject) objValue, key.substring(key.indexOf(".") + 1));
			} else {
				return null;
			}
		} else {
			return dataMap.get(key);
		}
	}


	/**
	 * Excel文件读值
	 *
	 * @param tempFilePath
	 * @param cell
	 * @param sheet
	 * @return
	 * @throws IOException
	 */
	public Object getValue(String tempFilePath, String cell, int sheet, File excelFile) throws IOException {
		//获取模板填充格式位置等数据
		Map<String, Object> tem = getTemp(tempFilePath, sheet);
		//模板工作区
		Workbook temWorkbook = getTempWorkbook(tempFilePath);
		//数据工作区
		Workbook dataWorkbook = getDataWorkbook(tempFilePath, excelFile);
		//获取对应单元格数据
		Cell c = getCell(cell, tem, temWorkbook, tempFilePath);
		//数据sheet
		Sheet dataSheet = dataWorkbook.getSheetAt(sheet);
		return ExcelUtil.getCellValue(dataSheet, c.getLine(), c.getColumn());
	}

	/**
	 * 读值列表值
	 *
	 * @param tempFilePath
	 * @param cellList
	 * @param sheet
	 * @return
	 * @throws IOException
	 */
	public List<Map<String, Object>> getListValue(String tempFilePath, List<String> cellList, int sheet, File excelFile) throws IOException {
		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
		//获取模板填充格式位置等数据
		Map<String, Object> tem = getTemp(tempFilePath, sheet);
		//获取数据填充开始行
		int startCell = Integer.parseInt((String) tem.get("STARTCELL"));
		//将Excel文件转换为工作区间
		Workbook dataWorkbook = getDataWorkbook(tempFilePath, excelFile);
		//数据sheet
		Sheet dataSheet = dataWorkbook.getSheetAt(sheet);
		//文件最后一行
		int lastLine = dataSheet.getLastRowNum();

		for (int i = startCell; i <= lastLine; i++) {
			dataList.add(getListLineValue(i, tempFilePath, cellList, sheet, excelFile));
		}
		return dataList;
	}

	/**
	 * 读值一行列表值
	 *
	 * @param tempFilePath
	 * @param cellList
	 * @param sheet
	 * @return
	 * @throws IOException
	 */
	public Map<String, Object> getListLineValue(int line, String tempFilePath, List<String> cellList, int sheet, File excelFile) throws IOException {
		Map<String, Object> lineMap = new HashMap<String, Object>();
		//获取模板填充格式位置等数据
		Map<String, Object> tem = getTemp(tempFilePath, sheet);
		//按模板为写入板
		Workbook temWorkbook = getTempWorkbook(tempFilePath);
		//将Excel文件转换为工作区间
		Workbook dataWorkbook = getDataWorkbook(tempFilePath, excelFile);
		//数据sheet
		Sheet dataSheet = dataWorkbook.getSheetAt(sheet);
		for (String cell : cellList) {
			//获取对应单元格数据
			Cell c = getCell(cell, tem, temWorkbook, tempFilePath);
			lineMap.put(cell, ExcelUtil.getCellValue(dataSheet, line, c.getColumn()));
		}
		return lineMap;
	}


	/**
	 * 获得模板输入流
	 *
	 * @param tempFilePath
	 * @return
	 * @throws FileNotFoundException
	 */
	private FileInputStream getFileInputStream(String tempFilePath) throws FileNotFoundException {
		if (!tempStream.containsKey(tempFilePath)) {
			tempStream.put(tempFilePath, new FileInputStream(tempFilePath));
		}

		return tempStream.get(tempFilePath);
	}

	/**
	 * 获得输入工作区
	 *
	 * @param tempFilePath
	 * @return
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public Workbook getTempWorkbook(String tempFilePath) throws FileNotFoundException, IOException {
		if (!tempWorkbook.containsKey(tempFilePath)) {
			if (tempFilePath.endsWith(".xlsx")) {
				tempWorkbook.put(tempFilePath, new XSSFWorkbook(getFileInputStream(tempFilePath)));
			} else if (tempFilePath.endsWith(".xls")) {
				tempWorkbook.put(tempFilePath, new HSSFWorkbook(getFileInputStream(tempFilePath)));
			}
		}
		return tempWorkbook.get(tempFilePath);
	}

	/**
	 * 获取对应单元格样式等数据数据
	 *
	 * @param cell
	 * @param tem
	 * @param wbModule
	 * @param tempFilePath
	 * @return
	 */
	private Cell getCell(String cell, Map<String, Object> tem, Workbook wbModule, String tempFilePath) {
		if (!cellMap.get(tempFilePath).containsKey(cell)) {
			Cell c = new Cell();

			int[] pos = ExcelUtil.getPos(tem, cell);
			if (pos.length > 1) {
				c.setLine(pos[1]);
			}
			c.setColumn(pos[0]);
			c.setCellStyle((ExcelUtil.getStyle(tem, cell, wbModule)));
			cellMap.get(tempFilePath).put(cell, c);
		}
		return cellMap.get(tempFilePath).get(cell);
	}

	/**
	 * 获取模板数据
	 *
	 * @param tempFilePath 模板文件路径
	 * @param sheet
	 * @return
	 * @throws IOException
	 */
	private Map<String, Object> getTemp(String tempFilePath, int sheet) throws IOException {
		if (!tempFileMap.containsKey(tempFilePath)) {
			tempFileMap.put(tempFilePath, ExcelUtil.getTemplateFile(tempFilePath));
			cellMap.put(tempFilePath, new HashMap<String, Cell>());
		}
		return tempFileMap.get(tempFilePath).get(sheet);
	}

	/**
	 * 资源关闭
	 *
	 * @param tempFilePath 模板文件路径
	 * @param os           输出流
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	public void writeAndClose(String tempFilePath, OutputStream os) throws FileNotFoundException, IOException {
		if (getTempWorkbook(tempFilePath) != null) {
			getTempWorkbook(tempFilePath).write(os);
			tempWorkbook.remove(tempFilePath);
		}
		if (getFileInputStream(tempFilePath) != null) {
			getFileInputStream(tempFilePath).close();
			tempStream.remove(tempFilePath);
		}
	}

	/**
	 * 获得读取数据工作间
	 *
	 * @param tempFilePath
	 * @param excelFile
	 * @return
	 * @throws IOException
	 * @throws FileNotFoundException
	 */
	private Workbook getDataWorkbook(String tempFilePath, File excelFile) throws FileNotFoundException, IOException {
		if (!dataWorkbook.containsKey(tempFilePath)) {
			if (tempFilePath.endsWith(".xlsx")) {
				dataWorkbook.put(tempFilePath, new XSSFWorkbook(new FileInputStream(excelFile)));
			} else if (tempFilePath.endsWith(".xls")) {
				dataWorkbook.put(tempFilePath, new HSSFWorkbook(new FileInputStream(excelFile)));
			}
		}
		return dataWorkbook.get(tempFilePath);
	}

	/**
	 * 读取数据后关闭
	 *
	 * @param tempFilePath
	 */
	public void readClose(String tempFilePath) {
		dataWorkbook.remove(tempFilePath);
	}

	/**
	 * 按模板向Excel中相应地方填充数据
	 *
	 * @param tempFilePath excel模板文件路径
	 * @param dataMap      填充的数据
	 * @throws IOException
	 */
	public void writeData(String tempFilePath, JSONObject dataMap) throws IOException {
		//按模板为写入板
		Workbook wbModule = getTempWorkbook(tempFilePath);
		//数据填充的sheet

		for (int i = 0; i < wbModule.getNumberOfSheets(); i++) {
			writeData(tempFilePath, dataMap, i);
		}
	}

	/**
	 * 按模板向Excel中相应地方填充数据
	 *
	 * @param tempFilePath excel模板文件路径
	 * @param dataMap      填充的数据
	 * @param sheet        填充的excel sheet,从0开始
	 * @throws IOException
	 */
	public void writeData(String tempFilePath, JSONObject dataMap, int sheet) throws IOException {
		//获取模板填充格式位置等数据
		Map<String, Object> tem = getTemp(tempFilePath, sheet);
		//按模板为写入板
		Workbook wbModule = getTempWorkbook(tempFilePath);
		//数据填充的sheet
		Sheet wsheet = wbModule.getSheetAt(sheet);

		int startCell = -1;

		if (tem.get("STARTCELL") != null) {
			startCell = Integer.parseInt((String) tem.get("STARTCELL"));
		}

		String listName = null;
		Map<String, Integer> listCellName = new HashMap<>();
		Map<String, CellStyle> listCellStyle = new HashMap<>();
		Map<String, CellStyle> newListCellStyle = new HashMap<>();

		for (String key : tem.keySet()) {
			Object value = tem.get(key);
			if (value instanceof String) {
				String vStr = value.toString();
				if (vStr.indexOf(",") > 0) {
					String[] pos = vStr.split(",");
					Object dataValue = getDataValue(dataMap, key);
					CellStyle cellStyle = (CellStyle) tem.get(key + "CellStyle");
					ExcelUtil.setValue(wsheet, Integer.parseInt(pos[1]), Integer.parseInt(pos[0]), dataValue, cellStyle);
				} else {
					if (key != "STARTCELL" && key.indexOf(".") > 0) {
						listName = key.substring(0, key.indexOf("."));
						String keyName = key.substring(key.indexOf(".") + 1);
						listCellName.put(keyName, Integer.parseInt(value.toString()));
						listCellStyle.put(keyName, (CellStyle) tem.get(key + "CellStyle"));
					}
				}
			}
		}
		for (String key : listCellStyle.keySet()) {
			CellStyle newStyle = wsheet.getWorkbook().createCellStyle();
			newStyle.cloneStyleFrom(listCellStyle.get(key));
			newListCellStyle.put(key, newStyle);
		}


		if (startCell >= 0) {
			wsheet.removeRow(wsheet.getRow(startCell));
			if (listName != null) {
				Object listObj = dataMap.get(listName);
				if (listObj != null && listObj instanceof JSONArray) {
					JSONArray list = (JSONArray) listObj;
					for (Object obj : list) {
						if (obj instanceof JSONObject) {
							for (String key : listCellName.keySet()) {
								Integer colNum = listCellName.get(key);
								Object dataValue = getDataValue((JSONObject) obj, key);
								ExcelUtil.setValue(wsheet, startCell, colNum, dataValue, newListCellStyle.get(key));
							}
							startCell++;
						}
					}
				}
			}
		}
	}

	/**
	 * 单无格类
	 *
	 * @author xiliang.xiao
	 */
	class Cell {
		private int column;//列
		private int line;//行
		private CellStyle cellStyle;

		public int getColumn() {
			return column;
		}

		public void setColumn(int column) {
			this.column = column;
		}

		public int getLine() {
			return line;
		}

		public void setLine(int line) {
			this.line = line;
		}

		public CellStyle getCellStyle() {
			return cellStyle;
		}

		public void setCellStyle(CellStyle cellStyle) {
			this.cellStyle = cellStyle;
		}
	}

}