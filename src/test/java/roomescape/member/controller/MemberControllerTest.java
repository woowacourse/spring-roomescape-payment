package roomescape.member.controller;

import static org.hamcrest.Matchers.is;

import io.restassured.http.ContentType;
import java.util.Map;
import org.junit.jupiter.api.Test;
import roomescape.IntegrationTest;
import roomescape.TestFixture;

class MemberControllerTest extends IntegrationTest {

    @Test
    void 유저_회원가입() {
        // given
        Map<String, Object> signup = Map.of("name", "newName", "email", "newEmail@naver.com", "password",
                "newPassword");

        givenWithDocs("member-signup-post")
                .contentType(ContentType.JSON)
                .body(signup)
                .when().post("/members")
                .then().log().all()
                .statusCode(201);
    }

    @Test
    void 모든_유저_조회() {
        // given
        dbHelper.insertMember(TestFixture.createMember("멍구", "test1@email.com", "1234"));
        dbHelper.insertMember(TestFixture.createMember("새로이", "test2@email.com", "1234"));

        // when & then
        givenWithDocs("member-get")
                .contentType(ContentType.JSON)
                .when().get("/members")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(2));
    }
}
