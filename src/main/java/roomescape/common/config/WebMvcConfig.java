package roomescape.common.config;

import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import roomescape.auth.infrastructure.interceptor.AdminRoleInterceptor;
import roomescape.auth.infrastructure.argumentresolver.LoginAdminArgumentResolver;
import roomescape.auth.infrastructure.argumentresolver.LoginMemberArgumentResolver;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    private final LoginAdminArgumentResolver loginAdminArgumentResolver;
    private final LoginMemberArgumentResolver loginMemberArgumentResolver;
    private final AdminRoleInterceptor adminRoleInterceptor;

    public WebMvcConfig(final LoginAdminArgumentResolver loginAdminArgumentResolver,
                        final LoginMemberArgumentResolver loginMemberArgumentResolver,
                        final AdminRoleInterceptor adminRoleInterceptor)
    {
        this.loginAdminArgumentResolver = loginAdminArgumentResolver;
        this.loginMemberArgumentResolver = loginMemberArgumentResolver;
        this.adminRoleInterceptor = adminRoleInterceptor;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(loginAdminArgumentResolver);
        resolvers.add(loginMemberArgumentResolver);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(adminRoleInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/admin/login");
    }
}
