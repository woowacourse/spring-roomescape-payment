package roomescape.controller;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class StaticAdminPageControllerTest extends AbstractControllerTest {

    @DisplayName("관리자 권한이 아니면 403을 반환한다.")
    @Test
    void should_status_403_when_not_admin_role() {
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookie(getMemberCookie())
                .when().get("/admin")
                .then().log().all()
                .statusCode(403);
    }

    @DisplayName("관리자 권한으로 요청하면 200을 응답한다.")
    @Test
    void should_status_200_when_admin_role() {
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookie(getAdminCookie())
                .when().get("/admin")
                .then().log().all()
                .statusCode(200);
    }
}
