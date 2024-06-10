package roomescape.config;

import java.time.Duration;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import roomescape.infrastructure.AuthorizationHandlerInterceptor;
import roomescape.infrastructure.LoginMemberArgumentResolver;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {
    public static final int REST_CLIENT_READ_TIMEOUT = 3;

    private final LoginMemberArgumentResolver loginMemberArgumentResolver;
    private final AuthorizationHandlerInterceptor authorizationHandlerInterceptor;

    public WebMvcConfiguration(final LoginMemberArgumentResolver loginMemberArgumentResolver,
                               final AuthorizationHandlerInterceptor authorizationHandlerInterceptor) {
        this.loginMemberArgumentResolver = loginMemberArgumentResolver;
        this.authorizationHandlerInterceptor = authorizationHandlerInterceptor;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginMemberArgumentResolver);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authorizationHandlerInterceptor)
                .addPathPatterns("/admin/**");
    }

    @Bean
    @Profile("!test")
    public RestClient restClient() {
        final JdkClientHttpRequestFactory factory = new JdkClientHttpRequestFactory();
        factory.setReadTimeout(Duration.ofSeconds(REST_CLIENT_READ_TIMEOUT));

        return RestClient.builder()
                .requestFactory(factory)
                .baseUrl("https://api.tosspayments.com/v1/payments/confirm")
                .build();
    }
}
