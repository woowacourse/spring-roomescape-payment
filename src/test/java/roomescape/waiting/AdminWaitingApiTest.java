package roomescape.waiting;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyHeaders;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;
import static roomescape.auth.AuthApiTest.TOKEN_COOKIE_NAME;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.jdbc.Sql;
import roomescape.auth.dto.LoginRequest;
import roomescape.waiting.dto.WaitingSimpleResponse;
import roomescape.waiting.repository.WaitingRepository;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@Sql({"/test-time-data.sql", "/test-theme-data.sql", "/test-member-data.sql", "/test-waiting-data.sql"})
@ExtendWith(RestDocumentationExtension.class)
public class AdminWaitingApiTest {

    @LocalServerPort
    private int port;

    public static final String AUTH_COOKIE_NAME = "token";
    private static String ADMIN_TOKEN;
    private RequestSpecification spec;

    @BeforeEach
    void setUp(final RestDocumentationContextProvider restDocumentation) {
        RestAssured.port = port;
        ADMIN_TOKEN = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(new LoginRequest("admin@gmail.com", "1234"))
                .when().post("/login")
                .then().log().all()
                .statusCode(200)
                .extract().cookie(AUTH_COOKIE_NAME);
        this.spec = new RequestSpecBuilder()
                .addFilter(documentationConfiguration(restDocumentation).operationPreprocessors()
                        .withRequestDefaults(modifyHeaders().remove("Foo"), prettyPrint())
                        .withResponseDefaults(prettyPrint()))
                .build();
    }

    @DisplayName("어드민 예약 대기 목록 조회 테스트")
    @Nested
    class FindAllTest {

        @Autowired
        private WaitingRepository waitingRepository;

        @DisplayName("어드민 예약 대기 목록 조회에 성공하면 200을 반환한다.")
        @Test
        void testFindAll() {
            // given
            // when
            WaitingSimpleResponse[] responses = RestAssured.given(spec).log().all()
                    .cookie(AUTH_COOKIE_NAME, ADMIN_TOKEN)
                    .filter(document(
                            "admin/get-waitings",
                            requestCookies(
                                    cookieWithName(TOKEN_COOKIE_NAME).description("인증 토큰")
                            ),
                            responseFields(
                                    fieldWithPath("[].id").description("예약 ID"),
                                    fieldWithPath("[].name").description("예약자 이름"),
                                    fieldWithPath("[].theme").description("테마 이름"),
                                    fieldWithPath("[].date").description("예약 날짜"),
                                    fieldWithPath("[].startAt").description("예약 시작 시간")
                            )
                    ))
                    .when().get("/admin/waitings")
                    .then().log().all()
                    .statusCode(200)
                    .extract()
                    .as(WaitingSimpleResponse[].class);
            // then
            assertThat(responses.length).isEqualTo(waitingRepository.count());
        }

        @DisplayName("인증 정보가 올바르지 않을 경우 401을 반환한다.")
        @Test
        void testUnauthorized() {
            RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .when().get("/admin/waitings")
                    .then().log().all()
                    .statusCode(401);
        }

        @DisplayName("어드민 권한이 없을 경우 403을 반환한다.")
        @Test
        void testForbidden() {
            // given
            String memberToken = RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .body(new LoginRequest("aaa@gmail.com", "1234"))
                    .when().post("/login")
                    .then().log().all()
                    .extract().cookie(AUTH_COOKIE_NAME);
            // when
            // then
            RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .cookie(AUTH_COOKIE_NAME, memberToken)
                    .when().get("/admin/waitings")
                    .then().log().all()
                    .statusCode(403);
        }
    }

    @DisplayName("어드민 예약 대기 거절 테스트")
    @Nested
    class DeleteWaitingTest {

        @Autowired
        private WaitingRepository waitingRepository;

        @DisplayName("어드민 예약 대기 거절 성공하면 204를 반환한다.")
        @Test
        void testDeleteWaiting() {
            RestAssured.given(spec).log().all()
                    .contentType(ContentType.JSON)
                    .cookie(AUTH_COOKIE_NAME, ADMIN_TOKEN)
                    .filter(document(
                            "admin/delete-waiting",
                            requestCookies(
                                    cookieWithName(TOKEN_COOKIE_NAME).description("인증 토큰")
                            )
                    ))
                    .when().delete("/admin/waitings/{id}", 1L)
                    .then().log().all()
                    .statusCode(204);
            assertThat(waitingRepository.count()).isEqualTo(1);
        }

        @DisplayName("인증 정보가 올바르지 않을 경우 401을 반환한다.")
        @Test
        void testUnauthorized() {
            RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .when().delete("/admin/waitings/{id}", 1L)
                    .then().log().all()
                    .statusCode(401);
        }

        @DisplayName("어드민 권한이 없을 경우 403을 반환한다.")
        @Test
        void testForbidden() {
            // given
            String memberToken = RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .body(new LoginRequest("aaa@gmail.com", "1234"))
                    .when().post("/login")
                    .then().log().all()
                    .extract().cookie(AUTH_COOKIE_NAME);
            // when
            // then
            RestAssured.given().log().all()
                    .contentType(ContentType.JSON)
                    .cookie(AUTH_COOKIE_NAME, memberToken)
                    .when().delete("/admin/waitings/{id}", 1L)
                    .then().log().all()
                    .statusCode(403);
        }
    }
}
