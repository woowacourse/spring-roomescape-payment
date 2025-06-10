package roomescape.global.config.web;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import roomescape.auth.web.interceptor.AdminMemberHandlerInterceptor;
import roomescape.auth.web.resolver.AuthenticatedMemberArgumentResolver;
import roomescape.global.interceptor.LogInterceptor;

@RequiredArgsConstructor
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthenticatedMemberArgumentResolver authenticatedMemberArgumentResolver;
    private final AdminMemberHandlerInterceptor adminMemberHandlerInterceptor;
    private final LogInterceptor logInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(logInterceptor)
                .addPathPatterns("/login/**")
                .addPathPatterns("/logout")
                .addPathPatterns("/members/**")
                .addPathPatterns("/times/**")
                .addPathPatterns("/themes/**")
                .addPathPatterns("/reservations/**")
                .addPathPatterns("/admin/**");
        registry.addInterceptor(adminMemberHandlerInterceptor).addPathPatterns("/admin/**");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authenticatedMemberArgumentResolver);
    }
}
