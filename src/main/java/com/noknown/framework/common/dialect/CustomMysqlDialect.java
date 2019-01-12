package com.noknown.framework.common.dialect;

import com.noknown.framework.common.hql.BitAndFunction;
import org.hibernate.dialect.MySQL5Dialect;

/**
 * @author guodong
 */
public class CustomMysqlDialect extends MySQL5Dialect {

	public CustomMysqlDialect() {
		super();
		this.registerFunction("bitand", new BitAndFunction());
	}
}
