package us.codecraft.tinyioc.beans.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import us.codecraft.tinyioc.BeanReference;
import us.codecraft.tinyioc.beans.AbstractBeanDefinitionReader;
import us.codecraft.tinyioc.beans.BeanDefinition;
import us.codecraft.tinyioc.beans.PropertyValue;
import us.codecraft.tinyioc.beans.io.ResourceLoader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;

/**
 * @author yihua.huang@dianping.com
 */
public class XmlBeanDefinitionReader extends AbstractBeanDefinitionReader {

	public XmlBeanDefinitionReader(ResourceLoader resourceLoader) {
		super(resourceLoader);
	}

	/**
	 * 对外暴露的获取 BeanDefinition
	 * @param location
	 * @throws Exception
	 */
	@Override
	public void loadBeanDefinitions(String location) throws Exception {
		// 获取输入流
		InputStream inputStream = getResourceLoader().getResource(location).getInputStream();
		// 将处理方法私有封装
		doLoadBeanDefinitions(inputStream);
	}

	protected void doLoadBeanDefinitions(InputStream inputStream) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = factory.newDocumentBuilder();
		Document doc = docBuilder.parse(inputStream);
		// 解析bean
		registerBeanDefinitions(doc);
		inputStream.close();
	}

	/**
	 * 解析bean
	 * @param doc
	 */
	public void registerBeanDefinitions(Document doc) {
		Element root = doc.getDocumentElement();
		// 真正开始解析bean
		parseBeanDefinitions(root);
	}

	/**
	 * 真正开始解析bean
	 * @param root
	 */
	protected void parseBeanDefinitions(Element root) {
		NodeList nl = root.getChildNodes();
		for (int i = 0; i < nl.getLength(); i++) {
			Node node = nl.item(i);
			if (node instanceof Element) {
				Element ele = (Element) node;
				// 将解析的信息转换成beanDefinition对象
				processBeanDefinition(ele);
			}
		}
	}

	/**
	 * 将解析的信息转换成beanDefinition对象
	 * @param ele
	 */
	protected void processBeanDefinition(Element ele) {
		String name = ele.getAttribute("id");
		String className = ele.getAttribute("class");
		BeanDefinition beanDefinition = new BeanDefinition();
		// 处理bean定义中的property
		processProperty(ele, beanDefinition);
		beanDefinition.setBeanClassName(className);
		getRegistry().put(name, beanDefinition);
	}

	/**
	 * 处理bean定义中的property
	 * @param ele
	 * @param beanDefinition
	 */
	private void processProperty(Element ele, BeanDefinition beanDefinition) {
		NodeList propertyNode = ele.getElementsByTagName("property");
		for (int i = 0; i < propertyNode.getLength(); i++) {
			Node node = propertyNode.item(i);
			if (node instanceof Element) {
				Element propertyEle = (Element) node;
				String name = propertyEle.getAttribute("name");
				String value = propertyEle.getAttribute("value");
				if (value != null && value.length() > 0) {
					// 如果值（value）不为空 表示该值是一个普通类型
					beanDefinition.getPropertyValues().addPropertyValue(new PropertyValue(name, value));
				} else {
					// 如果值为空 需要指定ref 表示该值是你一个对象
					String ref = propertyEle.getAttribute("ref");
					if (ref == null || ref.length() == 0) {
						throw new IllegalArgumentException("Configuration problem: <property> element for property '"
								+ name + "' must specify a ref or value");
					}
					// 定义ref对象
					BeanReference beanReference = new BeanReference(ref);
					beanDefinition.getPropertyValues().addPropertyValue(new PropertyValue(name, beanReference));
				}
			}
		}
	}
}
