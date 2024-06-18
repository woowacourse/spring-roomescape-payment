package roomescape;

import org.springframework.boot.Banner.Mode;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class RoomescapeApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(RoomescapeApplication.class);
        springApplication.setBannerMode(Mode.OFF);
        springApplication.run();
    }
}
