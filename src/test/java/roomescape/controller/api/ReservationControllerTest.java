package roomescape.controller.api;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import roomescape.controller.BaseControllerTest;
import roomescape.controller.dto.request.ReservationRequest;
import roomescape.domain.reservationtime.ReservationTime;
import roomescape.domain.reservationtime.ReservationTimeRepository;
import roomescape.domain.theme.Theme;
import roomescape.domain.theme.ThemeRepository;
import roomescape.service.dto.response.PersonalReservationResponse;
import roomescape.service.dto.response.ReservationResponse;
import roomescape.support.fixture.ReservationTimeFixture;
import roomescape.support.fixture.ThemeFixture;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

class ReservationControllerTest extends BaseControllerTest {

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private ThemeRepository themeRepository;

    private ReservationTime time;

    private Theme theme;

    @BeforeEach
    void setUp() {
        time = reservationTimeRepository.save(ReservationTimeFixture.ten());
        theme = themeRepository.save(ThemeFixture.theme());

        userLogin();
    }

    @TestFactory
    @DisplayName("예약을 생성, 조회, 삭제한다.")
    Stream<DynamicTest> reservationControllerTests() {
        return Stream.of(
                DynamicTest.dynamicTest("예약을 생성한다.", this::addReservation),
                DynamicTest.dynamicTest("예약을 모두 조회한다.", this::getReservationsByConditions),
                DynamicTest.dynamicTest("예약을 삭제한다.", this::deleteReservationById)
        );
    }

    @TestFactory
    @DisplayName("중복된 예약을 생성하면 실패한다.")
    Stream<DynamicTest> failWhenDuplicatedReservation() {
        return Stream.of(
                DynamicTest.dynamicTest("예약을 생성한다.", this::addReservation),
                DynamicTest.dynamicTest("이미 존재하는 예약을 생성한다.", this::addReservationFailWhenDuplicatedReservation)
        );
    }

    @Test
    @DisplayName("지나간 날짜/시간에 대한 예약은 실패한다.")
    void failWhenDateTimePassed() {
        ReservationRequest request = new ReservationRequest(LocalDate.of(2024, 4, 7), 1L, 1L, "paymentKey", "orderId", BigDecimal.TEN);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/reservations")
                .then().log().all()
                .extract();

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            softly.assertThat(response.body().asString()).contains("예약은 최소 1일 전에 해야합니다.");
        });
    }

    @Test
    @DisplayName("존재하지 않는 예약을 삭제하면 실패한다.")
    void failWhenNotFoundReservation() {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when().delete("/reservations/1?reason=단순 변심")
                .then().log().all()
                .extract();

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            softly.assertThat(response.body().asString()).contains("존재하지 않는 예약입니다.");
        });
    }

    @Test
    @DisplayName("나의 예약들을 조회한다")
    void getMyReservations() {
        LocalDate date = LocalDate.of(2024, 4, 9);
        ReservationRequest saveRequest = new ReservationRequest(date, time.getId(), theme.getId(), "paymentKey", "orderId", BigDecimal.TEN);
        RestAssured.given().log().all()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .body(saveRequest)
                .when().post("/reservations")
                .then().log().all()
                .extract().as(ReservationResponse.class);

        List<PersonalReservationResponse> personalReservationResponses = RestAssured.given().log().all()
                .cookie("token", token)
                .when().get("/reservations/mine")
                .then().log().all()
                .statusCode(200)
                .extract()
                .jsonPath()
                .getList("list", PersonalReservationResponse.class);
        PersonalReservationResponse personalReservationResponse = personalReservationResponses.get(0);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(personalReservationResponses).hasSize(1);
            softly.assertThat(personalReservationResponse.date()).isEqualTo(date);
            softly.assertThat(personalReservationResponse.time()).isEqualTo(time.getStartAt());
            softly.assertThat(personalReservationResponse.theme()).isEqualTo(theme.getRawName());
            softly.assertThat(personalReservationResponse.status()).isEqualTo("예약");
        });
    }

    private void addReservation() {
        LocalDate date = LocalDate.of(2024, 4, 9);
        ReservationRequest request = new ReservationRequest(date, time.getId(), theme.getId(), "paymentKey", "orderId", BigDecimal.TEN);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/reservations")
                .then().log().all()
                .extract();

        ReservationResponse reservationResponse = response.as(ReservationResponse.class);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.CREATED.value());
            softly.assertThat(response.header("Location")).isEqualTo("/reservations/1");

            softly.assertThat(reservationResponse.date()).isEqualTo(date);
            softly.assertThat(reservationResponse.theme()).isEqualTo(theme.getRawName());
            softly.assertThat(reservationResponse.startAt()).isEqualTo(time.getStartAt());
        });
    }

    private void getReservationsByConditions() {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when().get("/reservations")
                .then().log().all()
                .extract();

        List<ReservationResponse> reservationResponses = response.jsonPath()
                .getList("list", ReservationResponse.class);

        ReservationResponse reservationResponse = reservationResponses.get(0);

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value());
            softly.assertThat(reservationResponses).hasSize(1);

            softly.assertThat(reservationResponse.date()).isEqualTo(LocalDate.of(2024, 4, 9));
        });
    }

    private void deleteReservationById() {
        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .when().delete("/reservations/1?reason=단순 변심")
                .then().log().all()
                .extract();

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
        });
    }

    private void addReservationFailWhenDuplicatedReservation() {
        ReservationRequest request = new ReservationRequest(LocalDate.of(2024, 4, 9), 1L, 1L, "paymentKey", "orderId", BigDecimal.TEN);

        ExtractableResponse<Response> response = RestAssured.given().log().all()
                .cookie("token", token)
                .contentType(ContentType.JSON)
                .body(request)
                .when().post("/reservations")
                .then().log().all()
                .extract();

        SoftAssertions.assertSoftly(softly -> {
            softly.assertThat(response.statusCode()).isEqualTo(HttpStatus.BAD_REQUEST.value());
            softly.assertThat(response.body().asString()).contains("해당 날짜/시간에 이미 예약이 존재합니다.");
        });
    }
}
