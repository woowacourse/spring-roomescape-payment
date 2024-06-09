package roomescape.acceptance;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;
import roomescape.dto.reservation.ReservationTimeSaveRequest;

import static io.restassured.RestAssured.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.restassured.RestAssuredRestDocumentation.document;
import static roomescape.FieldDescriptorFixture.availableTimeListFieldDescriptor;
import static roomescape.FieldDescriptorFixture.errorFieldDescriptor;
import static roomescape.FieldDescriptorFixture.timeFieldDescriptor;
import static roomescape.FieldDescriptorFixture.timeFieldDescriptorWithoutId;
import static roomescape.FieldDescriptorFixture.timeListFieldDescriptor;
import static roomescape.FieldDescriptorFixture.timeParameterDescriptor;
import static roomescape.TestFixture.DATE_MAY_EIGHTH;
import static roomescape.TestFixture.START_AT_SIX;

class ReservationTimeAcceptanceTest extends AcceptanceTest {

    @Test
    @DisplayName("[관리자] 예약 시간을 성공적으로 생성하면 201을 응답한다.")
    void respondCreatedWhenCreateReservationTime() {
        final ReservationTimeSaveRequest request = new ReservationTimeSaveRequest(START_AT_SIX);

        given(spec)
                .filter(document("time/admin/create",
                        requestFields(timeFieldDescriptorWithoutId()),
                        responseFields(timeFieldDescriptor)))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .post("/times")
                .then()
                .statusCode(201);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "13-00"})
    @DisplayName("잘못된 형식으로 예약 시간 생성 시 400을 응답한다.")
    void respondBadRequestWhenCreateInvalidReservationTime(final String invalidTime) {
        final ReservationTimeSaveRequest request = new ReservationTimeSaveRequest(invalidTime);

        given(spec)
                .filter(document("time/admin/create/fail",
                        requestFields(timeFieldDescriptorWithoutId()),
                        responseFields(errorFieldDescriptor)))
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(request)
                .when()
                .post("/times")
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("예약 시간 목록을 성공적으로 조회하면 200을 응답한다.")
    void respondOkWhenFindReservationTimes() {
        saveReservationTime();

        given(spec)
                .filter(document("time/admin/find/all",
                        responseFields(timeListFieldDescriptor)))
                .when()
                .get("/times")
                .then()
                .statusCode(200);
    }

    @Test
    @DisplayName("예약 시간을 성공적으로 삭제하면 204를 응답한다.")
    void respondNoContentWhenDeleteReservationTime() {
        final Long reservationTimeId = saveReservationTime();

        given(spec)
                .filter(document("time/admin/delete/success",
                        pathParameters(parameterWithName("id").description("시간 아이디"))))
                .when()
                .delete("/times/{id}", reservationTimeId)
                .then()
                .statusCode(204);
    }

    @Test
    @DisplayName("존재하지 않는 예약 시간을 삭제하면 400을 응답한다.")
    void respondBadRequestWhenDeleteNotExistingReservationTime() {
        saveReservationTime();

        given(spec)
                .filter(document("time/admin/delete/fail",
                        pathParameters(parameterWithName("id").description("시간 아이디")),
                        responseFields(errorFieldDescriptor)))
                .when()
                .delete("/times/{id}", 0)
                .then()
                .statusCode(400);
    }

    @Test
    @DisplayName("예약 가능한 시간 목록을 성공적으로 조회하면 200을 응답한다.")
    void respondOkWhenFindAvailableReservationTimes() {
        final Long themeId = saveTheme();

        given(spec)
                .filter(document("time/admin/delete/fail",
                        queryParameters(timeParameterDescriptor),
                        responseFields(availableTimeListFieldDescriptor)))
                .queryParam("date", DATE_MAY_EIGHTH.toString())
                .queryParam("themeId", themeId)
                .when()
                .get("/times/available")
                .then()
                .statusCode(200);
    }
}
