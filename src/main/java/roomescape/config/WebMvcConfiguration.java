package roomescape.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import roomescape.auth.AuthenticationPrincipalArgumentResolver;
import roomescape.auth.AuthorizationAdminInterceptor;
import roomescape.log.LoggingInterceptor;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfiguration implements WebMvcConfigurer {

    private final AuthenticationPrincipalArgumentResolver authenticationPrincipalArgumentResolver;
    private final AuthorizationAdminInterceptor authorizationAdminInterceptor;
    private final LoggingInterceptor loggingInterceptor;

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authenticationPrincipalArgumentResolver);
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(authorizationAdminInterceptor)
                .addPathPatterns("/admin/**");

        registry.addInterceptor(loggingInterceptor)
                .addPathPatterns("/**");
    }
}
