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

@Configuration
@MapperScan(basePackages = "com.last.pay.core.db.mapper.gamedb",sqlSessionFactoryRef = "gameSqlSessionFactory")
public class GameDataSourceConfiguration {
	
	@Value("${datasource.game.location}")
	private String location;
	@Value("${db.encrypt}")
	private Boolean encrypt;
	@Value("${publicKey}")
	private String publicKey;
	
	@Autowired
	private DataSourceConfig dataSourceConfig; 
	
	@Bean
	public DataSource gameDataSource() {
		String gameDriverClassName = dataSourceConfig.getGameDriverClassName();
		String gameJdbcUrl = dataSourceConfig.getGameJdbcUrl();
		String gameUsername = dataSourceConfig.getGameUsername();
		String gamePassword = dataSourceConfig.getGamePassword();
		if(encrypt) {
			try {
				RSAPublicKey rsaPublicKey = EncryptUtil.getRSAPublicKey(publicKey);
				gameDriverClassName = EncryptUtil.bytesToString(EncryptUtil.decryptByRSAPublicKey(rsaPublicKey, gameDriverClassName));
				gameJdbcUrl = EncryptUtil.bytesToString(EncryptUtil.decryptByRSAPublicKey(rsaPublicKey, gameJdbcUrl));
				gameUsername = EncryptUtil.bytesToString(EncryptUtil.decryptByRSAPublicKey(rsaPublicKey, gameUsername));
				gamePassword = EncryptUtil.bytesToString(EncryptUtil.decryptByRSAPublicKey(rsaPublicKey, gamePassword));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return DataSourceBuilder.create()
				.driverClassName(gameDriverClassName)
				.url(gameJdbcUrl)
				.username(gameUsername)
				.password(gamePassword)
				.build();
	}
	
	@Bean
	public SqlSessionFactory gameSqlSessionFactory(@Qualifier("gameDataSource") DataSource gameDataSource) throws Exception {
		SqlSessionFactoryBean sqlSessionFactoryBean = new SqlSessionFactoryBean();
		sqlSessionFactoryBean.setDataSource(gameDataSource);
		PathMatchingResourcePatternResolver pathMatchingResourcePatternResolver = new PathMatchingResourcePatternResolver();
		sqlSessionFactoryBean.setMapperLocations(pathMatchingResourcePatternResolver.getResources(location));
		return sqlSessionFactoryBean.getObject();
	}
}
