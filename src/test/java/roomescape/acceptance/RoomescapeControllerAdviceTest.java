package roomescape.acceptance;

import static roomescape.fixture.MemberFixture.MEMBER_ARU;

import io.restassured.RestAssured;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RoomescapeControllerAdviceTest extends AcceptanceTest {

    @Test
    @DisplayName("권한이 없는 사용자가 admin 페이지에 접근한다.")
    void unAuthorizedMemberTest() {
        fixture.registerMember(MEMBER_ARU.registerRequest());
        String token = fixture.loginAndGetToken(MEMBER_ARU.loginRequest());

        RestAssured.given().log().all()
                .cookie("token", token)
                .when().get("/admin")
                .then().log().all()
                .statusCode(403);
    }
}
