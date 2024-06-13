package roomescape.global.config;

import java.time.Duration;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.ClientHttpRequestFactories;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import roomescape.auth.AdminHandlerInterceptor;
import roomescape.auth.AuthenticatedMemberArgumentResolver;
import roomescape.payment.service.TossPaymentClient;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Value("${roomescape.payment.toss.connection-timeout-duration-of-second}")
    private int connectionTimeoutDurationOfSecond;

    @Value("${roomescape.payment.toss.read-timeout-duration-of-second}")
    private int readTimeoutDurationOfSecond;
    private final AuthenticatedMemberArgumentResolver authenticatedMemberArgumentResolver;
    private final AdminHandlerInterceptor adminHandlerInterceptor;

    public WebMvcConfiguration(AuthenticatedMemberArgumentResolver authenticatedMemberArgumentResolver,
                               AdminHandlerInterceptor adminHandlerInterceptor) {
        this.authenticatedMemberArgumentResolver = authenticatedMemberArgumentResolver;
        this.adminHandlerInterceptor = adminHandlerInterceptor;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authenticatedMemberArgumentResolver);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminHandlerInterceptor)
                .addPathPatterns("/admin/**");
    }

    @Bean
    public TossPaymentClient tossPaymentClient() {
        ClientHttpRequestFactory factory = getClientHttpRequestFactory();

        return new TossPaymentClient(
                RestClient.builder()
                        .requestFactory(factory)
                        .baseUrl("https://api.tosspayments.com")
                        .build()
        );
    }

    private ClientHttpRequestFactory getClientHttpRequestFactory() {
        ClientHttpRequestFactorySettings settings = ClientHttpRequestFactorySettings.DEFAULTS
                .withConnectTimeout(Duration.ofSeconds(connectionTimeoutDurationOfSecond))
                .withReadTimeout(Duration.ofSeconds(readTimeoutDurationOfSecond));

        return ClientHttpRequestFactories.get(JdkClientHttpRequestFactory.class, settings);
    }
}
