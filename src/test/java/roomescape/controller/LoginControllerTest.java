package roomescape.controller;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import roomescape.IntegrationTestSupport;
import roomescape.controller.dto.LoginRequest;
import roomescape.service.dto.MemberResponse;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;

class LoginControllerTest extends IntegrationTestSupport {

    private RequestSpecification specification;

    String accessToken;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        this.specification = new RequestSpecBuilder()
                .addFilter(documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    @DisplayName("로그인")
    void login() {
        RestAssured.given(specification).log().all()
                .filter(document("auth-login"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .body(new LoginRequest(ADMIN_EMAIL, ADMIN_PASSWORD))
                .when().post("/login")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    @DisplayName("로그아웃")
    void logout() {
        String cookie = RestAssured.given(specification).log().all()
                .filter(document("auth-logout"))
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .cookie("token", ADMIN_TOKEN)
                .when().post("/logout")
                .then().log().all()
                .statusCode(200).extract().cookie("token");

        assertThat(cookie).isEmpty();
    }

    @Test
    @DisplayName("로그인 확인")
    void loginCheck() {
        MemberResponse member = RestAssured.given(specification).log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .filter(document("auth-check"))
                .cookie("token", ADMIN_TOKEN)
                .when().get("/login/check")
                .then().log().all()
                .statusCode(200).extract().as(MemberResponse.class);

        assertThat(member.name()).isEqualTo(ADMIN_NAME);
    }

    @DisplayName("토큰으로 로그인 인증한다.")
    @TestFactory
    Stream<DynamicTest> dynamicTestsFromCollection() {
        return Stream.of(
                dynamicTest("이메일, 패스워드로 로그인한다.", () -> {
                    accessToken = RestAssured
                            .given().log().all()
                            .body(new LoginRequest(ADMIN_EMAIL, ADMIN_PASSWORD))
                            .contentType(MediaType.APPLICATION_JSON_VALUE)
                            .accept(MediaType.APPLICATION_JSON_VALUE)
                            .when().post("/login")
                            .then().log().all().extract().cookie("token");
                }),
                dynamicTest("토큰으로 로그인 여부를 확인하여 이름을 받는다.", () -> {
                    MemberResponse member = RestAssured
                            .given().log().all()
                            .cookie("token", accessToken)
                            .accept(MediaType.APPLICATION_JSON_VALUE)
                            .when().get("/login/check")
                            .then().log().all()
                            .statusCode(HttpStatus.OK.value()).extract().as(MemberResponse.class);

                    assertThat(member.name()).isEqualTo(ADMIN_NAME);
                }),
                dynamicTest("로그아웃하면 토큰이 비어있다.", () -> {
                    String cookie = RestAssured
                            .given().log().all()
                            .cookie("token", accessToken)
                            .accept(MediaType.APPLICATION_JSON_VALUE)
                            .when().post("/logout")
                            .then().log().all()
                            .statusCode(HttpStatus.OK.value()).extract().cookie("token");

                    assertThat(cookie).isEmpty();
                })
        );
    }
}
