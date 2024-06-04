package roomescape.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import roomescape.auth.AuthenticationExtractor;
import roomescape.auth.AuthenticationPrincipalArgumentResolver;
import roomescape.auth.CheckAdminInterceptor;
import roomescape.auth.CheckLoginInterceptor;
import roomescape.service.AuthService;

import java.util.List;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    private final AuthService authService;
    private final AuthenticationExtractor authenticationExtractor;

    public WebMvcConfiguration(AuthService authService, AuthenticationExtractor authenticationExtractor) {
        this.authService = authService;
        this.authenticationExtractor = authenticationExtractor;
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/signup").setViewName("/signup");
        registry.addViewController("/reservation").setViewName("/reservation");
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new CheckLoginInterceptor(authService, authenticationExtractor))
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/", "/error", "/login", "/signup",
                        "/members", "/themes/popular",
                        "/css/**", "/*.ico", "/js/**", "/image/**");

        registry.addInterceptor(new CheckAdminInterceptor())
                .order(2)
                .addPathPatterns("/admin/**");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new AuthenticationPrincipalArgumentResolver());
    }
}