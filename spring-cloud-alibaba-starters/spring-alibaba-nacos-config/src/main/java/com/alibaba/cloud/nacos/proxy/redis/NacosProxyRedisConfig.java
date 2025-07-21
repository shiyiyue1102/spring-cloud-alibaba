package com.alibaba.cloud.nacos.proxy.redis;

import java.io.IOException;
import java.io.StringReader;
import java.util.Properties;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.AbstractListener;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import redis.clients.jedis.DefaultJedisClientConfig;
import redis.clients.jedis.DefaultRedisCredentials;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.RedisCredentials;
import redis.clients.jedis.RedisCredentialsProvider;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value = "spring.nacos.config.proxy.redis.enabled", havingValue = "true")
public class NacosProxyRedisConfig {

	private static final String GROUP = "nacos-redis";

	@Bean
	public DefaultJedisClientConfig defaultJedisClientConfig(NacosRedisParams nacosRedisParams) throws Exception {
		ConfigService configService = NacosConfigManager.getInstance().getConfigService();
		NacosProxyRedisCredentialsProvider redisCredentialsProvider = new NacosProxyRedisCredentialsProvider();

		RedisCredentials redisCredentials = new DefaultRedisCredentials(nacosRedisParams.username, nacosRedisParams.password);
		redisCredentialsProvider.setRedisCredentials(redisCredentials);

		DefaultJedisClientConfig defaultJedisClientConfig = DefaultJedisClientConfig.builder()
				.database(Integer.parseInt(nacosRedisParams.database))
				.credentialsProvider(redisCredentialsProvider).build();
		GenericObjectPoolConfig<Jedis> poolConfig = new GenericObjectPoolConfig<>();
		poolConfig.setJmxEnabled(false); // 启用 JMX
		configService.addListener(nacosRedisParams.fetchDataId, GROUP, new AbstractListener() {
			@Override
			public void receiveConfigInfo(String configInfo) {
				Properties properties = new Properties();
				try {
					properties.load(new StringReader(configInfo));
					String username = (String) properties.get("spring.data.redis.username");
					String password = (String) properties.get("spring.data.redis.password");
					RedisCredentials redisCredentials = new DefaultRedisCredentials(username, password);
					redisCredentialsProvider.setRedisCredentials(redisCredentials);
				}
				catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		});
		return defaultJedisClientConfig;
	}

	@Bean
	public JedisPool nacosProxyRedis(DefaultJedisClientConfig defaultJedisClientConfig,NacosRedisParams nacosRedisParams,RedisConnectionFactory redisConnectionFactory) throws Exception {

		GenericObjectPoolConfig<Jedis> poolConfig = new GenericObjectPoolConfig<>();
		poolConfig.setJmxEnabled(false); // 启用 JMX
		poolConfig.setJmxNamePrefix("nacos-proxy-redis-pool");
		final JedisPool jedisPool = new JedisPool(poolConfig, new HostAndPort(nacosRedisParams.redisHost, Integer.parseInt(nacosRedisParams.redisPort)),
				defaultJedisClientConfig);
		return jedisPool;
	}

	@Bean
	public NacosRedisParams nacosRedisParam(@Value("${spring.nacos.config.proxy.redis.data-id}") String config) throws Exception {
		ConfigService configService = NacosConfigManager.getInstance().getConfigService();
		String redisCredentialsProviderConfig = configService.getConfig(config, GROUP, 3000L);
		NacosRedisParams fromConfig = createFromConfig(redisCredentialsProviderConfig);
		fromConfig.setFetchDataId(config);
		return fromConfig;
	}

	private NacosRedisParams createFromConfig(String config) throws IOException {
		Properties properties = new Properties();
		properties.load(new StringReader(config));
		String redisHost = (String) properties.get("spring.data.redis.host");
		String redisPort = (String) properties.get("spring.data.redis.port");
		String username = (String) properties.get("spring.data.redis.username");
		String password = (String) properties.get("spring.data.redis.password");
		String database = (String) properties.get("spring.data.redis.database");
		NacosRedisParams nacosRedisParams=new NacosRedisParams();
		nacosRedisParams.setRedisHost(redisHost);
		nacosRedisParams.setRedisPort(redisPort);
		nacosRedisParams.setPassword(password);
		nacosRedisParams.setUsername(username);
		nacosRedisParams.setDatabase(database);
		return nacosRedisParams;
	}

}

class NacosProxyRedisCredentialsProvider implements RedisCredentialsProvider {

	protected RedisCredentials redisCredentials;

	@Override
	public RedisCredentials get() {
		return redisCredentials;
	}

	public void setRedisCredentials(RedisCredentials redisCredentialC) {
		this.redisCredentials = redisCredentialC;
	}
}

