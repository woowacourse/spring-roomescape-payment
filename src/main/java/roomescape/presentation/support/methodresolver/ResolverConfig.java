package roomescape.presentation.support.methodresolver;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class ResolverConfig implements WebMvcConfigurer {

    private final AuthInfoArgumentResolver authInfoArgumentResolver;

    public ResolverConfig(final AuthInfoArgumentResolver authInfoArgumentResolver) {
        this.authInfoArgumentResolver = authInfoArgumentResolver;
    }

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authInfoArgumentResolver);
    }
}
