package roomescape.common.config;

import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import roomescape.common.security.application.AuthService;
import roomescape.common.security.infrastructure.AuthorizationExtractor;
import roomescape.common.security.infrastructure.JwtProvider;
import roomescape.common.security.interceptor.ApiLoggingInterceptor;
import roomescape.common.security.interceptor.RoleInterceptor;
import roomescape.common.security.resolver.MemberInfoArgumentResolver;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthService authService;
    private final AuthorizationExtractor authorizationExtractor;
    private final JwtProvider jwtProvider;

    public WebMvcConfig(
            AuthService authService,
            AuthorizationExtractor authorizationExtractor,
            JwtProvider jwtProvider
    ) {
        this.authService = authService;
        this.authorizationExtractor = authorizationExtractor;
        this.jwtProvider = jwtProvider;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new MemberInfoArgumentResolver(authService, authorizationExtractor));
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RoleInterceptor(authorizationExtractor, jwtProvider));
        registry.addInterceptor(new ApiLoggingInterceptor())
                .excludePathPatterns(
                        "/actuator/health",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/img/**",
                        "/favicon.ico",
                        "/error",
                        "/**/*.html"
                );
    }
}
