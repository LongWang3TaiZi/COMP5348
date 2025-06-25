package com.usyd.backend.configs;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;
import org.apache.hc.core5.util.TimeValue;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

import java.nio.charset.StandardCharsets;
import java.time.Duration;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        // create a customized RestTemplate using RestTemplateBuilder
        return builder
                .requestFactory(this::clientHttpRequestFactory)
                .setConnectTimeout(Duration.ofSeconds(5)) // set connection timeout to 5 seconds
//                .setReadTimeout(Duration.ofSeconds(5)) // set read timeout to 5 seconds
                // add a StringHttpMessageConverter with UTF-8 encoding
                .additionalMessageConverters(new StringHttpMessageConverter(StandardCharsets.UTF_8))
                // add a MappingJackson2HttpMessageConverter for JSON processing
                .additionalMessageConverters(new MappingJackson2HttpMessageConverter())
                // set a default User-Agent header for all requests
                .defaultHeader("User-Agent", "COMP5348/1.0")
                .build();
    }

    @Bean
    public HttpComponentsClientHttpRequestFactory clientHttpRequestFactory() {
        // create a request factory using our custom HttpClient
        return new HttpComponentsClientHttpRequestFactory(httpClient());
    }

    @Bean
    public CloseableHttpClient httpClient() {
        // create a registry of connection socket factories
        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", SSLConnectionSocketFactory.getSocketFactory())
                .build();

        // create a connection manager with the registry
        PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager(registry);
        // set the maximum total connections
        connectionManager.setMaxTotal(100);
        // set the maximum connections per route
        connectionManager.setDefaultMaxPerRoute(20);
        // set the time to wait before validating inactive connections
        connectionManager.setValidateAfterInactivity(TimeValue.ofSeconds(10));

        // build and return the HttpClient with the custom connection manager
        return HttpClients.custom()
                .setConnectionManager(connectionManager)
                .build();
    }
}
