package roomescape.global.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {
    private final HandlerMethodArgumentResolver argumentResolver;
    private final HandlerInterceptor checkRoleInterceptor;
    private final HandlerInterceptor checkUserInterceptor;

    public WebMvcConfiguration(
            HandlerMethodArgumentResolver authenticationPrincipalArgumentResolver,
            HandlerInterceptor checkRoleInterceptor,
            HandlerInterceptor checkUserInterceptor) {

        this.argumentResolver = authenticationPrincipalArgumentResolver;
        this.checkRoleInterceptor = checkRoleInterceptor;
        this.checkUserInterceptor = checkUserInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(checkRoleInterceptor)
                .addPathPatterns("/admin/**");

        registry.addInterceptor(checkUserInterceptor)
                .addPathPatterns("/reservation")
                .addPathPatterns("/reservations")
                .addPathPatterns("/login/check");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(argumentResolver);
    }

    @Bean
    @Profile("dev")
    public RestClient restClient(@Value("${toss-payment.payment-url}") String PAYMENTS_URL) {
        return RestClient.builder()
                .baseUrl(PAYMENTS_URL)
                .requestFactory(clientHttpRequestFactory())
                .build();
    }

    private ClientHttpRequestFactory clientHttpRequestFactory() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(3000);
        factory.setReadTimeout(10000);

        return factory;
    }
}
