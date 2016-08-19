package cn.zhaoyuening.web.filter;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

public class Struts2Filter implements Filter{

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		//解析struts.xml文件
		SAXReader reader = new SAXReader();
		try {
			Document document=reader.read(new File(this.getClass().getResource("/struts.xml").getPath()));
			System.out.println(document);
		} catch (DocumentException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		System.out.println("=====================dofilter");
		//转换
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;
		//操作
		String url = req.getRequestURI();
		String contextPath = req.getContextPath();
		String path = url.substring(contextPath.length()+1);
		System.out.println("path="+path);
		//解析配置文件 struts.xml
		SAXReader reader = new SAXReader();
		try {
			//获取文档
			/**
			 * <?xml version="1.0" encoding="UTF-8"?>
				<struts>
				    <package name="default" namespace="/" extends="struts-default">
				        <action name="hello" class="cn.zhaoyuening.web.action.HelloAction">
				            <result name="success">/hello.jsp</result>
				        </action>
				    </package>
				
				</struts>
			 */
			Document document = reader.read(new File(this.getClass().getResource("/struts.xml").getPath()));
			System.out.println(document.asXML());
			//解析文档
			Element rootElement = document.getRootElement();
			System.out.println("element root name:"+rootElement.getName());
			List<Element> packageElements = rootElement.elements("package");
			for (Element packageElement : packageElements) {
				List<Element> actions = packageElement.elements("action");
				for (Element actionElement : actions) {
					if (path.equals(actionElement.attributeValue("name"))) {
						//匹配到相对应的action
						//获取类信息
						String classInfo = actionElement.attributeValue("class");
						Class actionClass = Class.forName(classInfo);
						//获取方法名
						String methodName = actionElement.attributeValue("methods", "execute");
						Method[] methods = actionClass.getMethods();
						for (Method method : methods) {
							if (method.getName().equals(methodName)) {
								//找到对应的方法 利用放射运行结果
								String result = (String) method.invoke(actionClass.newInstance());
								//获取结果列表
								List<Element> resulteElements = actionElement.elements("result");
								for (Element resulteElement : resulteElements) {
									if (result.equals(resulteElement.attributeValue("name"))) {
										String r = resulteElement.getText();
										System.out.println("r="+r);
										req.getRequestDispatcher(r).forward(request, response);
										return ;
									}
								}
								
							}
						}
					}
				}

			}
						
		} catch (DocumentException e ) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		}
		
		//放行
		chain.doFilter(request, response);
	}

	@Override
	public void destroy() {
	}

}
