package roomescape.core.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import roomescape.core.dto.auth.TokenRequest;
import roomescape.core.dto.member.MemberRequest;
import roomescape.core.dto.member.MemberResponse;
import roomescape.utils.AdminGenerator;
import roomescape.utils.DatabaseCleaner;
import roomescape.utils.DocumentHelper;
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

    private RequestSpecification specification;

    @BeforeEach
    void setUp(final RestDocumentationContextProvider restDocumentation) {
        RestAssured.port = port;

        specification = DocumentHelper.specification(restDocumentation);

        databaseCleaner.executeTruncate();
        adminGenerator.generate();
    }

    @AfterEach
    void tearDown() {
        databaseCleaner.executeTruncate();
    }

    @Test
    @DisplayName("예약 페이지로 이동한다.")
    void moveToReservationPage() {
        RestAssured.given(this.specification).log().all()
                .filter(document("member-reservation-view"))
                .when().get("/reservation")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    @DisplayName("로그인 페이지로 이동한다.")
    void moveToLoginPage() {
        RestAssured.given(this.specification).log().all()
                .filter(document("member-login-view"))
                .when().get("/login")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    @DisplayName("로그인을 수행한다.")
    void login() {
        TokenRequest request = new TokenRequest("test@email.com", "password");

        RestAssured.given(this.specification).log().all()
                .contentType("application/json")
                .filter(document("member-login",
                        requestFields(fieldWithPath("email").description("로그인 이메일"),
                                fieldWithPath("password").description("로그인 비밀번호"))))
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

        MemberResponse user = RestAssured
                .given(this.specification).log().all()
                .cookies("token", accessToken)
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .filter(document("member-login-check",
                        responseFields(fieldWithPath("id").description("로그인 된 사용자 ID"),
                                fieldWithPath("name").description("로그인 된 사용자 이름"))))
                .when().get("/login/check")
                .then().log().all()
                .statusCode(200).extract().as(MemberResponse.class);

        assertThat(user.getName()).isEqualTo("리건");
    }

    @Test
    @DisplayName("로그아웃을 수행한다.")
    void logout() {
        RestAssured.given(this.specification).log().all()
                .filter(document("member-logout"))
                .when().post("/logout")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    @DisplayName("회원 가입 페이지로 이동한다.")
    void moveToSignupPage() {
        RestAssured.given(this.specification).log().all()
                .filter(document("member-signup-view"))
                .when().get("/signup")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    @DisplayName("회원 가입을 수행한다.")
    void signup() {
        final MemberRequest request = new MemberRequest("hello@email.com", "password", "test");

        RestAssured.given(this.specification).log().all()
                .contentType("application/json")
                .filter(document("member-signup",
                        requestFields(fieldWithPath("email").description("사용자 이메일"),
                                fieldWithPath("password").description("사용자 비밀번호"),
                                fieldWithPath("name").description("사용자 이름")),
                        responseFields(fieldWithPath("id").description("회원가입된 사용자 ID"),
                                fieldWithPath("name").description("회원가입된 사용자 이름"))))
                .body(request)
                .when().post("/members")
                .then().log().all()
                .statusCode(201);
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

    @Test
    @DisplayName("모든 회원 정보를 조회한다.")
    void findMembers() {
        RestAssured.given(this.specification).log().all()
                .filter(document("members",
                        responseFields(fieldWithPath("[].id").description("사용자 ID"),
                                fieldWithPath("[].name").description("사용자 이름"))))
                .when().get("/members")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    @DisplayName("로그인된 회원의 예약 목록 조회 페이지로 이동한다.")
    void findMyReservation() {
        RestAssured.given().log().all()
                .when().get("/reservation-mine")
                .then().log().all()
                .statusCode(200);
    }
}
