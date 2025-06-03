package roomescape.testconfig;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import roomescape.domain.member.MemberRole;
import roomescape.presentation.support.methodresolver.AuthInfo;
import roomescape.presentation.support.methodresolver.AuthInfoArgumentResolver;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@TestConfiguration
public class TestConfig implements WebMvcConfigurer {

    public static final AuthInfo TESTER = new AuthInfo(9999L, "tester", MemberRole.NORMAL);

    @Bean
    public AuthInfoArgumentResolver mockAuthInfoArgumentResolver() {
        final AuthInfoArgumentResolver mock = mock(AuthInfoArgumentResolver.class);
        when(mock.supportsParameter(any())).thenReturn(true);
        when(mock.resolveArgument(any(), any(), any(), any()))
                .thenReturn(TESTER);
        return mock;
    }

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(mockAuthInfoArgumentResolver());
    }
}
