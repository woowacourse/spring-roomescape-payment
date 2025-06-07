package roomescape.e2e;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static roomescape.fixture.IntegrationFixture.PASSWORD;
import static roomescape.fixture.IntegrationFixture.REGULAR_EMAIL;
import static roomescape.fixture.IntegrationFixture.TOKEN;
import static roomescape.fixture.IntegrationFixture.createReservationTime;
import static roomescape.fixture.IntegrationFixture.createTheme;
import static roomescape.fixture.IntegrationFixture.findThemesBySize;
import static roomescape.fixture.IntegrationFixture.loginAndGetAuthToken;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import roomescape.common.security.dto.request.LoginRequest;
import roomescape.common.security.dto.response.CheckLoginResponse;
import roomescape.member.presentation.dto.request.SignupWebRequest;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestPropertySource(properties = {
        "spring.sql.init.data-locations=classpath:test-data.sql"
})
@ExtendWith(RestDocumentationExtension.class)
public class GuestTest {

    @LocalServerPort
    private int port;

    private RequestSpecification spec;

    @BeforeEach
    void setUp(RestDocumentationContextProvider provider) {
        RestAssured.port = port;
        this.spec = new RequestSpecBuilder().addFilter(documentationConfiguration(provider))
                .build();
    }

    @Test
    void findAvailableReservations() {
        // given
        createReservationTime();
        createTheme("추리");

        // when
        LocalDate now = LocalDate.now();
        RestAssured.given(spec).log().all()
                .filter(document("게스트-가능한-예약-조회"))
                .when().queryParams("date", now.toString(), "themeId", 1L)
                .get("/times/available")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(1));
    }

    @Test
    void findPopularTheme() {
        // given
        createReservationTime();
        createTheme("추리1");
        createTheme("추리2");
        createTheme("추리3");
        createTheme("추리4");
        createTheme("추리5");
        createTheme("추리6");
        createTheme("추리7");
        createTheme("추리8");
        createTheme("추리9");
        createTheme("추리10");
        createTheme("추리11");
        createTheme("추리12");
        findThemesBySize(12);

        // when
        RestAssured.given(spec).log().all()
                .filter(document("게스트-인기테마-조회"))
                .when().get("/themes/popular")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(10));
    }

    @Test
    void signup() {
        // when
        RestAssured.given(spec).log().all()
                .filter(document(
                        "게스트-회원가입",
                        requestFields(
                                fieldWithPath("email").description("이메일"),
                                fieldWithPath("password").description("비밀번호"),
                                fieldWithPath("name").description("회원 이름")
                        )
                ))
                .body(new SignupWebRequest("testMember@gmail.com", PASSWORD, "testMember"))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .when().post("/members")
                .then().log().all()
                .statusCode(201);

        // then
        loginAndGetAuthToken("testMember@gmail.com", PASSWORD);
    }

    @Test
    void loginCheck() {
        // given
        String regularToken = loginAndGetAuthToken(REGULAR_EMAIL, PASSWORD);
        // when
        CheckLoginResponse checkLoginResponse = RestAssured.given(spec).log().all()
                .filter(document(
                        "게스트-로그인확인",
                        requestFields(
                                fieldWithPath("email").description("이메일"),
                                fieldWithPath("password").description("비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("name").description("회원 이름")
                        )
                ))
                .body(new LoginRequest(REGULAR_EMAIL, PASSWORD))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .cookie(TOKEN, regularToken)
                .when().get("/login/check")
                .then().log().all()
                .statusCode(200)
                .extract()
                .as(CheckLoginResponse.class);

        // then
        assertThat(checkLoginResponse.name()).isEqualTo("Regular");
    }
}
