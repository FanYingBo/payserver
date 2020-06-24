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
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.last.pay.base.datasource.config.DataSourceConfig;
import com.last.pay.util.EncryptUtil;


/**
 * DataSource配置
 * @author Administrator
 *
 */
@Configuration
@MapperScan(basePackages = "com.last.pay.core.db.mapper.logdb",sqlSessionFactoryRef = "logSqlSessionFactory")
public class LogDataSourceConfiguration {
	
	@Value("${datasource.log.location}")
	private String logLocation;
	
	@Autowired
	private DataSourceConfig dataSourceConfig; 
	@Value("${db.encrypt}")
	private Boolean encrypt;
	@Value("${publicKey}")
	private String publicKey;
	/**
	 * 构建LogDb 的DataSource
	 * @return
	 */
	@Bean
	@Primary
	public DataSource logDataSource() {
		String logDriverClassName = dataSourceConfig.getLogDriverClassName();
		String logJdbcUrl = dataSourceConfig.getLogJdbcUrl();
		String logUsername = dataSourceConfig.getLogUsername();
		String logPassword = dataSourceConfig.getLogPassword();
		if(encrypt) {
			try {
				RSAPublicKey rsaPublicKey = EncryptUtil.getRSAPublicKey(publicKey);
				logDriverClassName = EncryptUtil.bytesToString(EncryptUtil.decryptByRSAPublicKey(rsaPublicKey, logDriverClassName));
				logJdbcUrl = EncryptUtil.bytesToString(EncryptUtil.decryptByRSAPublicKey(rsaPublicKey, logJdbcUrl));
				logUsername = EncryptUtil.bytesToString(EncryptUtil.decryptByRSAPublicKey(rsaPublicKey, logUsername));
				logPassword = EncryptUtil.bytesToString(EncryptUtil.decryptByRSAPublicKey(rsaPublicKey, logPassword));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return DataSourceBuilder.create()
				.driverClassName(logDriverClassName)
				.url(logJdbcUrl)
				.username(logUsername)
				.password(logPassword)
				.build();
	}
	
	@Bean
	@Primary
	public SqlSessionFactory logSqlSessionFactory(@Qualifier("logDataSource") DataSource logDataSource) throws Exception {
		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(logDataSource);
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		sqlSessionFactoryBean.setMapperLocations(resolver.getResources(logLocation));
		return sqlSessionFactoryBean.getObject();
	}
	
}
