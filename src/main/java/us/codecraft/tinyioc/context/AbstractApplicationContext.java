package us.codecraft.tinyioc.context;

import us.codecraft.tinyioc.beans.BeanPostProcessor;
import us.codecraft.tinyioc.beans.factory.AbstractBeanFactory;

import java.util.List;

/**
 * @author yihua.huang@dianping.com
 */
public abstract class AbstractApplicationContext implements ApplicationContext {
	protected AbstractBeanFactory beanFactory;

	public AbstractApplicationContext(AbstractBeanFactory beanFactory) {
		this.beanFactory = beanFactory;
	}

	/**
	 * 这个就是对外暴露，将getResource 解析resource 并且注入ioc容器这些步骤放到一起
	 * 加载或刷新一个持久化的配置，可能是XML文件、属性文件或关系数据库模式。
	 * 由于这是一种启动方法，如果失败，应该销毁已经创建的单例，以避免悬空资源。
	 * 换句话说，在调用该方法之后，要么全部实例化，要么完全不实例化。
	 * @throws Exception
	 */
	public void refresh() throws Exception {
		loadBeanDefinitions(beanFactory);
		registerBeanPostProcessors(beanFactory);
		onRefresh();
	}

	/**
	 * 载入bean关系（读取并获得关系）
	 * @param beanFactory
	 * @throws Exception
	 */
	protected abstract void loadBeanDefinitions(AbstractBeanFactory beanFactory) throws Exception;

	/**
	 * 注册bean处理器
	 * @param beanFactory
	 * @throws Exception
	 */
	protected void registerBeanPostProcessors(AbstractBeanFactory beanFactory) throws Exception {
		List beanPostProcessors = beanFactory.getBeansForType(BeanPostProcessor.class);
		for (Object beanPostProcessor : beanPostProcessors) {
			beanFactory.addBeanPostProcessor((BeanPostProcessor) beanPostProcessor);
		}
	}

	protected void onRefresh() throws Exception{
        beanFactory.preInstantiateSingletons();
    }

	@Override
	public Object getBean(String name) throws Exception {
		return beanFactory.getBean(name);
	}
}
