package com.aa.msgraphapi.config;


import com.aa.msgraphapi.model.Token;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TokenCacheConfig
{
    public static final String TOKEN = "TOKEN";

    @Bean
    public CacheManager ehCacheManager() {
	CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder().withCache(TOKEN,
					CacheConfigurationBuilder.newCacheConfigurationBuilder(	String.class, Token.class,
									ResourcePoolsBuilder.heap(10))).build();
	cacheManager.init();
	return cacheManager;
    }

}
