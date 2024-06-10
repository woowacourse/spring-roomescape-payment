package roomescape.integration;

import static org.hamcrest.Matchers.is;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.request.ParameterDescriptor;
import roomescape.domain.member.Member;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservationtime.ReservationTime;
import roomescape.domain.reservationwaiting.ReservationWaiting;
import roomescape.domain.theme.Theme;

public class ReservationWaitingIntegrationTest extends IntegrationTest {
    @Nested
    @DisplayName("예약 대기 목록 조회 API")
    class FindAllReservationWaiting {
        List<FieldDescriptor> reservationWaitingFindAllResponseDescriptors = List.of(
                fieldWithPath("waitings.[].id").description("예약 대기 id"),
                fieldWithPath("waitings.[].name").description("예약자 이름"),
                fieldWithPath("waitings.[].theme").description("테마 이름"),
                fieldWithPath("waitings.[].date").description("예약 날짜"),
                fieldWithPath("waitings.[].startAt").description("예약 시작 시간"),
                fieldWithPath("waitings.[].reservationId").description("예약 id")
        );

        @Test
        void 예약_대기_목록을_조회할_수_있다() {
            ReservationTime reservationTime = reservationTimeFixture.createFutureReservationTime();
            Theme theme = themeFixture.createFirstTheme();
            Member member = memberFixture.createUserMember();
            Reservation reservation = reservationFixture.createFutureReservation(reservationTime, theme, member);
            waitingFixture.createWaiting(reservation, member);
            memberFixture.createAdminMember();

            RestAssured.given(spec).log().all()
                    .filter(document(
                            "reservation-waiting-find-all-success",
                            responseFields(reservationWaitingFindAllResponseDescriptors)
                    ))
                    .cookies(cookieProvider.createAdminCookies())
                    .when().get("/reservations/waitings")
                    .then().log().all()
                    .statusCode(200)
                    .body("waitings.size()", is(1));
        }
    }

    @Nested
    @DisplayName("예약 대기 추가 API")
    class SaveReservationWaiting {
        List<FieldDescriptor> reservationWaitingSaveRequestDescriptors = List.of(
                fieldWithPath("themeId").description("테마 id"),
                fieldWithPath("timeId").description("예약 시간 id"),
                fieldWithPath("date").description("예약 날짜")
        );
        List<FieldDescriptor> reservationWaitingSaveResponseDescriptors = List.of(
                fieldWithPath("id").description("예약 대기 id"),
                fieldWithPath("name").description("예약자 이름"),
                fieldWithPath("theme").description("테마 이름"),
                fieldWithPath("date").description("예약 날짜"),
                fieldWithPath("startAt").description("예약 시작 시간"),
                fieldWithPath("reservationId").description("예약 id")
        );
        ReservationTime reservationTime;
        Theme theme;
        Member user;
        Member admin;
        Reservation reservation;
        Map<String, String> params;

        @BeforeEach
        void setUp() {
            reservationTime = reservationTimeFixture.createFutureReservationTime();
            theme = themeFixture.createFirstTheme();
            admin = memberFixture.createAdminMember();
            reservation = reservationFixture.createFutureReservation(reservationTime, theme, admin);
            user = memberFixture.createUserMember();
            params = new HashMap<>();
            params.put("themeId", theme.getId().toString()); // TODO: requestDTO로 대체
            params.put("timeId", reservationTime.getId().toString());
        }

        @Test
        void 예약_대기를_추가할_수_있다() {
            params.put("date", reservation.getDate().toString());

            RestAssured.given(spec).log().all()
                    .filter(document(
                            "reservation-waiting-save-success",
                            requestFields(reservationWaitingSaveRequestDescriptors),
                            responseFields(reservationWaitingSaveResponseDescriptors)
                    ))
                    .cookies(cookieProvider.createUserCookies())
                    .contentType(ContentType.JSON)
                    .body(params)
                    .when().post("/reservations/waitings")
                    .then().log().all()
                    .statusCode(201)
                    .header("Location", "/reservations/waitings/1");
        }

        @Test
        void 같은_사용자가_같은_예약에_대해선_예약_대기를_두_번_이상_추가할_수_없다() {
            waitingFixture.createWaiting(reservation, user);
            params.put("date", reservation.getDate().toString());

            RestAssured.given(spec).log().all()
                    .filter(document("reservation-waiting-save-duplicate"))
                    .cookies(cookieProvider.createUserCookies())
                    .contentType(ContentType.JSON)
                    .body(params)
                    .when().post("/reservations/waitings")
                    .then().log().all()
                    .statusCode(409);
        }

        @Test
        void 본인이_예약한_건에_대해선_예약_대기를_추가할_수_없다() {
            params.put("date", reservation.getDate().toString());

            RestAssured.given(spec).log().all()
                    .filter(document("reservation-waiting-save-bad-request"))
                    .cookies(cookieProvider.createAdminCookies())
                    .contentType(ContentType.JSON)
                    .body(params)
                    .when().post("/reservations/waitings")
                    .then().log().all()
                    .statusCode(400);
        }

