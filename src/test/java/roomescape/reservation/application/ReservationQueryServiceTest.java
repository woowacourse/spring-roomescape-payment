package roomescape.reservation.application;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.Theme;
import roomescape.reservation.dto.response.MyReservationResponse;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static roomescape.TestFixture.ADMIN_NAME;
import static roomescape.TestFixture.MIA_NAME;
import static roomescape.TestFixture.MIA_RESERVATION;
import static roomescape.TestFixture.MIA_RESERVATION_DATE;
import static roomescape.TestFixture.MIA_RESERVATION_TIME;
import static roomescape.TestFixture.TOMMY_NAME;
import static roomescape.TestFixture.TOMMY_RESERVATION;
import static roomescape.TestFixture.TOMMY_RESERVATION_DATE;
import static roomescape.TestFixture.TOMMY_RESERVATION_TIME;
import static roomescape.TestFixture.WOOTECO_THEME_NAME;
import static roomescape.reservation.domain.ReservationStatus.BOOKING;
import static roomescape.reservation.domain.ReservationStatus.PENDING_PAYMENT;
import static roomescape.reservation.domain.ReservationStatus.WAITING;

public class ReservationQueryServiceTest extends ReservationServiceTest {
    @Autowired
    private ReservationQueryService reservationQueryService;

    @Autowired
    private BookingManageService bookingManageService;

    @Autowired
    private WaitingManageService waitingManageService;

    @Test
    @DisplayName("모든 예약 목록을 조회한다.")
    void findAll() {
        // given
        bookingManageService.create(MIA_RESERVATION(miaReservationTime, wootecoTheme, mia, BOOKING));
        bookingManageService.create(TOMMY_RESERVATION(miaReservationTime, wootecoTheme, tommy, BOOKING));

        // when
        List<Reservation> reservations = reservationQueryService.findAllInBooking();

        // then
        assertSoftly(softly -> {
            softly.assertThat(reservations).hasSize(2)
                    .extracting(Reservation::getMemberName)
                    .contains(MIA_NAME, TOMMY_NAME);
            softly.assertThat(reservations).extracting(Reservation::getTime)
                    .extracting(ReservationTime::getStartAt)
                    .contains(MIA_RESERVATION_TIME, TOMMY_RESERVATION_TIME);
            softly.assertThat(reservations).extracting(Reservation::getTheme)
                    .extracting(Theme::getName)
                    .contains(WOOTECO_THEME_NAME, WOOTECO_THEME_NAME);
        });
    }

    @Test
    @DisplayName("예약자, 테마, 날짜로 예약 목록을 조회한다.")
    void findAllByMemberIdAndThemeIdAndDateBetween() {
        // given
        Reservation miaReservation = bookingManageService.create(
                MIA_RESERVATION(miaReservationTime, wootecoTheme, mia, BOOKING));
        Reservation tommyReservation = bookingManageService.create(
                TOMMY_RESERVATION(miaReservationTime, wootecoTheme, tommy, BOOKING));

        // when
        List<Reservation> reservations = reservationQueryService.findAllByMemberIdAndThemeIdAndDateBetween(
                miaReservation.getMember().getId(), miaReservation.getTheme().getId(),
                miaReservation.getDate(), tommyReservation.getDate());

        // then
        assertThat(reservations).hasSize(1)
                .extracting(Reservation::getMemberName)
                .containsExactly(MIA_NAME);
    }

    @Test
    @DisplayName("대기 중인 모든 예약 목록을 조회한다.")
    void findAllWithDetails() {
        // given
        bookingManageService.create(MIA_RESERVATION(miaReservationTime, wootecoTheme, mia, BOOKING));
        waitingManageService.create(new Reservation(tommy, MIA_RESERVATION_DATE, miaReservationTime, wootecoTheme, WAITING));

        bookingManageService.create(TOMMY_RESERVATION(miaReservationTime, wootecoTheme, tommy, BOOKING));
        waitingManageService.create(new Reservation(admin, TOMMY_RESERVATION_DATE, miaReservationTime, wootecoTheme, PENDING_PAYMENT));
        waitingManageService.create(new Reservation(mia, TOMMY_RESERVATION_DATE, miaReservationTime, wootecoTheme, WAITING));

        // when
        List<Reservation> reservations = reservationQueryService.findAllInWaiting();

        // then
        assertThat(reservations).hasSize(3)
                .extracting(Reservation::getMemberName)
                .contains(TOMMY_NAME, MIA_NAME, ADMIN_NAME);
    }

    @Test
    @DisplayName("사용자의 예약 목록을 조회한다.")
    void findAllByMember() {
        // given
        bookingManageService.create(MIA_RESERVATION(miaReservationTime, wootecoTheme, mia, BOOKING));
        bookingManageService.create(TOMMY_RESERVATION(miaReservationTime, wootecoTheme, tommy, WAITING));
        waitingManageService.create(MIA_RESERVATION(miaReservationTime, horrorTheme, mia, PENDING_PAYMENT));

        // when
        List<MyReservationResponse> miaReservations = reservationQueryService.findAllMyReservations(mia);

        // then
        assertThat(miaReservations).hasSize(2)
                .extracting(MyReservationResponse::status)
                .contains("예약", "결제 대기");
    }
}
