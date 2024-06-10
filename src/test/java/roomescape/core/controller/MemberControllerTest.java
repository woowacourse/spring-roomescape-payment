package roomescape.core.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.cookies.CookieDocumentation.responseCookies;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;
import static roomescape.utils.RestDocumentGenerator.documentWithTokenDescription;
import static roomescape.utils.RestDocumentGenerator.memberFieldDescriptors;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import roomescape.core.dto.auth.TokenRequest;
import roomescape.core.dto.member.MemberRequest;
import roomescape.utils.AdminGenerator;
import roomescape.utils.DatabaseCleaner;
import roomescape.utils.TestFixture;

@AcceptanceTest
class MemberControllerTest {
    private static final String EMAIL = TestFixture.getAdminEmail();
    private static final String PASSWORD = TestFixture.getPassword();

    @LocalServerPort
    private int port;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private AdminGenerator adminGenerator;

    private RequestSpecification spec;

    @BeforeEach
    void setUp(RestDocumentationContextProvider restDocumentation) {
        RestAssured.port = port;
        spec = new RequestSpecBuilder().addFilter(documentationConfiguration(restDocumentation))
                .build();

        databaseCleaner.executeTruncate();
        adminGenerator.generate();
    }

    @Test
    @DisplayName("로그인을 수행한다.")
    void login() {
        TokenRequest request = new TokenRequest("test@email.com", "password");

        RestAssured.given(spec).log().all()
                .contentType("application/json")
                .accept("application/json")
                .filter(document("member/do-login/",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestFields(
                                fieldWithPath("email").description("로그인할 유저의 이메일"),
                                fieldWithPath("password").description("로그인할 유저의 비밀번호")
                        ),
                        responseCookies(cookieWithName("token").description("로그인한 유저의 토큰"))))
                .body(request)
                .when().post("/login")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    @DisplayName("인증 정보를 확인한다.")
    void checkLogin() {
        String accessToken = RestAssured
                .given().log().all()
                .body(new TokenRequest(EMAIL, PASSWORD))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/login")
                .then().log().cookies().extract().cookie("token");

        Response response = RestAssured
                .given(spec).log().all()
                .cookies("token", accessToken)
                .accept("application/json")
                .filter(document("member/check-member-status/",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestCookies(cookieWithName("token").description("로그인한 유저의 토큰")),
                        responseFields(memberFieldDescriptors())))
                .when().get("/login/check")
                .then().log().all()
                .statusCode(200).extract().response();

        assertThat(response.getBody().jsonPath().getString("name")).isEqualTo("리건");
    }

    @Test
    @DisplayName("로그아웃을 수행한다.")
    void logout() {
        String accessToken = RestAssured
                .given().log().all()
                .body(new TokenRequest(EMAIL, PASSWORD))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/login")
                .then().log().cookies().extract().cookie("token");

        RestAssured.given(spec).log().all()
                .cookie("token", accessToken)
                .accept("application/json")
                .filter(documentWithTokenDescription("member/do-logout/", "로그인한 유저의 토큰"))
                .when().post("/logout")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    @DisplayName("회원 가입을 수행한다.")
    void signup() {
        final MemberRequest request = new MemberRequest("hello@email.com", "password", "test");

        RestAssured.given(spec).log().all()
                .contentType("application/json")
                .body(request)
                .accept("application/json")
                .filter(document("member/do-signup/",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        requestFields(
                                fieldWithPath("name").description("회원 가입할 유저의 이름"),
                                fieldWithPath("email").description("회원 가입할 유저의 이메일"),
                                fieldWithPath("password").description("회원 가입할 유저의 비밀번호")
                        ),
                        responseFields(memberFieldDescriptors())))
                .when().post("/members")
                .then().log().all()
                .statusCode(201);
    }

    @Test
    @DisplayName("모든 회원 정보를 조회한다.")
    void findMembers() {
        RestAssured.given(spec).log().all()
                .accept("application/json")
                .filter(document("member/find-all-members/",
                        Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
                        Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
                        responseFields(
                                fieldWithPath("[]").description("모든 회원 목록"))
                                .andWithPrefix("[].", memberFieldDescriptors())))
                .when().get("/members")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    @DisplayName("이미 가입되어 있는 이메일로 가입하면 예외가 발생한다.")
    void signupWithDuplicatedEmail() {
        final MemberRequest request = new MemberRequest("test@email.com", "password", "test");

        RestAssured.given().log().all()
                .contentType("application/json")
                .body(request)
                .when().post("/members")
                .then().log().all()
                .statusCode(400);
    }
}
