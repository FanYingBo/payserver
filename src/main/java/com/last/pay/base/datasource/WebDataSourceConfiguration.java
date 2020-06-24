package com.last.pay.base.datasource;


import java.security.interfaces.RSAPublicKey;

import javax.sql.DataSource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.last.pay.base.datasource.config.DataSourceConfig;
import com.last.pay.util.EncryptUtil;

/**
 * DataSource配置
 * @author Administrator
 *
 */
@Configuration
@MapperScan(basePackages = "com.last.pay.core.db.mapper.webdb",sqlSessionFactoryRef = "webSqlSessionFactory")
public class WebDataSourceConfiguration {
	
	@Value("${datasource.web.location}")
	private String webLocation;
	@Autowired
	private DataSourceConfig dataSourceConfig;
	@Value("${db.encrypt}")
	private Boolean encrypt;
	@Value("${publicKey}")
	private String publicKey;
	/**
	 * 构建UserDb 的DataSource
	 * @return
	 */
	@Bean
	public DataSource webDataSource() {
		String webDriverClassName = dataSourceConfig.getWebDriverClassName();
		String webJdbcUrl = dataSourceConfig.getWebJdbcUrl();
		String webUsername = dataSourceConfig.getWebUsername();
		String webPassword = dataSourceConfig.getWebPassword();
		if(encrypt) {
			try {
				RSAPublicKey rsaPublicKey = EncryptUtil.getRSAPublicKey(publicKey);
				webDriverClassName = EncryptUtil.bytesToString(EncryptUtil.decryptByRSAPublicKey(rsaPublicKey, webDriverClassName));
				webJdbcUrl = EncryptUtil.bytesToString(EncryptUtil.decryptByRSAPublicKey(rsaPublicKey, webJdbcUrl));
				webUsername = EncryptUtil.bytesToString(EncryptUtil.decryptByRSAPublicKey(rsaPublicKey, webUsername));
				webPassword = EncryptUtil.bytesToString(EncryptUtil.decryptByRSAPublicKey(rsaPublicKey, webPassword));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return DataSourceBuilder.create()
				.driverClassName(webDriverClassName)
				.url(webJdbcUrl)
				.username(webUsername)
				.password(webPassword)
				.build();
	}
	
	@Bean
	public SqlSessionFactory webSqlSessionFactory(@Qualifier("webDataSource") DataSource webDataSource) throws Exception {
		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(webDataSource);
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		sqlSessionFactoryBean.setMapperLocations(resolver.getResources(webLocation));
		return sqlSessionFactoryBean.getObject();
	}
}
