package roomescape.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import roomescape.controller.interceptor.CheckRoleInterceptor;
import roomescape.controller.resolver.LoginMemberArgumentResolver;
import roomescape.service.AuthService;
import roomescape.service.MemberReadService;

import java.util.List;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    private final AuthService authService;
    private final MemberReadService memberReadService;

    public WebMvcConfiguration(AuthService authService, MemberReadService memberReadService) {
        this.authService = authService;
        this.memberReadService = memberReadService;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoginMemberArgumentResolver(authService, memberReadService));
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new CheckRoleInterceptor(authService))
                .addPathPatterns("/admin/**");
    }
}
