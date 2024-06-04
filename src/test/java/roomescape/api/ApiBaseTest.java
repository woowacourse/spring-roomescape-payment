package roomescape.api;

import io.restassured.RestAssured;
import io.restassured.http.Cookie;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import roomescape.BaseTest;
import roomescape.dto.login.LoginRequest;

@Sql("/test-data.sql")
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public abstract class ApiBaseTest extends BaseTest {

    public static Cookie getCookieByLogin(int port, String email, String password) {
        return RestAssured
                .given().log().all()
                .port(port)
                .body(new LoginRequest(email, password))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/login")
                .getDetailedCookie("token");
    }
}
