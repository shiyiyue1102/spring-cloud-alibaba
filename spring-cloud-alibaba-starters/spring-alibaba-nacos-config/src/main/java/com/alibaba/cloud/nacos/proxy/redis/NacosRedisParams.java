package com.alibaba.cloud.nacos.proxy.redis;

public class NacosRedisParams {
	String fetchDataId;
	String redisHost;
	String redisPort;
	String username;
	String password;
	String database;
	long timeout;
	int maxActive;
	int maxWait;
	int maxIdle;
	int minIdle;

	long getTimeout() {
		return timeout;
	}

	void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	int getMaxActive() {
		return maxActive;
	}

	void setMaxActive(int maxActive) {
		this.maxActive = maxActive;
	}

	int getMaxWait() {
		return maxWait;
	}

	void setMaxWait(int maxWait) {
		this.maxWait = maxWait;
	}

	int getMaxIdle() {
		return maxIdle;
	}

	void setMaxIdle(int maxIdle) {
		this.maxIdle = maxIdle;
	}

	int getMinIdle() {
		return minIdle;
	}

	void setMinIdle(int minIdle) {
		this.minIdle = minIdle;
	}

	String getFetchDataId() {
		return fetchDataId;
	}

	void setFetchDataId(String fetchDataId) {
		this.fetchDataId = fetchDataId;
	}

	String getRedisHost() {
		return redisHost;
	}

	void setRedisHost(String redisHost) {
		this.redisHost = redisHost;
	}

	String getRedisPort() {
		return redisPort;
	}

	void setRedisPort(String redisPort) {
		this.redisPort = redisPort;
	}

	String getUsername() {
		return username;
	}

	void setUsername(String username) {
		this.username = username;
	}

	String getPassword() {
		return password;
	}

	void setPassword(String password) {
		this.password = password;
	}

	String getDatabase() {
		return database;
	}

	void setDatabase(String database) {
		this.database = database;
	}
}
