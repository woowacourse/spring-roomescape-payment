package roomescape.reservation.ui;

import static org.hamcrest.Matchers.is;
import static roomescape.fixture.ui.LoginApiFixture.adminLoginAndGetCookies;
import static roomescape.fixture.ui.MemberApiFixture.signUpMembers;
import static roomescape.fixture.ui.ReservationTimeApiFixture.createReservationTimes;
import static roomescape.fixture.ui.ThemeApiFixture.createThemes;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator.ReplaceUnderscores;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import roomescape.member.ui.dto.MemberResponse;
import roomescape.reservation.ui.dto.request.CreateBookedReservationRequest;
import roomescape.reservation.ui.dto.request.CreateWaitingRequest;
import roomescape.reservation.ui.dto.response.ReservationResponse;
import roomescape.reservation.ui.dto.response.ReservationTimeResponse;
import roomescape.theme.ui.dto.ThemeResponse;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@DisplayNameGeneration(ReplaceUnderscores.class)
@DisplayName("관리자 예약 대기 관리 API 테스트")
class AdminWaitingRestControllerTest {

    private LocalDate tomorrow;
    private List<ReservationTimeResponse> createReservationTimeResponses;
    private List<ThemeResponse> createThemeResponses;
    private List<MemberResponse> createMemberResponses;
    private List<ReservationResponse> createReservationResponses;

    @BeforeEach
    void setUp() {
        final Map<String, String> adminCookies = adminLoginAndGetCookies();

        tomorrow = LocalDate.now().plusDays(1);
        // 예약 시간 추가 (3개)
        createReservationTimeResponses = createReservationTimes(adminCookies, 3);
        // 테마 추가 (1개)
        createThemeResponses = createThemes(adminCookies, 1);
        // 회원 추가 (3명)
        createMemberResponses = signUpMembers(3);
        // 예약 추가 (2개)
        createReservationResponses = createReservations(adminCookies);
    }

    @Test
    void 예약_대기를_추가한다() {
        final Map<String, String> adminCookies = adminLoginAndGetCookies();
        final Long reservationTimeId = createReservationResponses.get(0).time().id();
        final Long themeId = createReservationResponses.get(0).theme().id();
        final Long memberId = createMemberResponses.get(2).id();
        final CreateWaitingRequest createWaitingRequest
                = new CreateWaitingRequest(tomorrow, reservationTimeId, themeId, memberId);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookies(adminCookies)
                .body(createWaitingRequest)
                .when().post("/admin/waitings")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value());
    }

    @Test
    void 예약_대기를_삭제한다() {
        final Map<String, String> adminCookies = adminLoginAndGetCookies();
        final Long reservationTimeId = createReservationResponses.get(0).time().id();
        final Long themeId = createReservationResponses.get(0).theme().id();
        final Long memberId = createMemberResponses.get(1).id();
        final CreateWaitingRequest createWaitingRequest
                = new CreateWaitingRequest(tomorrow, reservationTimeId, themeId, memberId);

        // 예약 대기 추가
        final Integer waitingId = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookies(adminCookies)
                .body(createWaitingRequest)
                .when().post("/admin/waitings")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract().path("id");

        // 예약 대기 삭제
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookies(adminCookies)
                .when().delete("/admin/waitings/{id}", waitingId)
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    void 예약_대기_목록을_조회한다() {
        final Map<String, String> adminCookies = adminLoginAndGetCookies();
        final Long reservationTimeId = createReservationResponses.get(0).time().id();
        final Long themeId = createReservationResponses.get(0).theme().id();
        final Long memberId = createMemberResponses.get(2).id();
        final CreateWaitingRequest createWaitingRequest1
                = new CreateWaitingRequest(tomorrow, reservationTimeId, themeId, memberId);

        // 예약 대기 추가
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookies(adminCookies)
                .body(createWaitingRequest1)
                .when().post("/admin/waitings")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value());

        final Long reservationTimeId2 = createReservationResponses.get(1).time().id();
        final Long themeId2 = createReservationResponses.get(1).theme().id();
        final Long memberId2 = createMemberResponses.get(2).id();
        final CreateWaitingRequest createWaitingRequest2
                = new CreateWaitingRequest(tomorrow, reservationTimeId2, themeId2, memberId2);

        // 예약 대기 추가
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookies(adminCookies)
                .body(createWaitingRequest2)
                .when().post("/admin/waitings")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value());

        // 예약 대기 목록 조회
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookies(adminCookies)
                .when().get("/admin/waitings")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .body("size()", is(2));
    }

    private List<ReservationResponse> createReservations(final Map<String, String> adminCookies) {
        // member0의 예약
        final CreateBookedReservationRequest request1 = new CreateBookedReservationRequest(
                tomorrow,
                createReservationTimeResponses.get(0).id(),
                createThemeResponses.get(0).id(),
                createMemberResponses.get(0).id()
        );
        // member1의 예약
        final CreateBookedReservationRequest request2 = new CreateBookedReservationRequest(
                tomorrow,
                createReservationTimeResponses.get(1).id(),
                createThemeResponses.get(0).id(),
                createMemberResponses.get(0).id()
        );

        final ReservationResponse response1 = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookies(adminCookies)
                .body(request1)
                .when().post("/admin/reservations")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract().as(ReservationResponse.class);

        final ReservationResponse response2 = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .cookies(adminCookies)
                .body(request2)
                .when().post("/admin/reservations")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract().as(ReservationResponse.class);

        return List.of(response1, response2);
    }
} 
