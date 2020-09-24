package com.noknown.framework.common.dialect;

import com.noknown.framework.common.hql.BitAndFunction;
import org.hibernate.dialect.*;

/**
 * @author guodong
 */
public class CustomMysqlDialect extends MySQL57Dialect {

	public CustomMysqlDialect() {
		super();
		this.registerFunction("bitand", new BitAndFunction());
	}

}
