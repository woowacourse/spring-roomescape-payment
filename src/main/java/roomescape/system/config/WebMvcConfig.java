package roomescape.system.config;

import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import roomescape.system.auth.interceptor.AdminInterceptor;
import roomescape.system.auth.interceptor.LoginInterceptor;
import roomescape.system.auth.resolver.MemberIdResolver;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final MemberIdResolver memberIdResolver;
    private final AdminInterceptor adminInterceptor;
    private final LoginInterceptor loginInterceptor;

    public WebMvcConfig(MemberIdResolver memberIdResolver, AdminInterceptor adminInterceptor,
                        LoginInterceptor loginInterceptor) {
        this.memberIdResolver = memberIdResolver;
        this.adminInterceptor = adminInterceptor;
        this.loginInterceptor = loginInterceptor;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(memberIdResolver);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminInterceptor);
        registry.addInterceptor(loginInterceptor);
    }
}
