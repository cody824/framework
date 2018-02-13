/**
 * @Title: OrderProperties.java
 * @Package com.soulinfo.commons.util
 * @Description: 基础工具类
 * CopyRright (c) 2014-2015 SOUL
 * Company:无锡众志和达数据计算股份有限公司
 * 
 * @author xingweiwei
 * @date 2015年5月19日 下午3:05:54
 * @version V0.0.1
 */
package com.noknown.framework.common.util;

import java.util.*;

public class OrderProperties extends Properties {
	private static final long serialVersionUID = -4627607243846121965L;
    private final LinkedHashSet<Object> keys = new LinkedHashSet<Object>();

	@Override
	public Enumeration<Object> keys() {
		return Collections.<Object> enumeration(keys);
	}

	@Override
	public Object put(Object key, Object value) {
		if (key.toString().indexOf("#") != -1) {
			return null;
		}
		keys.add(key);
		return super.put(key, value);
	}

	@Override
	public synchronized Object remove(Object key) {
		keys.remove(key);
		return super.remove(key);
	}

	@Override
	public Set<Object> keySet() {
		return keys;
	}

	@Override
	public Set<String> stringPropertyNames() {
		Set<String> set = new LinkedHashSet<String>();
		for (Object key : this.keys) {
			set.add((String) key);
		}
		return set;
	}
}