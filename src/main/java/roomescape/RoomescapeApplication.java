package roomescape;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@OpenAPIDefinition(
    info = @Info(
        title = "Roomescape Reservation API",
        version = "v1",
        description = "방탈출 예약 시스템의 API 문서입니다."
    )
)
@SpringBootApplication
public class RoomescapeApplication {

    public static void main(String[] args) {
        SpringApplication.run(RoomescapeApplication.class, args);
    }
}
