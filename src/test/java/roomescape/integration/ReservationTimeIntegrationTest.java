package roomescape.integration;

import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.request.ParameterDescriptor;
import roomescape.domain.member.Member;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservationtime.ReservationTime;
import roomescape.domain.theme.Theme;
import roomescape.service.reservationtime.dto.ReservationTimeAvailableListResponse;

class ReservationTimeIntegrationTest extends IntegrationTest {
    @Nested
    @DisplayName("예약 시간 목록 조회 API")
    class FindAllReservationTime {
        List<FieldDescriptor> reservationTimeFindAllResponseDescriptors = List.of(
                fieldWithPath("times.[].id").description("시간 ID"),
                fieldWithPath("times.[].startAt").description("시작 시간")
        );

        @Test
        void 시간_목록을_조회할_수_있다() {
            memberFixture.createAdminMember();
            reservationTimeFixture.createFutureReservationTime();

            RestAssured.given(spec).log().all()
                    .filter(document("reservation-time-find-all-success",
                            responseFields(reservationTimeFindAllResponseDescriptors)
                    ))
                    .cookies(cookieProvider.createAdminCookies())
                    .when().get("/times")
                    .then().log().all()
                    .statusCode(200)
                    .body("times.size()", is(1));
        }
    }

    @Nested
    @DisplayName("예약 가능 시간 목록 조회 API")
    class FindAllAvailableReservationTime {
        List<ParameterDescriptor> reservationTimeAvailableFindAllQueryParametersDescriptors = List.of(
                parameterWithName("date").description("날짜"),
                parameterWithName("themeId").description("테마 ID")
        );
        List<FieldDescriptor> reservationTimeAvailableFindAllResponseDescriptors = List.of(
                fieldWithPath("times.[].id").description("시간 ID"),
                fieldWithPath("times.[].startAt").description("시작 시간"),
                fieldWithPath("times.[].alreadyBooked").description("이미 예약된 시간인지 여부")
        );
        Reservation reservation;
        Theme theme;

        @BeforeEach
        void setUp() {
            ReservationTime reservationTime = reservationTimeFixture.createFutureReservationTime();
            Member member = memberFixture.createUserMember();
            theme = themeFixture.createFirstTheme();
            reservation = reservationFixture.createFutureReservation(reservationTime, theme, member);
        }

        @Test
        void 예약이_가능한_시간을_필터링해_조회할_수_있다() {
            RestAssured.given(spec).log().all()
                    .filter(document("reservation-time-available-find-all-success",
                            queryParameters(reservationTimeAvailableFindAllQueryParametersDescriptors),
                            responseFields(reservationTimeAvailableFindAllResponseDescriptors)
                    ))
                    .when().get("/times/available?date={date}&themeId={themeId}",
                            "2024-10-05", theme.getId())
                    .then().log().all()
                    .statusCode(200)
                    .body("times.size()", is(1));

            ReservationTimeAvailableListResponse response = RestAssured.get(
                            "/times/available?date={date}&themeId={themeId}",
                            "2024-10-05", theme.getId())
                    .as(ReservationTimeAvailableListResponse.class);
            Assertions.assertThat(response.getTimes().get(0).getAlreadyBooked()).isFalse();
        }

        @Test
        void 예약이_불가한_시간을_필터링해_조회할_수_있다() {
            String date = reservation.getDate().toString();

            RestAssured.given().log().all()
                    .when().get("/times/available?date={date}&themeId={themeId}", date, theme.getId())
                    .then().log().all()
                    .statusCode(200)
                    .body("times.size()", is(1));

            ReservationTimeAvailableListResponse response = RestAssured.get(
                            "/times/available?date=" + date + "&themeId=1")
                    .as(ReservationTimeAvailableListResponse.class);
            Assertions.assertThat(response.getTimes().get(0).getAlreadyBooked()).isTrue();
        }
    }

    @Nested
    @DisplayName("예약 시간 추가 API")
    class SaveReservationTime {
        List<FieldDescriptor> reservationTimeSaveRequestDescriptors = List.of(
                fieldWithPath("startAt").description("시작 시간")
        );
        List<FieldDescriptor> reservationTimeSaveResponseDescriptors = List.of(
                fieldWithPath("id").description("시간 ID"),
                fieldWithPath("startAt").description("시작 시간")
        );

