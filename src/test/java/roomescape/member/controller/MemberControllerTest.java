package roomescape.member.controller;

import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.RestClientControllerTest;
import roomescape.auth.token.TokenProvider;
import roomescape.member.dto.SaveMemberRequest;
import roomescape.member.model.MemberRole;

class MemberControllerTest extends RestClientControllerTest {

    @Autowired
    private TokenProvider tokenProvider;

    @DisplayName("전체 회원 정보를 조회한다.")
    @Test
    void getMembersTest() {
        RestAssured.given(spec).log().all()
                .filter(document("findAll-members"))
                .cookie("token", createAdminAccessToken())
                .when().get("/admin/members")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(5));
    }

    @DisplayName("회원을 저장한다.")
    @Test
    void createMember() throws JsonProcessingException {
        final SaveMemberRequest request =
                new SaveMemberRequest("test@mail.cmo", "testPwtest!", "name", MemberRole.USER);
        String requestBody = objectMapper.writeValueAsString(request);

        RestAssured.given(spec).log().all()
                .filter(document("create-member"))
                .cookie("token", createAdminAccessToken())
                .contentType(ContentType.JSON)
                .body(requestBody)
                .when().post("/members")
                .then().log().all()
                .statusCode(200);
    }

    private String createAdminAccessToken() {
        return tokenProvider.createToken(1L, MemberRole.ADMIN);
    }
}
