package roomescape.presentation.api;

import static org.hamcrest.Matchers.matchesPattern;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.cookies.CookieDocumentation.responseCookies;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;

import static roomescape.support.docs.DescriptorUtil.ERROR_MESSAGE_DESCRIPTOR;
import static roomescape.support.docs.DescriptorUtil.LOGIN_DESCRIPTOR;
import static roomescape.support.docs.DescriptorUtil.MEMBER_DESCRIPTOR;

import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import roomescape.application.dto.request.LoginRequest;
import roomescape.domain.member.MemberRepository;
import roomescape.fixture.Fixture;
import roomescape.presentation.BaseControllerTest;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
class AuthControllerTest extends BaseControllerTest {

    @Autowired
    private MemberRepository memberRepository;

    private String token;

    private RequestSpecification spec;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        this.spec = new RequestSpecBuilder()
                .addFilter(documentationConfiguration(restDocumentation))
                .build();
    }

    @TestFactory
    @DisplayName("로그인, 로그인 상태 확인, 로그아웃을 한다.")
    Stream<DynamicTest> authControllerTests() {
        memberRepository.save(Fixture.MEMBER_USER);

        return Stream.of(
                DynamicTest.dynamicTest("로그인한다.", this::login),
                DynamicTest.dynamicTest("로그인 상태를 확인한다.", this::checkLogin),
                DynamicTest.dynamicTest("로그아웃한다.", this::logout)
        );
    }

    @Test
    @DisplayName("로그인하지 않으면 로그인 상태를 확인할 수 없다.")
    void checkLoginFailWhenNotLoggedIn() {
        String token = "invalid token";

        RestAssured.given(spec).log().all()
                .accept("application/json")
                .filter(document("auth/login/check/invalid",
                        requestCookies(cookieWithName("token").description("로그인시 응답받은 쿠키값입니다.")),
                        responseFields(ERROR_MESSAGE_DESCRIPTOR)))
                .cookie("token", token)
                .when().get("/login/check")
                .then().log().all()
                .assertThat()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    void login() {
        LoginRequest request = new LoginRequest("user@gmail.com", "abc123");

        ExtractableResponse<Response> response = RestAssured.given(spec).log().all()
                .accept("application/json")
                .filter(document("auth/login",
                        requestFields(LOGIN_DESCRIPTOR[0], LOGIN_DESCRIPTOR[1]),
                        responseFields(MEMBER_DESCRIPTOR),
                        responseCookies(cookieWithName("token").description("로그인 성공 시 받는 쿠키 값입니다."))))
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/login")
                .then().log().all()
                .assertThat()
                .header("Set-Cookie", matchesPattern("^token=.*"))
                .extract();

        token = response.cookie("token");
    }

    void checkLogin() {
        RestAssured.given(spec).log().all()
                .accept("application/json")
                .filter(document("auth/login/check/valid",
                        requestCookies(cookieWithName("token").description("로그인시 응답받은 쿠키값입니다.")),
                        responseFields(MEMBER_DESCRIPTOR)))
                .cookie("token", token)
                .when().get("/login/check")
                .then().log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value());
    }

    void logout() {
        RestAssured.given(spec).log().all()
                .accept("application/json")
                .filter(document("auth/logout",
                        requestCookies(cookieWithName("token").description("로그인시 응답받은 쿠키값입니다."))))
                .cookie("token", token)
                .when().post("/logout")
                .then().log().all()
                .assertThat()
                .statusCode(HttpStatus.OK.value());
    }
}
