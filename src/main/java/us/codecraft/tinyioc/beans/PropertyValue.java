package us.codecraft.tinyioc.beans;

/**
 * 用于bean的属性注入
 * 比如一个User对象中有个String username的属性
 * 那么该对象的name会存储username
 * 该对象的value会存储（String）xxx表示该值
 * @author yihua.huang@dianping.com
 */
public class PropertyValue {

    private final String name;

    private final Object value;

    public PropertyValue(String name, Object value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public Object getValue() {
        return value;
    }
}
