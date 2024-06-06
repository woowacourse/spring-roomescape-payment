package roomescape.acceptance;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static roomescape.fixture.MemberFixture.MEMBER_ARU;
import static roomescape.fixture.MemberFixture.MEMBER_PK;
import static roomescape.fixture.ThemeFixture.TEST_THEME;

import java.time.LocalDate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.cookies.CookieDescriptor;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.restdocs.restassured.RestDocumentationFilter;
import roomescape.application.reservation.dto.request.ReservationRequest;
import roomescape.application.reservation.dto.response.ReservationWaitingResponse;

class ReservationWaitingAcceptanceTest extends AcceptanceTest {

    @Test
    @DisplayName("이미 예약된 곳에 대기한다.")
    void enqueueWaitList() {
        long aruId = fixture.registerMember(MEMBER_ARU.registerRequest()).id();
        fixture.registerMember(MEMBER_PK.registerRequest());
        long themeId = fixture.createTheme(TEST_THEME.request()).id();
        long timeId = fixture.createReservationTime(10, 0).id();
        LocalDate date = LocalDate.of(2024, 12, 25);

        String aruToken = fixture.loginAndGetToken(MEMBER_ARU.loginRequest());
        String pkToken = fixture.loginAndGetToken(MEMBER_PK.loginRequest());
        fixture.createReservation(aruToken, new ReservationRequest(aruId, themeId, date, timeId));

        String request = """
                {
                    "themeId": %d,
                    "date": "%s",
                    "timeId": %d
                }
                """.formatted(themeId, date, timeId);

        CookieDescriptor[] cookieDescriptors = {
                cookieWithName("token").description("인증 토큰")
        };

        FieldDescriptor[] requestFieldDescriptors = {
                fieldWithPath("themeId").description("테마 ID"),
                fieldWithPath("date").description("예약 날짜"),
                fieldWithPath("timeId").description("예약 시간 ID")
        };

        FieldDescriptor[] responseFieldDescriptors = {
                fieldWithPath("reservation.id").description("예약 ID"),
                fieldWithPath("reservation.member").description("대기자 이름"),
                fieldWithPath("reservation.theme").description("테마 이름"),
                fieldWithPath("reservation.date").description("예약 날짜"),
                fieldWithPath("reservation.startAt").description("시작 시간"),
                fieldWithPath("waitingCount").description("대기자 수")
        };

        RestDocumentationFilter docsFilter = document(
                "reservation-waiting-enqueue",
                requestCookies(cookieDescriptors),
                requestFields(requestFieldDescriptors),
                responseFields(responseFieldDescriptors)
        );

        givenWithSpec().log().all()
                .cookie("token", pkToken)
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .filter(docsFilter)
                .body(request)
                .when().post("/reservations/queue")
                .then().log().all()
                .statusCode(200);
    }

    @Test
    @DisplayName("이미 예약한 곳에 대기할 수 없다.")
    void duplicateWaiting() {
        long aruId = fixture.registerMember(MEMBER_ARU.registerRequest()).id();
        long themeId = fixture.createTheme(TEST_THEME.request()).id();
        long timeId = fixture.createReservationTime(10, 0).id();
        LocalDate date = LocalDate.of(2024, 12, 25);

        String aruToken = fixture.loginAndGetToken(MEMBER_ARU.loginRequest());
        fixture.createReservation(aruToken, new ReservationRequest(aruId, themeId, date, timeId));
        String request = """
                {
                    "themeId": %d,
                    "date": "%s",
                    "timeId": %d
                }
                """.formatted(themeId, date, timeId);

        CookieDescriptor[] cookieDescriptors = {
                cookieWithName("token").description("인증 토큰")
        };

        FieldDescriptor[] requestFieldDescriptors = {
                fieldWithPath("themeId").description("테마 ID"),
                fieldWithPath("date").description("예약 날짜"),
                fieldWithPath("timeId").description("예약 시간 ID")
        };

        RestDocumentationFilter docsFilter = document(
                "reservation-waiting-enqueue-already-reserved",
                requestCookies(cookieDescriptors),
                requestFields(requestFieldDescriptors)
        );

        givenWithSpec().log().all()
                .cookie("token", aruToken)
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .filter(docsFilter)
                .body(request)
                .when().post("/reservations/queue")
                .then().log().all()
                .statusCode(400);
    }

    @Test
    @DisplayName("예약 대기를 취소한다.")
    void dequeueWaitList() {
        long aruId = fixture.registerMember(MEMBER_ARU.registerRequest()).id();
        fixture.registerMember(MEMBER_PK.registerRequest());
        long themeId = fixture.createTheme(TEST_THEME.request()).id();
        long timeId = fixture.createReservationTime(10, 0).id();
        LocalDate date = LocalDate.of(2024, 12, 25);

        String aruToken = fixture.loginAndGetToken(MEMBER_ARU.loginRequest());
        String pkToken = fixture.loginAndGetToken(MEMBER_PK.loginRequest());
        fixture.createReservation(aruToken, new ReservationRequest(aruId, themeId, date, timeId));
        ReservationWaitingResponse response =
                fixture.enqueueWaitList(pkToken, new ReservationRequest(themeId, date, timeId));
        long waitingId = response.reservation().id();

        CookieDescriptor[] cookieDescriptors = {
                cookieWithName("token").description("인증 토큰")
        };

        ParameterDescriptor[] pathParameterDescriptors = {
                parameterWithName("id").description("예약 대기 ID")
        };

        RestDocumentationFilter docsFilter = document(
                "reservation-waiting-dequeue",
                requestCookies(cookieDescriptors),
                pathParameters(pathParameterDescriptors)
        );

        givenWithSpec().log().all()
                .cookie("token", pkToken)
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .filter(docsFilter)
                .pathParam("id", waitingId)
                .when().delete("/reservations/queue/{id}")
                .then().log().all()
                .statusCode(204);
    }
}
