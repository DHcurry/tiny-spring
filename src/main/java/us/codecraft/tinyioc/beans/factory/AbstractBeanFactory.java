package us.codecraft.tinyioc.beans.factory;

import us.codecraft.tinyioc.beans.BeanDefinition;
import us.codecraft.tinyioc.beans.BeanPostProcessor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 负责类装配的抽象工厂
 * @author yihua.huang@dianping.com
 */
public abstract class AbstractBeanFactory implements BeanFactory {
	/** ioc容器
	 * 在之前我们的AbstractBeanDefinitionReader也存储了一个类似的registMap
	 * 我们可以看到registerBeanDefinition这个方法
	 * 这个方法的作用就是获取registMap并将其注册进这个容器中
	 * */
	private Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, BeanDefinition>();

	/** 用于存储所有装配好的类名*/
	private final List<String> beanDefinitionNames = new ArrayList<String>();

	/**
	 * bean处理器列表
	 */
	private List<BeanPostProcessor> beanPostProcessors = new ArrayList<BeanPostProcessor>();

	/**
	 * 对外暴露的获取bean的方法
	 * @param name
	 * @return
	 * @throws Exception
	 */
	@Override
	public Object getBean(String name) throws Exception {
		BeanDefinition beanDefinition = beanDefinitionMap.get(name);
		if (beanDefinition == null) {
			throw new IllegalArgumentException("No bean named " + name + " is defined");
		}
		Object bean = beanDefinition.getBean();
		if (bean == null) {
			// 创建bean
			bean = doCreateBean(beanDefinition);
			// 对bean进行前置与后置处理
            bean = initializeBean(bean, name);
            // 向beanDefinition设置bean实例
			// 其实在createBean中也设置了 但是进行了前置与后置处理
            beanDefinition.setBean(bean);
		}
		return bean;
	}

	/** initbean 表示对bean进行前置和后置处理
	 * @param bean
	 * @param name
	 * @return
	 * @throws Exception
	 */
	protected Object initializeBean(Object bean, String name) throws Exception {
		for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
			bean = beanPostProcessor.postProcessBeforeInitialization(bean, name);
		}

		// TODO:call initialize method
		for (BeanPostProcessor beanPostProcessor : beanPostProcessors) {
            bean = beanPostProcessor.postProcessAfterInitialization(bean, name);
		}
        return bean;
	}

	/**
	 * 利用反射创建bean实例
	 * @param beanDefinition
	 * @return
	 * @throws Exception
	 */
	protected Object createBeanInstance(BeanDefinition beanDefinition) throws Exception {
		return beanDefinition.getBeanClass().newInstance();
	}

	/**
	 * 向map中注册bean
	 * @param name
	 * @param beanDefinition
	 * @throws Exception
	 */
	public void registerBeanDefinition(String name, BeanDefinition beanDefinition) throws Exception {
		beanDefinitionMap.put(name, beanDefinition);
		beanDefinitionNames.add(name);
	}

	/**
	 * 遍历bean的name并调用getbean方法
	 * 该方法是放在context调用的
	 * @throws Exception
	 */
	public void preInstantiateSingletons() throws Exception {
		for (Iterator it = this.beanDefinitionNames.iterator(); it.hasNext();) {
			String beanName = (String) it.next();
			getBean(beanName);
		}
	}

	/**
	 * 创建bean
	 * @param beanDefinition
	 * @return
	 * @throws Exception
	 */
	protected Object doCreateBean(BeanDefinition beanDefinition) throws Exception {
		Object bean = createBeanInstance(beanDefinition);
		beanDefinition.setBean(bean);
		applyPropertyValues(bean, beanDefinition);
		return bean;
	}

	/**
	 *
	 * @param bean
	 * @param beanDefinition
	 * @throws Exception
	 */
	protected void applyPropertyValues(Object bean, BeanDefinition beanDefinition) throws Exception {

	}

	/**
	 * 添加前置与后置处理器
	 * @param beanPostProcessor
	 * @throws Exception
	 */
	public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) throws Exception {
		this.beanPostProcessors.add(beanPostProcessor);
	}

	/**
	 * todo
	 * @param type
	 * @return
	 * @throws Exception
	 */
	public List getBeansForType(Class type) throws Exception {
		List beans = new ArrayList<Object>();
		for (String beanDefinitionName : beanDefinitionNames) {
			if (type.isAssignableFrom(beanDefinitionMap.get(beanDefinitionName).getBeanClass())) {
				beans.add(getBean(beanDefinitionName));
			}
		}
		return beans;
	}

}
