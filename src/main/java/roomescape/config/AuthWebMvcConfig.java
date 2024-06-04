package roomescape.config;

import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import roomescape.controller.auth.LoginMemberArgumentResolver;
import roomescape.controller.auth.RoleAllowedInterceptor;
import roomescape.controller.login.CookieExtractor;
import roomescape.service.login.LoginService;

@Configuration
public class AuthWebMvcConfig implements WebMvcConfigurer {
    private final LoginService loginService;
    private final CookieExtractor cookieExtractor;

    public AuthWebMvcConfig(LoginService loginService, CookieExtractor cookieExtractor) {
        this.loginService = loginService;
        this.cookieExtractor = cookieExtractor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RoleAllowedInterceptor(loginService, cookieExtractor));
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoginMemberArgumentResolver(loginService, cookieExtractor));
    }
}