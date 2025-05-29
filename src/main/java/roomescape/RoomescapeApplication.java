package roomescape;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;

@ConfigurationPropertiesScan
@EnableJpaAuditing
@EnableAsync
@SpringBootApplication
public class RoomescapeApplication {

    public static void main(final String[] args) {
        SpringApplication.run(RoomescapeApplication.class, args);
    }
}
