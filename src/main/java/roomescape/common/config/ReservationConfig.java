package roomescape.common.config;

import java.util.List;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import roomescape.common.argumentResolver.LoginArgumentResolver;
import roomescape.common.interceptor.AdminInterceptor;
import roomescape.common.util.JwtTokenContainer;
import roomescape.common.util.TokenCookieManager;
import roomescape.member.service.LoginService;

@Configuration
public class ReservationConfig implements WebMvcConfigurer {

    private final TokenCookieManager tokenCookieManager;
    private final JwtTokenContainer jwtTokenContainer;

    public ReservationConfig(final TokenCookieManager tokenCookieManager, final JwtTokenContainer jwtTokenContainer) {
        this.tokenCookieManager = tokenCookieManager;
        this.jwtTokenContainer = jwtTokenContainer;
    }

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new LoginArgumentResolver(tokenCookieManager, jwtTokenContainer));
    }

    @Override
    public void addInterceptors(final InterceptorRegistry registry) {
        registry.addInterceptor(new AdminInterceptor(tokenCookieManager, jwtTokenContainer))
                .addPathPatterns("/admin/**");
    }
}
