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
@MapperScan(basePackages = "com.last.pay.core.db.mapper.userdb",sqlSessionFactoryRef = "userSqlSessionFactory")
public class UserDataSourceConfiguration {
	
	@Value("${datasource.user.location}")
	private String userLocation;
	
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
	public DataSource userDataSource() {
		String userDriverClassName = dataSourceConfig.getUserDriverClassName();
		String userJdbcUrl = dataSourceConfig.getUserJdbcUrl();
		String userUsername = dataSourceConfig.getUserUsername();
		String userPassword = dataSourceConfig.getUserPassword();
		if(encrypt) {
			try {
				RSAPublicKey rsaPublicKey = EncryptUtil.getRSAPublicKey(publicKey);
				userDriverClassName = EncryptUtil.bytesToString(EncryptUtil.decryptByRSAPublicKey(rsaPublicKey, userDriverClassName));
				userJdbcUrl = EncryptUtil.bytesToString(EncryptUtil.decryptByRSAPublicKey(rsaPublicKey, userJdbcUrl));
				userUsername = EncryptUtil.bytesToString(EncryptUtil.decryptByRSAPublicKey(rsaPublicKey, userUsername));
				userPassword = EncryptUtil.bytesToString(EncryptUtil.decryptByRSAPublicKey(rsaPublicKey, userPassword));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return DataSourceBuilder.create()
				.driverClassName(userDriverClassName)
				.url(userJdbcUrl)
				.username(userUsername)
				.password(userPassword)
				.build();
	}
	
	@Bean
	public SqlSessionFactory userSqlSessionFactory(@Qualifier("userDataSource") DataSource userDataSource) throws Exception {
		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(userDataSource);
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		sqlSessionFactoryBean.setMapperLocations(resolver.getResources(userLocation));
		return sqlSessionFactoryBean.getObject();
	}
}
