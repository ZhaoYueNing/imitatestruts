package cn.zhaoyuening.test;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.junit.Test;
/**
 */
public class TestClass {
	@Test
	public void test1() throws Exception {
		SAXReader reader = new SAXReader();
//		File f = new File(this.getClass().getResource("/struts.xml").getPath());
		Document document = reader.read(new File("C:\\Users\\Zhao\\Workspaces\\MyEclipse Professional 2014\\imitatestruts\\WebRoot\\WEB-INF\\classes\\struts.xml"));
//		System.out.println(document.asXML());
		Element rootElement = document.getRootElement();
		for (Iterator<Element> i = rootElement.elementIterator("action");  i.hasNext()	; ) {
			Element element = i.next();
			System.out.println(element.asXML());
		}
//		Document document=reader.read());
//		System.out.println(document);
	}
}
