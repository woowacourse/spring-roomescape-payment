package roomescape.e2e;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.documentationConfiguration;
import static roomescape.fixture.IntegrationFixture.ADMIN_EMAIL;
import static roomescape.fixture.IntegrationFixture.FUTURE_DATE_TEXT;
import static roomescape.fixture.IntegrationFixture.PASSWORD;
import static roomescape.fixture.IntegrationFixture.TOKEN;
import static roomescape.fixture.IntegrationFixture.createRegularReservation;
import static roomescape.fixture.IntegrationFixture.createReservationTime;
import static roomescape.fixture.IntegrationFixture.createTheme;
import static roomescape.fixture.IntegrationFixture.findThemesBySize;
import static roomescape.fixture.IntegrationFixture.loginAndGetAuthToken;
import static roomescape.fixture.IntegrationFixture.makeWaitingReservations;
import static roomescape.fixture.TestFixture.FUTURE_DATE;

import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import roomescape.member.presentation.dto.response.MemberWebResponse;
import roomescape.reservation.presentation.dto.response.ConfirmedReservationWebResponse;
import roomescape.reservation.presentation.dto.response.WaitingWebResponse;
import roomescape.reservationslot.presentation.dto.response.ReservationResponse;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestPropertySource(properties = {
        "spring.sql.init.data-locations=classpath:test-data.sql"
})
@ExtendWith(RestDocumentationExtension.class)
public class AdminTest {

    @LocalServerPort
    private int port;

    private RequestSpecification spec;
    private String adminToken;

    @BeforeEach
    void setUp(RestDocumentationContextProvider provider) {
        RestAssured.port = port;
        this.spec = new RequestSpecBuilder().addFilter(documentationConfiguration(provider))
                .build();
        adminToken = loginAndGetAuthToken(ADMIN_EMAIL, PASSWORD);
    }

    @Test
    void accessAdminPage() {
        // given
        String authToken = loginAndGetAuthToken(ADMIN_EMAIL, PASSWORD);

        // when & then
        RestAssured.given(spec).log().all()
                .filter(document("어드민-페이지-조회"))
                .cookie(TOKEN, authToken)
                .when().get("/admin")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    void accessAdminReservationPage() {
        // given
        String authToken = loginAndGetAuthToken(ADMIN_EMAIL, PASSWORD);

        // when & then: 어드민이 예약 페이지에 접속한다.
        RestAssured.given(spec).log().all()
                .filter(document("어드민-예약페이지-조회"))
                .cookie(TOKEN, authToken)
                .when()
                .get("/admin/reservation")
                .then()
                .statusCode(200);
    }

    @Test
    void deleteReservation() {
        // given
        createReservationTime();
        createTheme("추리");
        createRegularReservation(1L);

        RestAssured.given().log().all()
                .cookie(TOKEN, adminToken)
                .when()
                .get("/admin/reservations")
                .then()
                .statusCode(200)
                .body("size()", is(1));

        // when 예약을 삭제한다.
        RestAssured.given(spec).log().all()
                .filter(document(
                        "어드민-예약-삭제",
                        pathParameters(
                                parameterWithName(
                                        "reservationId").description("삭제할 예약 ID")
                        )
                ))
                .cookie(TOKEN, adminToken)
                .when()
                .delete("/admin/reservations/{reservationId}", 1)
                .then()
                .statusCode(204);

        // then
        RestAssured.given().log().all()
                .cookie(TOKEN, adminToken)
                .when()
                .get("/admin/reservations")
                .then()
                .statusCode(200)
                .body("size()", is(0));
    }

    @Test
    void createThemeTest() {
        // given
        String authToken = loginAndGetAuthToken(ADMIN_EMAIL, PASSWORD);

        // when
        Map<String, String> theme = new HashMap<>();
        theme.put("name", "추리");
        theme.put("description", "셜록 with Danny");
        theme.put("thumbnail", "image.png");

        RestAssured.given(spec).log().all()
                .filter(document(
                        "어드민-테마-생성",
                        requestFields(
                                fieldWithPath("name").description("테마명"),
                                fieldWithPath("description").description("설명"),
                                fieldWithPath("thumbnail").description("이미지명")
                        )
                ))
                .contentType(ContentType.JSON)
                .body(theme)
                .cookie(TOKEN, authToken)
                .when().post("/admin/themes")
                .then().log().all()
                .statusCode(201);

        // then
        findThemesBySize(1);
    }

    @Test
    void deleteTheme() {
        // given
        createTheme("추리");
        findThemesBySize(1);

        // when
        RestAssured.given(spec).log().all()
                .filter(document(
                        "어드민-테마-삭제",
                        pathParameters(
                                parameterWithName("id").description("삭제할 테마 ID")
                        )
                ))
                .cookie(TOKEN, adminToken)
                .when().delete("/admin/themes/{id}", 1)
                .then().log().all()
                .statusCode(204);

        // then
        findThemesBySize(0);
    }

    @Test
    void createReservationTimeTest() {
        // when
        createReservationTime();
        // then
        RestAssured.given(spec).log().all()
                .filter(document("어드민-시간-생성"))
                .when().get("/times")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(1));
    }

