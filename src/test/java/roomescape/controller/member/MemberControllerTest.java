package roomescape.controller.member;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import roomescape.IntegrationTestSupport;

import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static roomescape.controller.doc.DocumentFilter.GET_MEMBERS;
import static roomescape.controller.doc.DocumentFilter.SIGN_UP;
import static roomescape.controller.doc.DocumentFilter.SING_UP_FAIL;

@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
class MemberControllerTest extends IntegrationTestSupport {

    @Autowired
    MemberController memberController;

    @Test
    @DisplayName("회원 조회")
    void getMembers() {
        RestAssured.given(specification).log().all()
                .cookie("token", ADMIN_TOKEN)
                .filter(GET_MEMBERS.getValue())
                .contentType(ContentType.JSON)
                .when().get("/members")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    @DisplayName("회원 가입")
    void createMember() {
        final Map<String, String> params = Map.of("email", "new@mail.com", "password", "486", "name", "zz");

        RestAssured.given(specification).log().all()
                .filter(SIGN_UP.getValue())
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/members")
                .then().log().all()
                .statusCode(201);

        RestAssured.given().log().all()
                .cookie("token", ADMIN_TOKEN)
                .when().get("/members")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(4));
    }

    @Test
    @DisplayName("이미 존재하는 email로 회원가입을 시도할 경우 예외가 발생")
    void duplicateEmail() {
        final Map<String, String> params = Map.of("email", "redddy@gmail.com", "password", "486", "name", "ee");

        RestAssured.given(specification).log().all()
                .filter(SING_UP_FAIL.getValue())
                .contentType(ContentType.JSON)
                .body(params)
                .when().post("/members")
                .then().log().all()
                .statusCode(400);
    }
}
