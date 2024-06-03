package roomescape.reservation.acceptance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static roomescape.fixture.MemberFixture.MEMBER_ADMIN;
import static roomescape.fixture.MemberFixture.MEMBER_BRI;
import static roomescape.fixture.MemberFixture.MEMBER_BROWN;
import static roomescape.fixture.ThemeFixture.THEME_1;
import static roomescape.fixture.TimeFixture.TIME_1;

import io.restassured.RestAssured;
import io.restassured.http.Cookies;
import java.time.LocalDate;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.fixture.ThemeFixture;
import roomescape.fixture.TimeFixture;
import roomescape.model.SpringBootTestBase;
import roomescape.paymenthistory.PaymentType;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.dto.ReservationCreateRequest;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.waiting.dto.WaitingCreateRequest;

public class ReservationAcceptanceTest extends SpringBootTestBase {

    @Autowired
    private ReservationRepository reservationRepository;

    @TestFactory
    @DisplayName("예약 삭제 시 예약 대기가 존재한다면 첫번째 예약 대기가 예약으로 승격된다.")
    Stream<DynamicTest> deleteReservation_whenWaitingExists() {
        Cookies adminCookies = restAssuredTemplate.makeUserCookie(MEMBER_ADMIN);
        LocalDate date = LocalDate.now().plusDays(1);
        Long themeId = restAssuredTemplate.create(ThemeFixture.toThemeCreateRequest(THEME_1), adminCookies).id();
        Long timeId = restAssuredTemplate.create(TimeFixture.toTimeCreateRequest(TIME_1), adminCookies).id();

        Long expectedReservationId = 1L;

        return Stream.of(
                dynamicTest("예약을 추가한다", () -> {
                    Cookies reservationMemberCookies = restAssuredTemplate.makeUserCookie(MEMBER_BRI);
                    ReservationCreateRequest reservationParams =
                            new ReservationCreateRequest(date, timeId, themeId, "paymentKey", "orderId", 1000,
                                    PaymentType.NORMAL);
                    ReservationResponse response = restAssuredTemplate.create(reservationParams,
                            reservationMemberCookies);

                    assertThat(response.id())
                            .isEqualTo(expectedReservationId);
                }),

                dynamicTest("대기를 추가한다", () -> {
                    Cookies waitingMemberCookies = restAssuredTemplate.makeUserCookie(MEMBER_BROWN);
                    WaitingCreateRequest waitingParams = new WaitingCreateRequest(date, timeId, themeId);
                    restAssuredTemplate.create(waitingParams, waitingMemberCookies);
                }),

                dynamicTest("예약을 삭제한다.", () -> {
                    RestAssured.given().log().all()
                            .when().delete("/reservations/" + expectedReservationId)
                            .then().log().all()
                            .statusCode(204);
                }),

                dynamicTest("대기가 승격된다.", () -> {
                    Reservation reservation = reservationRepository.findById(expectedReservationId).get();

                    assertThat(reservation.getMember().getId())
                            .isEqualTo(MEMBER_BROWN.getId());
                    assertThat(reservation.getReservationStatus())
                            .isEqualTo(ReservationStatus.PAYMENT_PENDING);
                }),

                dynamicTest("예약으로 승격된 대기가 결제 대기 상태인 경우에도 지울 수 있다.", () -> {
                    Reservation reservation = reservationRepository.findById(expectedReservationId).get();

                    assertThat(reservation.isNotPaidReservation())
                            .isTrue();

                    RestAssured.given().log().all()
                            .when().delete("/reservations/" + expectedReservationId)
                            .then().log().all()
                            .statusCode(204);
                })
        );
    }
}
