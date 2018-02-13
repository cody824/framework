package com.noknown.framework.fss.config;

import com.noknown.framework.fss.service.FileStoreService;
import com.noknown.framework.fss.service.FileStoreServiceRepo;
import com.noknown.framework.fss.service.impl.AliFSImpl;
import com.noknown.framework.fss.service.impl.FileFSImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.HashMap;
import java.util.Map;

@Configuration
@PropertySource(value = "${spring.config.custom-path:classpath:}conf/${spring.profiles.active}/fss.properties", ignoreResourceNotFound = true)
@PropertySource(value = "classpath:app.properties", ignoreResourceNotFound = true)
public class FSSConfig {

    public final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private OSSConfig ossConfig;

    @Bean
    public FileStoreServiceRepo initGlobalFSSRepo(@Value("${framework.fss.oss:false}") boolean oss,
                                                  @Value("${framework.fss.fileFS:false}") boolean fileFS,
                                                  @Value("${framework.fss.fileFS.basePath:}") String basePath,
                                                  @Value("${framework.fss.fileFS.baseUrl:}") String baseUrl) {
        Map<String, FileStoreService> map = new HashMap<>();
        if (oss) {
	        AliFSImpl aliFsImpl = new AliFSImpl();
	        aliFsImpl.setConfig(ossConfig);
            logger.debug("INIT OSS FSS");
	        map.put("alioss", aliFsImpl);
        }
        if (fileFS) {
	        FileFSImpl fs = new FileFSImpl(basePath, baseUrl);
            logger.debug("INIT FILE FSS");
            map.put("file", fs);
        }
	    FileStoreServiceRepo repo = type -> {
		    if (map.size() > 0) {
			    if (type == null) {
                    type = map.keySet().iterator().next();
			    }
			    return map.get(type);
		    }
		    return null;
        };
        return repo;
    }

}
