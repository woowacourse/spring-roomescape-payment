package roomescape.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import roomescape.auth.service.PasswordEncoder;
import roomescape.auth.service.bcrypt.BcryptPasswordEncoder;
import roomescape.global.util.Base64Encoder;
import roomescape.global.util.Encoder;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BcryptPasswordEncoder();
    }
    @Bean
    public Encoder keyEncoder(){
        return new Base64Encoder();
    }
}
