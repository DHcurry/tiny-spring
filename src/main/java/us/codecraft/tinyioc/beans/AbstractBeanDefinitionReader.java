package us.codecraft.tinyioc.beans;

import us.codecraft.tinyioc.beans.io.ResourceLoader;

import java.util.HashMap;
import java.util.Map;

/**
 * 从配置中读取BeanDefinition
 * @author yihua.huang@dianping.com
 */
public abstract class AbstractBeanDefinitionReader implements BeanDefinitionReader {
    /** 用于保存xml文件当中的注册信息 */
    private Map<String,BeanDefinition> registry;

    /** UrlResource资源加载器*/
    private ResourceLoader resourceLoader;

    protected AbstractBeanDefinitionReader(ResourceLoader resourceLoader) {
        this.registry = new HashMap<String, BeanDefinition>();
        this.resourceLoader = resourceLoader;
    }

    public Map<String, BeanDefinition> getRegistry() {
        return registry;
    }

    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }
}
