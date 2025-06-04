package roomescape.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import roomescape.global.AuthInterceptor;
import roomescape.global.LoginMemberArgumentResolver;
import roomescape.global.RequestLoggingInterceptor;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;
    private final RequestLoggingInterceptor requestLoggingInterceptor;
    private final LoginMemberArgumentResolver loginMemberArgumentResolver;

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginMemberArgumentResolver);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/reservation").setViewName("reservation");
        registry.addViewController("/reservation-mine").setViewName("reservation-mine");
        registry.addViewController("/login").setViewName("login");
        registry.addViewController("/signup").setViewName("signup");

        registry.addViewController("/admin").setViewName("admin/index");
        registry.addViewController("/admin/reservation").setViewName("admin/reservation-new");
        registry.addViewController("/admin/time").setViewName("admin/time");
        registry.addViewController("/admin/theme").setViewName("admin/theme");
        registry.addViewController("/admin/waiting").setViewName("admin/waiting");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor).addPathPatterns("/api/admin/**");
        registry.addInterceptor(requestLoggingInterceptor).addPathPatterns("/**").excludePathPatterns("/favicon.ico");
    }
}
