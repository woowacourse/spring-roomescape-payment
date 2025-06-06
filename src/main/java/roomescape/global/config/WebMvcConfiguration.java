package roomescape.global.config;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import roomescape.auth.config.AuthCheckInterceptor;
import roomescape.auth.config.AuthenticationPrincipalArgumentResolver;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfiguration implements WebMvcConfigurer {

    private final AuthenticationPrincipalArgumentResolver authPrincipalResolver;
    private final AuthCheckInterceptor authCheckInterceptor;

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authPrincipalResolver);
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(authCheckInterceptor)
                .addPathPatterns("/admin/**");
    }
}
