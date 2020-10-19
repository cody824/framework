package com.noknown.framework.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.jdbc.datasource.init.DataSourceInitializer;
import org.springframework.jdbc.datasource.init.DatabasePopulator;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;

import javax.sql.DataSource;

/**
 * @author guodong
 * @date 2019/9/9
 */
@Configuration
public class SqlUpdate {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	/**
	 * 自定义Bean实现业务的特殊需求
	 *
	 * @param dataSource
	 * @return
	 */
	@Bean
	public DataSourceInitializer dataSourceInitializer(final DataSource dataSource) {
		final DataSourceInitializer initializer = new DataSourceInitializer();
		// 设置数据源
		initializer.setDataSource(dataSource);
		initializer.setDatabasePopulator(databasePopulator());

		return initializer;
	}

	private DatabasePopulator databasePopulator() {
		ResourceLoader resourceLoader = new DefaultResourceLoader();
		Resource resource = resourceLoader.getResource("update.sql");
		final ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
		if (resource.exists() && resource.isFile()) {
			logger.info("执行更新脚本：" + resource);
			populator.addScripts(resource);
		} else {
			logger.info("没有SQL更新脚本");
		}
		return populator;
	}

}
