package roomescape.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import roomescape.config.auth.LoginMemberArgumentResolver;
import roomescape.config.auth.RoleAllowedInterceptor;
import roomescape.config.logging.LoggingInterceptor;
import roomescape.controller.login.AuthCookieHandler;
import roomescape.service.login.LoginService;

import java.util.List;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    private final LoginService loginService;
    private final AuthCookieHandler authCookieHandler;

    public WebMvcConfig(LoginService loginService, AuthCookieHandler authCookieHandler) {
        this.loginService = loginService;
        this.authCookieHandler = authCookieHandler;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RoleAllowedInterceptor(loginService, authCookieHandler));
        registry.addInterceptor(new LoggingInterceptor());
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoginMemberArgumentResolver(loginService, authCookieHandler));
    }
}
