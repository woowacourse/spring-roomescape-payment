package roomescape.api.page;

import static org.hamcrest.Matchers.containsString;
import static roomescape.TestFixture.ADMIN_EMAIL;
import static roomescape.TestFixture.ADMIN_PASSWORD;
import static roomescape.TestFixture.USER_EMAIL;
import static roomescape.TestFixture.USER_PASSWORD;

import io.restassured.RestAssured;
import io.restassured.http.Cookie;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.boot.test.web.server.LocalServerPort;
import roomescape.api.ApiBaseTest;

class AdminPageApiTest extends ApiBaseTest {

    @LocalServerPort
    int port;

    static List<String> provideAdminUrls() {
        return List.of("/admin", "/admin/reservation", "/admin/time", "admin/theme");
    }

    @ParameterizedTest
    @MethodSource("provideAdminUrls")
    void 로그인이_되어있지_않은_경우_어드민_페이지_진입시_로그인_페이지로_이동(String url) {
        RestAssured
                .given().redirects().follow(false).log().all()
                .port(port)
                .when().get(url)
                .then().log().all()
                .statusCode(302)
                .header("Location", containsString("/login"));
    }

    @ParameterizedTest
    @MethodSource("provideAdminUrls")
    void 로그인_계정의_권한이_관리자가_아닐때_어드민_페이지_진입시_권한없음_응답(String url) {
        Cookie cookieByLogin = getCookieByLogin(port, USER_EMAIL, USER_PASSWORD);

        RestAssured
                .given().log().all()
                .port(port)
                .cookie(cookieByLogin)
                .when().get(url)
                .then().log().all()
                .statusCode(401);
    }

    @ParameterizedTest
    @MethodSource("provideAdminUrls")
    void 로그인_계정의_권한이_관리자이면_어드민_페이지_정상_진입(String url) {
        Cookie cookieByLogin = getCookieByLogin(port, ADMIN_EMAIL, ADMIN_PASSWORD);

        RestAssured
                .given().log().all()
                .port(port)
                .cookie(cookieByLogin)
                .when().get(url)
                .then().log().all()
                .statusCode(200);
    }
}
