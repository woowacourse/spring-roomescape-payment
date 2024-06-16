package roomescape.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import roomescape.controller.resolver.LoginMemberArgumentResolver;
import roomescape.interceptor.CheckRoleInterceptor;
import roomescape.service.AuthService;
import roomescape.service.MemberService;

import java.util.List;

@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    private final AuthService authService;
    private final MemberService memberService;

    public WebMvcConfiguration(final AuthService authService, final MemberService memberService) {
        this.authService = authService;
        this.memberService = memberService;
    }

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoginMemberArgumentResolver(authService, memberService));
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(new CheckRoleInterceptor(authService))
                .addPathPatterns("/admin/**");
    }
}
