package com.last.pay.base.datasource.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:config/jdbc.properties")
public class DataSourceConfig {
	
	@Value("${game.username}")
	private String gameUsername;
	@Value("${game.password}")
	private String gamePassword;
	@Value("${game.driverClassName}")
	private String gameDriverClassName;
	@Value("${game.jdbcUrl}")
	private String gameJdbcUrl;
	
	@Value("${web.username}")
	private String webUsername;
	@Value("${web.password}")
	private String webPassword;
	@Value("${web.driverClassName}")
	private String webDriverClassName;
	@Value("${web.jdbcUrl}")
	private String webJdbcUrl;
	
	@Value("${user.username}")
	private String userUsername;
	@Value("${user.password}")
	private String userPassword;
	@Value("${user.driverClassName}")
	private String userDriverClassName;
	@Value("${user.jdbcUrl}")
	private String userJdbcUrl;
	
	@Value("${log.username}")
	private String logUsername;
	@Value("${log.password}")
	private String logPassword;
	@Value("${log.driverClassName}")
	private String logDriverClassName;
	@Value("${log.jdbcUrl}")
	private String logJdbcUrl;
	
	public String getGameUsername() {
		return gameUsername;
	}
	public void setGameUsername(String gameUsername) {
		this.gameUsername = gameUsername;
	}
	public String getGamePassword() {
		return gamePassword;
	}
	public void setGamePassword(String gamePassword) {
		this.gamePassword = gamePassword;
	}
	public String getGameDriverClassName() {
		return gameDriverClassName;
	}
	public void setGameDriverClassName(String gameDriverClassName) {
		this.gameDriverClassName = gameDriverClassName;
	}
	public String getGameJdbcUrl() {
		return gameJdbcUrl;
	}
	public void setGameJdbcUrl(String gameJdbcUrl) {
		this.gameJdbcUrl = gameJdbcUrl;
	}
	public String getWebUsername() {
		return webUsername;
	}
	public void setWebUsername(String webUsername) {
		this.webUsername = webUsername;
	}
	public String getWebPassword() {
		return webPassword;
	}
	public void setWebPassword(String webPassword) {
		this.webPassword = webPassword;
	}
	public String getWebDriverClassName() {
		return webDriverClassName;
	}
	public void setWebDriverClassName(String webDriverClassName) {
		this.webDriverClassName = webDriverClassName;
	}
	public String getWebJdbcUrl() {
		return webJdbcUrl;
	}
	public void setWebJdbcUrl(String webJdbcUrl) {
		this.webJdbcUrl = webJdbcUrl;
	}
	public String getUserUsername() {
		return userUsername;
	}
	public void setUserUsername(String userUsername) {
		this.userUsername = userUsername;
	}
	public String getUserPassword() {
		return userPassword;
	}
	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}
	public String getUserDriverClassName() {
		return userDriverClassName;
	}
	public void setUserDriverClassName(String userDriverClassName) {
		this.userDriverClassName = userDriverClassName;
	}
	public String getUserJdbcUrl() {
		return userJdbcUrl;
	}
	public void setUserJdbcUrl(String userJdbcUrl) {
		this.userJdbcUrl = userJdbcUrl;
	}
	public String getLogUsername() {
		return logUsername;
	}
	public void setLogUsername(String logUsername) {
		this.logUsername = logUsername;
	}
	public String getLogPassword() {
		return logPassword;
	}
	public void setLogPassword(String logPassword) {
		this.logPassword = logPassword;
	}
	public String getLogDriverClassName() {
		return logDriverClassName;
	}
	public void setLogDriverClassName(String logDriverClassName) {
		this.logDriverClassName = logDriverClassName;
	}
	public String getLogJdbcUrl() {
		return logJdbcUrl;
	}
	public void setLogJdbcUrl(String logJdbcUrl) {
		this.logJdbcUrl = logJdbcUrl;
	}

}
