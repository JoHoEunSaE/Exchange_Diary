package org.johoeunsae.exchangediary.config;

import org.johoeunsae.exchangediary.member.domain.OauthType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter((Converter<String, OauthType>) OauthType::of);
    }
}
