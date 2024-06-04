package roomescape.api.page;

import io.restassured.RestAssured;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.web.server.LocalServerPort;
import roomescape.api.ApiBaseTest;

class CommonPageApiTest extends ApiBaseTest {

    @LocalServerPort
    int port;

    static List<String> provideCommonUrls() {
        return List.of("/login", "/");
    }

    @ParameterizedTest
    @MethodSource("provideCommonUrls")
    void 공통_페이지는_로그인_하지_않아도_진입_가능(String url) {
        RestAssured
                .given().log().all()
                .port(port)
                .when().get(url)
                .then().log().all()
                .statusCode(200);
    }
}
