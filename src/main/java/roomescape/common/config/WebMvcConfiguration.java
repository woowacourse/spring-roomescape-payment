package roomescape.common.config;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import roomescape.infrastructure.web.argumentresolver.LoginAdminArgumentResolver;
import roomescape.infrastructure.web.argumentresolver.LoginMemberArgumentResolver;
import roomescape.infrastructure.web.interceptor.AdminRoleInterceptor;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfiguration implements WebMvcConfigurer {

    private final LoginAdminArgumentResolver loginAdminArgumentResolver;
    private final LoginMemberArgumentResolver loginMemberArgumentResolver;
    private final AdminRoleInterceptor adminRoleInterceptor;

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
