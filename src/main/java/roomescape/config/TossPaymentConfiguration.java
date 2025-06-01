package roomescape.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import roomescape.reservation.client.TossPaymentProperties;

@Configuration
@EnableConfigurationProperties(TossPaymentProperties.class)
public class TossPaymentConfiguration {
}
