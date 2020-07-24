package com.nineone.nocm.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nineone.nocm.oauth.UserArgumentResolver;
import com.nineone.nocm.xss.HtmlCharacterEscapes;
import lombok.RequiredArgsConstructor;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final UserArgumentResolver userArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(userArgumentResolver);
    }

    @Bean
    public MappingJackson2HttpMessageConverter jsonEscapeConverter() {
        ObjectMapper copy = new ObjectMapper();
        copy.getFactory().setCharacterEscapes(new HtmlCharacterEscapes());
        return new MappingJackson2HttpMessageConverter(copy);
    }

    @Bean
    public RestTemplate restTemplate(){
        try{
            HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
            factory.setReadTimeout(5000);
            factory.setConnectTimeout(3000);

            HttpClient httpClient = HttpClientBuilder.create()
                    .setMaxConnTotal(100)
                    .setMaxConnPerRoute(5)
                    .build();
            factory.setHttpClient(httpClient);
            RestTemplate restTemplate = new RestTemplate(factory);
            return restTemplate;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
