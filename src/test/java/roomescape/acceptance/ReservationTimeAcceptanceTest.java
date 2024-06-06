package roomescape.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static roomescape.fixture.MemberFixture.MEMBER_ARU;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.restdocs.cookies.CookieDescriptor;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.restdocs.restassured.RestDocumentationFilter;
import roomescape.application.reservation.dto.request.ReservationRequest;
import roomescape.application.reservation.dto.request.ThemeRequest;
import roomescape.application.reservation.dto.response.AvailableTimeResponse;
import roomescape.application.reservation.dto.response.ReservationTimeResponse;

class ReservationTimeAcceptanceTest extends AcceptanceTest {

    @Test
    @DisplayName("관리자가 예약 시간을 생성한다.")
    void createReservationTimeTest() {
        String request = """
                {
                    "startAt": "10:00"
                }
                """;

        CookieDescriptor[] cookieDescriptors = {
                cookieWithName("token").description("어드민 토큰")
        };

        FieldDescriptor[] requestFieldDescriptors = {
                fieldWithPath("startAt").description("예약 시각"),
        };

        FieldDescriptor[] responseFieldDescriptors = {
                fieldWithPath("id").description("ID"),
                fieldWithPath("startAt").description("예약 시각")
        };

        RestDocumentationFilter docsFilter = document(
                "reservation-time-create",
                requestCookies(cookieDescriptors),
                requestFields(requestFieldDescriptors),
                responseFields(responseFieldDescriptors)
        );

        givenWithSpec().log().all()
                .cookie("token", fixture.getAdminToken())
                .contentType(APPLICATION_JSON_VALUE)
                .accept(APPLICATION_JSON_VALUE)
                .filter(docsFilter)
                .body(request)
                .when().post("/times")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value());
    }

    @Test
    @DisplayName("예약 시간을 모두 조회한다.")
    void findAllReservationTimesTest() {
        fixture.createReservationTime(10, 0);
        fixture.createReservationTime(11, 30);

        FieldDescriptor[] responseFieldDescriptors = {
                fieldWithPath("[].id").description("ID"),
                fieldWithPath("[].startAt").description("예약 시각")
        };

        RestDocumentationFilter docsFilter = document(
                "reservation-time-find-all",
                responseFields(responseFieldDescriptors)
        );

        givenWithSpec().given().log().all()
                .accept(APPLICATION_JSON_VALUE)
                .filter(docsFilter)
                .when().get("/times")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .assertThat()
                .body("size()", equalTo(2));
    }

    @Test
    @DisplayName("관리자가 예약 시간을 삭제한다.")
    void deleteReservationTimeTest() {
        ReservationTimeResponse response = fixture.createReservationTime(10, 0);

        CookieDescriptor[] cookieDescriptors = {
                cookieWithName("token").description("어드민 토큰")
        };

        ParameterDescriptor[] parameterDescriptors = {
                parameterWithName("id").description("예약 시간 ID")
        };

        RestDocumentationFilter docsFilter = document(
                "reservation-time-delete",
                requestCookies(cookieDescriptors),
                pathParameters(parameterDescriptors)
        );

        givenWithSpec().log().all()
                .cookie("token", fixture.getAdminToken())
                .filter(docsFilter)
                .pathParam("id", response.id())
                .when().delete("/times/{id}")
                .then().log().all()
                .statusCode(HttpStatus.NO_CONTENT.value());
    }

    @Test
    @DisplayName("예약 가능한 시간을 조회한다.")
    void findAvailableTimesTest() {
        fixture.registerMember(MEMBER_ARU.registerRequest());
        String token = fixture.loginAndGetToken(MEMBER_ARU.loginRequest());
        fixture.createReservationTime(10, 0);
        fixture.createReservationTime(11, 30);
        long timeId = fixture.createReservationTime(13, 0).id();
        long themeId = fixture.createTheme(
                new ThemeRequest("theme", "desc", "url", 10_000L)
        ).id();
        ReservationRequest request = new ReservationRequest(themeId, LocalDate.of(2024, 12, 25), timeId);
        fixture.createReservation(token, request);

        ParameterDescriptor[] requestParameterDescriptors = {
                parameterWithName("date").description("예약 날짜"),
                parameterWithName("themeId").description("테마 ID")
        };

        FieldDescriptor[] responseFieldDescriptors = {
                fieldWithPath("[].time.id").description("시간 ID"),
                fieldWithPath("[].time.startAt").description("예약 시각"),
                fieldWithPath("[].isBooked").description("예약 여부")
        };

        RestDocumentationFilter docsFilter = document(
                "reservation-time-find-available",
                queryParameters(requestParameterDescriptors),
                responseFields(responseFieldDescriptors)
        );

        AvailableTimeResponse[] responses = givenWithSpec().log().all()
                .accept(APPLICATION_JSON_VALUE)
                .queryParam("date", "2024-12-25")
                .queryParam("themeId", themeId)
                .filter(docsFilter)
                .when().get("/times/available")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract()
                .as(AvailableTimeResponse[].class);

        List<AvailableTimeResponse> actual = Arrays.stream(responses)
                .filter(AvailableTimeResponse::isBooked)
                .toList();
        assertThat(actual).hasSize(1);
    }
}
