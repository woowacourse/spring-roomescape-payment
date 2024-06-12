package roomescape.config;


import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import roomescape.web.support.JwtTokenProperties;

@RequiredArgsConstructor
@EnableConfigurationProperties(JwtTokenProperties.class)
@Configuration
public class JwtTokenConfig {
}