        @BeforeEach
        void setUp() {
            memberFixture.createAdminMember();
        }

        @Test
        void 시간을_추가할_수_있다() {
            Map<String, String> params = new HashMap<>();
            params.put("startAt", "11:00");

            RestAssured.given(spec).log().all()
                    .filter(document("reservation-time-save-success",
                            requestFields(reservationTimeSaveRequestDescriptors),
                            responseFields(reservationTimeSaveResponseDescriptors)
                    ))
                    .cookies(cookieProvider.createAdminCookies())
                    .contentType(ContentType.JSON)
                    .body(params)
                    .when().post("/times")
                    .then().log().all()
                    .statusCode(201)
                    .header("Location", "/times/1")
                    .body("id", is(1));
        }

        @Test
        void 시작_시간이_빈_값이면_시간을_추가할_수_없다() {
            Map<String, String> params = new HashMap<>();
            params.put("startAt", null);

            RestAssured.given().log().all()
                    .cookies(cookieProvider.createAdminCookies())
                    .contentType(ContentType.JSON)
                    .body(params)
                    .when().post("/times")
                    .then().log().all()
                    .statusCode(400);
        }

        @Test
        void 시작_시간의_형식이_다르면_시간을_추가할_수_없다() {
            Map<String, String> params = new HashMap<>();
            params.put("startAt", "25:00");

            RestAssured.given(spec).log().all()
                    .filter(document("reservation-time-save-bad-request"))
                    .cookies(cookieProvider.createAdminCookies())
                    .contentType(ContentType.JSON)
                    .body(params)
                    .when().post("/times")
                    .then().log().all()
                    .statusCode(400);
        }

        @Test
        void 중복된_시간은_추가할_수_없다() {
            ReservationTime reservationTime = reservationTimeFixture.createFutureReservationTime();
            Map<String, String> params = new HashMap<>();
            params.put("startAt", reservationTime.getStartAt().toString());

            RestAssured.given(spec).log().all()
                    .filter(document("reservation-time-save-duplicate"))
                    .cookies(cookieProvider.createAdminCookies())
                    .contentType(ContentType.JSON)
                    .body(params)
                    .when().post("/times")
                    .then().log().all()
                    .statusCode(409);
        }
    }

    @Nested
    @DisplayName("예약 시간 삭제 API")
    class DeleteReservationTime {
        List<ParameterDescriptor> reservationTimeDeletePathParametersDescriptors = List.of(
                parameterWithName("timeId").description("시간 ID")
        );
        ReservationTime reservationTime;
        Member member;

        @BeforeEach
        void setUp() {
            reservationTime = reservationTimeFixture.createFutureReservationTime();
            member = memberFixture.createAdminMember();
        }

        @Test
        void 시간을_삭제할_수_있다() {
            RestAssured.given(spec).log().all()
                    .filter(document("reservation-time-delete-success",
                            pathParameters(reservationTimeDeletePathParametersDescriptors)
                    ))
                    .cookies(cookieProvider.createAdminCookies())
                    .when().delete("/times/{timeId}", reservationTime.getId())
                    .then().log().all()
                    .statusCode(204);
        }

        @Test
        void 존재하지_않는_시간은_삭제할_수_없다() {
            RestAssured.given(spec).log().all()
                    .filter(document("reservation-time-delete-not-found"))
                    .cookies(cookieProvider.createAdminCookies())
                    .when().delete("/times/{timeId}", 13)
                    .then().log().all()
                    .statusCode(404);
        }

        @Test
        void 예약이_존재하는_시간은_삭제할_수_없다() {
            Theme theme = themeFixture.createFirstTheme();
            reservationFixture.createFutureReservation(reservationTime, theme, member);

            RestAssured.given(spec).log().all()
                    .filter(document("reservation-time-delete-bad-request"))
                    .cookies(cookieProvider.createAdminCookies())
                    .when().delete("/times/{timeId}", reservationTime.getId())
                    .then().log().all()
                    .statusCode(400);
        }
    }
}
