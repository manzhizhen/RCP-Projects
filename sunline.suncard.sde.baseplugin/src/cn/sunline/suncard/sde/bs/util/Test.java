package cn.sunline.suncard.sde.bs.util;

import java.io.UnsupportedEncodingException;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;

import cn.sunline.suncard.sde.exception.SyntaxErrorException;


public class Test {
	public int syntaxCheck(String syntax) throws DocumentException, SyntaxErrorException, UnsupportedEncodingException{
		Document document = XMLUtil.getDocumentBsSax(syntax);
		String a=getXPathFromSyntax();
		System.out.println(a);
		List<Node> list = document.selectNodes(a);
		for(int i=0;i<list.size();i++){
			System.out.println(list.get(i).getText());
		}
		return list.size();
	}
	
	//public String getXPathFromSyntax
	
	public static void main(String args[]){
//		Test t=new Test();
//		try {
//			t.syntaxCheck("<Datapart><tableName id='cps_Id' name='系统参数'><columnName><field id='bankorgId' isKey='Y' name='银行机构'>1001</field><field id='idType' isKey='Y' name='银行机构'>0</field><field id='idNum' isKey='Y' name='银行机构'>11111</field></columnName><columnName><field id='bankorgId' isKey='Y' name='银行机构'>1001</field><field id='idType' isKey='Y' name='银行机构'>1</field><field id='idNum' isKey='Y' name='银行机构'>22222</field></columnName></tableName></Datapart>");
//		} catch (DocumentException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
////		String[] a="bankorgId='1001';idType='0';".split(";");
////		System.out.println(a.length);
////		for(String b : a){
////			System.out.println("dddd"+b+"ffff");
////		}
// catch (SyntaxErrorException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		double a=123.44f;
		Object b=a;
		System.out.println(b.toString());
		
	}
	
	
	public String getXPathFromSyntax() throws SyntaxErrorException{
		String syntax="cps_Id/idNum[bankorgId='1001';idType='1';]";
		String[] pathArray=syntax.split("/");
		if(pathArray.length != 2)throw new SyntaxErrorException();
		String table_name=pathArray[0];
		int symbolIndex=pathArray[1].indexOf("[");
		
		String field_name=pathArray[1].substring(0, symbolIndex);
		String[] conditions=pathArray[1].substring(symbolIndex+1, pathArray[1].length()-1).split(";");
		
		StringBuffer xpathSyntax=new StringBuffer();
		xpathSyntax.append("/");
		xpathSyntax.append(DATA_PART);
		xpathSyntax.append("/");
		xpathSyntax.append(TABLE_NAME);
		xpathSyntax.append("[@id='");
		xpathSyntax.append(table_name);
		xpathSyntax.append("']/");
		xpathSyntax.append(COLUMN_NAME);
		xpathSyntax.append("[");
		for(int i=0;i<conditions.length;i++){
			xpathSyntax.append(FIELD);
			xpathSyntax.append("[@id='");
			String[] condition = conditions[i].split("=");
			if(condition.length != 2)throw new SyntaxErrorException();
			xpathSyntax.append(condition[0]);
			xpathSyntax.append("']=");
			xpathSyntax.append(condition[1]);
			if(i < conditions.length-1){
				xpathSyntax.append(" and ");
			}
		}
		xpathSyntax.append("]/");
		xpathSyntax.append(FIELD);
		xpathSyntax.append("[@id='");
		xpathSyntax.append(field_name);
		xpathSyntax.append("']");
		
		return xpathSyntax.toString();
	}
	
	private final static String DATA_PART="Datapart";
	private final static String TABLE_NAME="tableName";
	private final static String COLUMN_NAME="columnName";
	private final static String FIELD="field";
}
