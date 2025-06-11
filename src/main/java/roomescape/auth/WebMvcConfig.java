package roomescape.auth;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import roomescape.auth.interceptor.AdminApiAuthorizationInterceptor;
import roomescape.auth.interceptor.AdminPageAuthorizationInterceptor;
import roomescape.auth.interceptor.LoggingInterceptor;
import roomescape.infrastructure.JwtTokenProvider;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthorizationExtractor authorizationExtractor;

    public WebMvcConfig(
            JwtTokenProvider jwtTokenProvider,
            AuthorizationExtractor authorizationExtractor
    ) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.authorizationExtractor = authorizationExtractor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AdminPageAuthorizationInterceptor(jwtTokenProvider, authorizationExtractor))
                .addPathPatterns("/admin/**");

        registry.addInterceptor(new AdminApiAuthorizationInterceptor(jwtTokenProvider, authorizationExtractor))
                .addPathPatterns("/api/admin/**");

        registry.addInterceptor(new LoggingInterceptor())
                .addPathPatterns("/**");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new AuthenticationArgumentResolver(jwtTokenProvider, authorizationExtractor));
    }
}
