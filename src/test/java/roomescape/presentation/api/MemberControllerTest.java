package roomescape.presentation.api;

import static org.hamcrest.Matchers.equalTo;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestBody;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;

import static roomescape.support.docs.DescriptorUtil.LOGIN_DESCRIPTOR;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import roomescape.application.dto.request.SignupRequest;
import roomescape.domain.member.Role;
import roomescape.presentation.BaseControllerTest;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
class MemberControllerTest extends BaseControllerTest {

    private RequestSpecification spec;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        this.spec = new RequestSpecBuilder()
                .addFilter(documentationConfiguration(restDocumentation))
                .build();
    }


    @Test
    @DisplayName("회원 가입에 성공할 경우 201을 반환한다.")
    void signup() {
        SignupRequest request = new SignupRequest("new@gmail.com", "password", "new");

        RestAssured.given(this.spec).log().all()
                .accept("application/json")
                .filter(document("member/sign-in",
                        requestBody(),
                        requestFields(LOGIN_DESCRIPTOR)))
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/members")
                .then().log().all()
                .assertThat()
                .statusCode(HttpStatus.CREATED.value())
                .header("Location", response -> equalTo("/members/" + response.path("id")))
                .body("id", equalTo(1))
                .body("email", equalTo("new@gmail.com"))
                .body("name", equalTo("new"))
                .body("role", equalTo(Role.USER.name()));
    }
}
