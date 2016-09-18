package mainclasses;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class LayoutComparisonTool {

	public static void main(String[] args) {
		try{
			  Workbook layoutComparisonWorkBook = new HSSFWorkbook();
			  //Create DOM object for both files  
			  Document doc1 = parseFile(args[0]);
			  Document doc2 = parseFile(args[1]);
			  List<String> setFieldsLayout1 = retrieveLayout(doc1);
			  List<String> setFieldsLayout2 = retrieveLayout(doc2);
			  compareLayoutsAndGenerateSheet(setFieldsLayout1, setFieldsLayout2, layoutComparisonWorkBook, args);
			  compareLayoutsAndGenerateSheetSideBySide(setFieldsLayout1, setFieldsLayout2, layoutComparisonWorkBook, args);
			  // Write the output to a file
			    FileOutputStream fileOut = new FileOutputStream("LayoutComparison.xls");
			  layoutComparisonWorkBook.write(fileOut);
		      fileOut.close();				  
		}catch(Exception e){
			System.out.println("There was an error generating Comparison Sheet - " + e.getMessage());
		}
	}

	  private static Document parseFile(String fileName){
			Document doc = null;
		  try{
				File fXmlFile = new File(fileName);
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				doc = dBuilder.parse(fXmlFile);
						
				//optional, but recommended
				//read this - http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
				doc.getDocumentElement().normalize();
		  }catch(Exception e){
			  e.printStackTrace();
		  }	
		  return doc;
	  }	
	  private static List<String> retrieveLayout(Document doc){
		  List<String> lstFields = new ArrayList<String>();
		  NodeList nList = doc.getElementsByTagName("field");
			for (int temp = 0; temp < nList.getLength(); temp++) {

				Node nNode = nList.item(temp);
						
				//System.out.println("\nCurrent Element :" + nNode.getNodeName());
						
				if (nNode.getNodeType() == Node.ELEMENT_NODE) {
					Element eElement = (Element) nNode;
					System.out.println(eElement.getFirstChild().getTextContent());
					lstFields.add(eElement.getFirstChild().getTextContent());
				}
			}		
			return lstFields;
	  }
	  private static Integer compareLayoutsAndGenerateSheetSideBySide(List<String> lstFieldsLayout1, List<String> lstFieldsLayout2, Workbook layoutComparisonWorkBook, String args[]){
		  Set<String> setExtraFieldsLayouts1 = new HashSet<String>();
		  Set<String> setExtraFieldsLayouts2 = new HashSet<String>();
		  Set<String> setCombinedLayoutFields = new HashSet<String>();
		  List<String> lstCombinedLayoutFields = new ArrayList<String>();
		  Collections.sort(lstFieldsLayout1);
		  Collections.sort(lstFieldsLayout2);
		  setCombinedLayoutFields.addAll(lstFieldsLayout1);
		  setCombinedLayoutFields.addAll(lstFieldsLayout2);
		  lstCombinedLayoutFields.addAll(setCombinedLayoutFields);
		  Collections.sort(lstCombinedLayoutFields);
		  Integer numberOfRows = 0;
		  Sheet layoutSheet = layoutComparisonWorkBook.createSheet("Layout Comparison Side by Side");		
		  Row row = layoutSheet.createRow((short)0);
		  row.createCell(0).setCellValue(args[0]);
		  row.createCell(1).setCellValue(args[1]);

		  Integer i=1;
		  for(String fieldName:lstCombinedLayoutFields){
			  Row valueRow = layoutSheet.createRow(i);
			  if(lstFieldsLayout1.contains(fieldName) && lstFieldsLayout2.contains(fieldName)){
				  valueRow.createCell(0).setCellValue(fieldName);
				  valueRow.createCell(1).setCellValue(fieldName);
			  }else if(lstFieldsLayout1.contains(fieldName) && !lstFieldsLayout2.contains(fieldName)){
				  valueRow.createCell(0).setCellValue(fieldName);
				  valueRow.createCell(1).setCellValue("");
			  }else if(!lstFieldsLayout1.contains(fieldName) && lstFieldsLayout2.contains(fieldName)){
				  valueRow.createCell(1).setCellValue(fieldName);
				  valueRow.createCell(0).setCellValue("");
			  }
			  i++;
		  }
		  
		  
		  for(String layoutName:lstFieldsLayout1){
			  if(!lstFieldsLayout2.contains(layoutName)){
				  setExtraFieldsLayouts1.add(layoutName);
			  }
		  }
		  for(String layoutName:lstFieldsLayout2){
			  if(!lstFieldsLayout1.contains(layoutName)){
				  setExtraFieldsLayouts2.add(layoutName);
			  }
		  }
		  numberOfRows = layoutSheet.getLastRowNum();

		return numberOfRows;
  }	  
	  
	  private static Integer compareLayoutsAndGenerateSheet(List<String> lstFieldsLayout1, List<String> lstFieldsLayout2, Workbook layoutComparisonWorkBook, String args[]){
		  Set<String> setExtraFieldsLayouts1 = new HashSet<String>();
		  Set<String> setExtraFieldsLayouts2 = new HashSet<String>();
		  Collections.sort(lstFieldsLayout1);
		  Collections.sort(lstFieldsLayout2);
		  Integer numberOfRows = 0;
		  for(String layoutName:lstFieldsLayout1){
			  if(!lstFieldsLayout2.contains(layoutName)){
				  setExtraFieldsLayouts1.add(layoutName);
			  }
		  }
		  for(String layoutName:lstFieldsLayout2){
			  if(!lstFieldsLayout1.contains(layoutName)){
				  setExtraFieldsLayouts2.add(layoutName);
			  }
		  }
		  Sheet layoutSheet = layoutComparisonWorkBook.createSheet("Extra Fields in each layout");		
			try {
				Row row = layoutSheet.createRow((short)0);
				row.createCell(0).setCellValue(args[0]);
				row.createCell(1).setCellValue(args[1]);
				Integer i=1;
				//Write a new student object list to the CSV file
				for (String key:setExtraFieldsLayouts1) {
					Row valueRow = layoutSheet.createRow(i);
					valueRow.createCell(0).setCellValue(key);
					i++;
				}
				i=1;
				for (String key:setExtraFieldsLayouts2) {
					Row valueRow = layoutSheet.getRow(i);
					if(valueRow==null)valueRow = layoutSheet.createRow(i);
					valueRow.createCell(1).setCellValue(key);
					i++;
				}			
				//System.out.println("VF Page Sheet was created successfully !!!");
				numberOfRows = layoutSheet.getLastRowNum();
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
			}
			return numberOfRows;
	  
		  
	  }
	
}
