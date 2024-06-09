package roomescape.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import roomescape.config.properties.TossPaymentConfigProperties;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(initializers = ConfigDataApplicationContextInitializer.class)
@EnableConfigurationProperties(TossPaymentConfigProperties.class)
public class ConfigPropertiesTest {

    @Autowired
    private TossPaymentConfigProperties tossPaymentConfigProperties;

    @Test
    void tossProperties() {
        assertThat(tossPaymentConfigProperties.secret()).isEqualTo("test");
        assertThat(tossPaymentConfigProperties.baseUri()).isEqualTo("http://localhost");
        assertThat(tossPaymentConfigProperties.confirmUri()).isEqualTo("/confirm");
        assertThat(tossPaymentConfigProperties.connectTimeout()).isEqualTo(30);
        assertThat(tossPaymentConfigProperties.readTimeout()).isEqualTo(30);
    }
}
