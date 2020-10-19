package com.noknown.framework.common;

import com.noknown.framework.common.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.FileSystemResourceLoader;
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

	@Value("${UPDATE_SQL:#{null}}")
	private String uploadSQL;

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
		initializer.setDatabasePopulator(databasePopulator(uploadSQL));
		return initializer;
	}

	private DatabasePopulator databasePopulator(String updateSQLPath) {
		ResourceLoader resourceLoader = new DefaultResourceLoader();
		Resource resource = resourceLoader.getResource("update.sql");
		Resource resourcePath = null;
		if (StringUtil.isNotBlank(updateSQLPath)) {
			ResourceLoader fileSystemResourceLoader = new FileSystemResourceLoader();
			resourcePath = fileSystemResourceLoader.getResource(updateSQLPath);
		}
		final ResourceDatabasePopulator populator = new ResourceDatabasePopulator();
		if (resource.exists() && resource.isFile()) {
			logger.info("执行CLASSPATH更新脚本：" + resource);
			populator.addScripts(resource);
		} else {
			logger.info("没有CLASSPATH SQL更新脚本");
		}
		if (resourcePath != null) {
			if (resourcePath.exists() && resourcePath.isFile()) {
				logger.info("执行指定更新脚本：" + resourcePath);
				populator.addScripts(resourcePath);
			} else {
				logger.warn("指定脚本{}不存在", updateSQLPath);
			}
		} else {
			logger.info("没有指定SQL更新脚本");
		}
		return populator;
	}

}
