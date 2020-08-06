package wooteco.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import wooteco.security.web.UserInfoPrincipalArgumentResolver;

import java.util.List;

@Configuration
public class UserInfoPrincipalConfig implements WebMvcConfigurer {
    @Override
    public void addArgumentResolvers(List argumentResolvers) {
        argumentResolvers.add(createUserInfoPrincipalArgumentResolver());
    }

    @Bean
    public UserInfoPrincipalArgumentResolver createUserInfoPrincipalArgumentResolver() {
        return new UserInfoPrincipalArgumentResolver();
    }
}
