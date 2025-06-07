package roomescape.config;

import java.util.List;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import roomescape.mock.TestAdminInterceptor;
import roomescape.mock.TestAuthenticationPrincipalArgumentResolver;

@TestConfiguration
public class TestWebMvcConfig implements WebMvcConfigurer {

    @Bean
    public TestAdminInterceptor testAdminInterceptor() {
        return new TestAdminInterceptor();
    }

    @Bean
    public TestAuthenticationPrincipalArgumentResolver testAuthenticationPrincipalArgumentResolver() {
        return new TestAuthenticationPrincipalArgumentResolver();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(testAdminInterceptor());
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(testAuthenticationPrincipalArgumentResolver());
    }
}
