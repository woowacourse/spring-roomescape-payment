package roomescape.config;

import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import roomescape.controller.AuthAdminInterceptor;
import roomescape.controller.AuthArgumentResolver;
import roomescape.controller.LoggingInterceptor;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    private final AuthAdminInterceptor authAdminInterceptor;
    private final AuthArgumentResolver authArgumentResolver;
    private final LoggingInterceptor loggingInterceptor;

    public WebMvcConfiguration(final AuthAdminInterceptor authAdminInterceptor,
                               final AuthArgumentResolver authArgumentResolver, LoggingInterceptor loggingInterceptor) {
        this.authAdminInterceptor = authAdminInterceptor;
        this.authArgumentResolver = authArgumentResolver;
        this.loggingInterceptor = loggingInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authAdminInterceptor)
                .addPathPatterns("/admin/**");
        registry.addInterceptor(loggingInterceptor);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authArgumentResolver);
    }
}
