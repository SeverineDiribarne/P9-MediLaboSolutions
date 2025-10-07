package com.medilabo.medilabo_gateway.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.format.support.DefaultFormattingConversionService;

@Configuration
public class ConversionConfig {
    @Bean
    public ConversionService conversionService(@Value("${algorithm}")String algorithm){
        DefaultFormattingConversionService conversionService = new DefaultFormattingConversionService();
        StringToSecretKeyConverter converter = new StringToSecretKeyConverter();
        converter.setAlgorithm(algorithm);
        conversionService.addConverter(converter);
        return conversionService;
    }

}