        @Test
        void 존재하지_않는_예약에_대해선_예약_대기를_추가할_수_없다() {
            params.put("date", "2000-04-09");

            RestAssured.given(spec).log().all()
                    .filter(document("reservation-waiting-save-not-found"))
                    .cookies(cookieProvider.createUserCookies())
                    .contentType(ContentType.JSON)
                    .body(params)
                    .when().post("/reservations/waitings")
                    .then().log().all()
                    .statusCode(404);
        }

        @Test
        void 지난_예약에_대해선_예약_대기를_추가할_수_없다() {
            Reservation pastReservation = reservationFixture.createPastReservation(reservationTime, theme, user);
            params.put("date", pastReservation.getDate().toString());

            RestAssured.given().log().all()
                    .cookies(cookieProvider.createUserCookies())
                    .contentType(ContentType.JSON)
                    .body(params)
                    .when().post("/reservations/waitings")
                    .then().log().all()
                    .statusCode(400);
        }
    }

    @Nested
    @DisplayName("사용자 예약 대기 삭제 API")
    class DeleteReservationWaiting {
        List<ParameterDescriptor> reservationWaitingDeletePathParameterDescriptors = List.of(
                parameterWithName("reservationId").description("예약 id")
        );
        Reservation reservation;

        @BeforeEach
        void setUp() {
            ReservationTime reservationTime = reservationTimeFixture.createFutureReservationTime();
            Theme theme = themeFixture.createFirstTheme();
            Member member = memberFixture.createUserMember();
            reservation = reservationFixture.createFutureReservation(reservationTime, theme, member);
            waitingFixture.createWaiting(reservation, member);
        }

        @Test
        void 사용자는_예약_id로_본인의_예약_대기를_삭제할_수_있다() {
            RestAssured.given(spec).log().all()
                    .filter(document(
                            "reservation-waiting-delete-success",
                            pathParameters(reservationWaitingDeletePathParameterDescriptors)
                    ))
                    .cookies(cookieProvider.createUserCookies())
                    .when().delete("/reservations/{reservationId}/waitings", reservation.getId())
                    .then().log().all()
                    .statusCode(204);
        }

        @Test
        void 예약_id가_존재하지_않는_예약_대기는_삭제할_수_없다() {
            RestAssured.given(spec).log().all()
                    .filter(document("reservation-waiting-delete-not-found"))
                    .cookies(cookieProvider.createUserCookies())
                    .when().delete("/reservations/{reservationId}/waitings", 10)
                    .then().log().all()
                    .statusCode(404);
        }

        @Test
        void 본인이_예약자로_존재하지_않는_예약_대기는_삭제할_수_없다() {
            memberFixture.createAdminMember();

            RestAssured.given().log().all()
                    .cookies(cookieProvider.createAdminCookies())
                    .when().delete("/reservations/{reservationId}/waitings", reservation.getId())
                    .then().log().all()
                    .statusCode(404); // TODO: 400으로 변경하기
        }
    }

    @Nested
    @DisplayName("관리자 예약 대기 삭제 API")
    class DeleteAdminReservationWaiting {
        List<ParameterDescriptor> reservationWaitingDeletePathParameterDescriptors = List.of(
                parameterWithName("waitingId").description("예약 대기 id")
        );
        ReservationWaiting waiting;

        @BeforeEach
        void setUp() {
            ReservationTime reservationTime = reservationTimeFixture.createFutureReservationTime();
            Theme theme = themeFixture.createFirstTheme();
            Member member = memberFixture.createUserMember();
            Reservation reservation = reservationFixture.createFutureReservation(reservationTime, theme, member);
            waiting = waitingFixture.createWaiting(reservation, member);
            memberFixture.createAdminMember();
        }

        @Test
        void 관리자는_선택한_예약_대기_id로_예약_대기를_삭제할_수_있다() {
            RestAssured.given(spec).log().all()
                    .filter(document(
                            "reservation-waiting-admin-delete-success",
                            pathParameters(reservationWaitingDeletePathParameterDescriptors)
                    ))
                    .cookies(cookieProvider.createAdminCookies())
                    .when().delete("/admin/reservations/waitings/{waitingId}", waiting.getId())
                    .then().log().all()
                    .statusCode(204);
        }

        @Test
        void 예약_대기_id가_존재하지_않는_예약_대기는_삭제할_수_없다() {
            RestAssured.given(spec).log().all()
                    .filter(document("reservation-waiting-admin-delete-not-found"))
                    .cookies(cookieProvider.createAdminCookies())
                    .when().delete("/admin/reservations/waitings/{waitingId}", 10)
                    .then().log().all()
                    .statusCode(404);
        }
    }
}
