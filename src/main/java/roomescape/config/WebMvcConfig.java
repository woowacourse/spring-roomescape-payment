package roomescape.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import roomescape.config.auth.RoleAllowedInterceptor;
import roomescape.config.logging.LoggingInterceptor;
import roomescape.controller.auth.LoginMemberArgumentResolver;
import roomescape.controller.login.CookieExtractor;
import roomescape.service.login.LoginService;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    private final LoginService loginService;
    private final CookieExtractor cookieExtractor;

    public WebMvcConfig(LoginService loginService, CookieExtractor cookieExtractor) {
        this.loginService = loginService;
        this.cookieExtractor = cookieExtractor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RoleAllowedInterceptor(loginService, cookieExtractor));
        registry.addInterceptor(new LoggingInterceptor());
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoginMemberArgumentResolver(loginService, cookieExtractor));
    }
}
