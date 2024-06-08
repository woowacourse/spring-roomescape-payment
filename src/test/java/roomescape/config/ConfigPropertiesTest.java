package roomescape.config;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ConfigPropertiesTest {

    @Autowired
    private PaymentConfigProperties paymentConfigProperties;

    @Test
    void tossProperties() {
        TossPaymentConfigProperties properties = paymentConfigProperties.getTossProperties();

        assertThat(properties.getSecret()).isEqualTo("test");
        assertThat(properties.getBaseUri()).isEqualTo("http://localhost");
        assertThat(properties.getConfirmUri()).isEqualTo("/confirm");
    }
}
