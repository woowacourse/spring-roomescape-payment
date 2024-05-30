package roomescape;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import roomescape.infrastructure.payment.TossPaymentProperties;

@SpringBootApplication
@ConfigurationPropertiesScan(basePackageClasses = TossPaymentProperties.class)
public class RoomescapeApplication {
    public static void main(String[] args) {
        SpringApplication.run(RoomescapeApplication.class, args);
    }

}
