package us.codecraft.tinyioc.beans.io;

import java.net.URL;

/**
 * @author yihua.huang@dianping.com
 */
public class ResourceLoader {
    /**
     * 通过该加载器加载Resource
     * 由于设计简单，因此这里直接加载的是UrlResource
     * @param location
     * @return
     */
    public Resource getResource(String location){
        URL resource = this.getClass().getClassLoader().getResource(location);
        return new UrlResource(resource);
    }
}
