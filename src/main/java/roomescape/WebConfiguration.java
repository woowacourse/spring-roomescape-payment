package roomescape;

import auth.JwtUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import roomescape.member.AdminInterceptor;
import roomescape.member.LoginMemberArgumentResolver;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {
    private static final int CONNECT_TIMEOUT_MAX = 3000;
    private static final int READ_TIMEOUT_MAX = 3000;
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginMemberArgumentResolver(jwtUtils()));
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminInterceptor(jwtUtils()))
                .addPathPatterns("/admin/**");
    }

    @Bean
    public LoginMemberArgumentResolver loginMemberArgumentResolver(JwtUtils jwtUtils) {
        return new LoginMemberArgumentResolver(jwtUtils);
    }

    @Bean
    public AdminInterceptor adminInterceptor(JwtUtils jwtUtils) {
        return new AdminInterceptor(jwtUtils);
    }

    @Bean
    public JwtUtils jwtUtils() {
        return new JwtUtils();
    }

    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory httpRequestFactory = new SimpleClientHttpRequestFactory();
        httpRequestFactory.setConnectTimeout(CONNECT_TIMEOUT_MAX);
        httpRequestFactory.setReadTimeout(READ_TIMEOUT_MAX);
        return new RestTemplate(httpRequestFactory);
    }
}