    @Test
    void deleteReservationTime() {
        // given
        createReservationTime();
        RestAssured.given().log().all()
                .when().get("/times")
                .then().log().all()
                .statusCode(200)
                .body("size()", is(1));

        // when
        RestAssured.given(spec).log().all()
                .filter(document(
                        "어드민-시간-삭제",
                        pathParameters(
                                parameterWithName("id").description("삭제할 시간 ID")
                        )
                )).cookie(TOKEN, adminToken)
                .pathParam("id",1)
                .when().delete("/admin/times/{id}")
                .then().log().all()
                .statusCode(204);
    }

    @Test
    void addReservation() {
        // given
        createReservationTime();
        createTheme("추리");

        // when & then
        Map<String, Object> reservation = new HashMap<>();
        reservation.put("date", FUTURE_DATE_TEXT);
        reservation.put("timeId", 1);
        reservation.put("themeId", 1);
        reservation.put("memberId", 2);

        RestAssured.given(spec).log().all()
                .filter(document(
                        "어드민-예약-생성",
                        requestFields(
                                fieldWithPath("date").description("예약 날짜 (yyyy-MM-dd)"),
                                fieldWithPath("timeId").description("예약 시간 ID"),
                                fieldWithPath("themeId").description("테마 ID"),
                                fieldWithPath("memberId").description("회원 ID")
                        )
                ))
                .contentType(ContentType.JSON)
                .body(reservation)
                .cookie(TOKEN, adminToken)
                .when().post("/admin/reservations")
                .then().log().all()
                .statusCode(201);
    }

    @Test
    void findWaitingReservation() {
        // given
        makeWaitingReservations();

        // when & then: 어드민이 대기 예약 목록을 조회한다.
        List<WaitingWebResponse> responses = RestAssured.given(spec).log().all()
                .filter(document("어드민-대기예약-조회"))
                .contentType(ContentType.JSON)
                .cookie(TOKEN, adminToken)
                .when()
                .get("/admin/waiting-reservations")
                .then()
                .statusCode(200)
                .extract()
                .as(new TypeRef<>() {
                });

        // then
        assertThat(responses.size()).isOne();
    }

    @Test
    void removeWaitingReservation() {
        // given
        ReservationResponse waitingResponse = makeWaitingReservations();

        // when & then: 대기 예약을 삭제한다.
        RestAssured.given(spec).log().all()
                .filter(document(
                        "어드민-대기예약-삭제",
                        pathParameters(
                                parameterWithName("waitingId")
                                        .description("삭제할 대기 예약 ID")
                        )
                ))
                .contentType(ContentType.JSON)
                .cookie(TOKEN, adminToken)
                .pathParam("waitingId", waitingResponse.waitingId())
                .when()
                .delete("/admin/waiting-reservations/{waitingId}")
                .then()
                .statusCode(204);

        List<WaitingWebResponse> responses = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookie(TOKEN, adminToken)
                .when()
                .get("/admin/waiting-reservations")
                .then()
                .statusCode(200)
                .extract()
                .as(new TypeRef<>() {
                });
        assertThat(responses).isEmpty();
    }

    @Test
    void findAllReservations() {
        // given
        createReservationTime();
        createTheme("추리");
        createRegularReservation(1L);

        // when & then: 전체 예약 목록을 조회한다.
        RestAssured.given(spec).log().all()
                .filter(document("어드민-전체예약-조회"))
                .cookie(TOKEN, adminToken)
                .when()
                .get("/admin/reservations")
                .then()
                .statusCode(200)
                .body("size()", is(1));
    }

    @Test
    void filterReservations() {
        // given
        createReservationTime();
        createTheme("추리");
        createTheme("로맨스");
        createRegularReservation(1L);
        createRegularReservation(2L);

        // when & then: 예약 필터링 조회를 한다.
        List<ConfirmedReservationWebResponse> reservationsFilteredByThemeId = RestAssured.given(spec).log().all()
                .filter(document(
                        "어드민-필터예약-조회",
                        queryParameters(
                                parameterWithName("themeId")
                                        .optional().description("테마 ID (필터, optional)"),
                                parameterWithName("memberId")
                                        .optional().description("회원 ID (필터, optional)"),
                                parameterWithName("dateFrom")
                                        .optional().description("예약 시작 날짜 (yyyy-MM-dd, 필터, optional)"),
                                parameterWithName("dateTo")
                                        .optional().description("예약 종료 날짜 (yyyy-MM-dd, 필터, optional)")
                        )
                ))
                .cookie(TOKEN, adminToken)
                .queryParams("themeId", 1L, "memberId", 2L, "dateFrom", FUTURE_DATE_TEXT,
                        "dateTo", FUTURE_DATE.plusDays(1).toString())
                .when()
                .get("/admin/reservations")
                .then()
                .statusCode(200)
                .extract()
                .as(new TypeRef<>() {
                });

        // then
        assertThat(reservationsFilteredByThemeId.size()).isEqualTo(1);
    }


    @Test
    void findAllRegulars() {
        // when & then: 정규 회원 전체를 조회한다.
        List<MemberWebResponse> memberWebRespons = RestAssured.given(spec).log().all()
                .filter(document("어드민-정규회원-조회"))
                .cookie(TOKEN, adminToken)
                .contentType(ContentType.JSON)
                .when()
                .get("/admin/members")
                .then()
                .statusCode(200)
                .extract()
                .as(new TypeRef<>() {
                });
        // then
        assertThat(memberWebRespons.size()).isEqualTo(2);
    }
}
