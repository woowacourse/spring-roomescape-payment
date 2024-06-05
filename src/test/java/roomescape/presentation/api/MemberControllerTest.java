package roomescape.presentation.api;

import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import roomescape.application.dto.request.SignupRequest;
import roomescape.domain.member.Role;
import roomescape.presentation.BaseControllerTest;

class MemberControllerTest extends BaseControllerTest {

    @Test
    @DisplayName("회원 가입에 성공할 경우 201을 반환한다.")
    void signup() {
        SignupRequest request = new SignupRequest("new@gmail.com", "password", "new");

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/members")
                .then().log().all()
                .assertThat()
                .statusCode(HttpStatus.CREATED.value())
                .header("Location", response1 -> equalTo("/members/" + response1.path("id")))
                .body("id", equalTo(1))
                .body("email", equalTo("new@gmail.com"))
                .body("name", equalTo("new"))
                .body("role", equalTo(Role.USER.name()));
    }
}
