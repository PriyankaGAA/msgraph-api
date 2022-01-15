package com.aa.msgraphapi.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.Arrays;

@Configuration
@Slf4j
public class RestTemplateConfig
{
    @Autowired
    CloseableHttpClient httpClient;

    @Bean
    public RestTemplate restTemplate()
    {
	RestTemplate restTemplate = new RestTemplate();
	restTemplate.setRequestFactory(clientHttpRequestFactory());
	restTemplate.setInterceptors(Arrays.asList((httpRequest, bytes, execution) -> {
	    log.info("URI: '{}' HTTP Method: '{}' :: Body: '{}'", httpRequest.getURI(), httpRequest.getMethodValue(), new String(bytes, Charset.forName("UTF-8")));
	    return execution.execute(httpRequest, bytes);
	}));
	return restTemplate;
    }

    @Bean
    public HttpComponentsClientHttpRequestFactory clientHttpRequestFactory()
    {
	HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory();
	clientHttpRequestFactory.setHttpClient(httpClient);
	return clientHttpRequestFactory;
    }
}
