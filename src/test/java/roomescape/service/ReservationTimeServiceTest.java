package roomescape.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.domain.member.Member;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservationtime.ReservationTime;
import roomescape.domain.theme.Theme;
import roomescape.exception.reservationtime.DuplicatedReservationTimeException;
import roomescape.exception.reservationtime.NotFoundReservationTimeException;
import roomescape.exception.reservationtime.ReservationReferencedTimeException;
import roomescape.service.reservationtime.ReservationTimeService;
import roomescape.service.reservationtime.dto.ReservationTimeAvailableListResponse;
import roomescape.service.reservationtime.dto.ReservationTimeListResponse;
import roomescape.service.reservationtime.dto.ReservationTimeRequest;
import roomescape.service.reservationtime.dto.ReservationTimeResponse;

class ReservationTimeServiceTest extends ServiceTest {
    @Autowired
    private ReservationTimeService reservationTimeService;

    @Nested
    @DisplayName("시간 목록 조회")
    class FindAllReservation {
        @Test
        void 시간_목록을_조회할_수_있다() {
            reservationTimeFixture.createFutureReservationTime();

            ReservationTimeListResponse response = reservationTimeService.findAllReservationTime();

            assertThat(response.getTimes().size())
                    .isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("예약 가능 시간 목록 조회")
    class FindAllAvailableReservationTime {
        Theme theme;
        Reservation reservation;

        @BeforeEach
        void setUp() {
            ReservationTime reservationTime = reservationTimeFixture.createFutureReservationTime();
            Member member = memberFixture.createUserMember();
            theme = themeFixture.createFirstTheme();
            reservation = reservationFixture.createFutureReservation(reservationTime, theme, member);
        }

        @Test
        void 예약이_가능한_시간을_필터링해_조회할_수_있다() {
            LocalDate date = LocalDate.of(2000, 4, 10);

            ReservationTimeAvailableListResponse response = reservationTimeService.findAllAvailableReservationTime(
                    date, theme.getId());

            assertThat(response.getTimes().get(0).getAlreadyBooked())
                    .isFalse();
        }

        @Test
        void 예약이_불가한_시간을_필터링해_조회할_수_있다() {
            ReservationTimeAvailableListResponse response = reservationTimeService.findAllAvailableReservationTime(
                    reservation.getDate(), theme.getId());

            assertThat(response.getTimes().get(0).getAlreadyBooked())
                    .isTrue();
        }
    }

    @Nested
    @DisplayName("시간 추가")
    class SaveReservationTime {
        @Test
        void 시간을_추가할_수_있다() {
            LocalTime startAt = LocalTime.of(11, 0);
            ReservationTimeRequest request = new ReservationTimeRequest(startAt);

            ReservationTimeResponse response = reservationTimeService.saveReservationTime(request);

            assertThat(response.getStartAt())
                    .isEqualTo(startAt);
        }

        @Test
        void 중복된_시간_추가시_예외가_발생한다() {
            ReservationTime reservationTime = reservationTimeFixture.createFutureReservationTime();
            LocalTime startAt = reservationTime.getStartAt();
            ReservationTimeRequest request = new ReservationTimeRequest(startAt);

            assertThatThrownBy(() -> reservationTimeService.saveReservationTime(request))
                    .isInstanceOf(DuplicatedReservationTimeException.class);
        }
    }

    @Nested
    @DisplayName("시간 삭제")
    class DeleteReservationTime {
        ReservationTime reservationTime;
        Member member;

        @BeforeEach
        void setUp() {
            reservationTime = reservationTimeFixture.createFutureReservationTime();
            member = memberFixture.createUserMember();
        }

        @Test
        void 시간을_삭제할_수_있다() {
            reservationTimeService.deleteReservationTime(1L);

            List<ReservationTime> reservationTimes = reservationTimeFixture.findAllReservationTime();
            assertThat(reservationTimes)
                    .isEmpty();
        }

        @Test
        void 존재하지_않는_시간_삭제_시_예외가_발생한다() {
            assertThatThrownBy(() -> reservationTimeService.deleteReservationTime(13L))
                    .isInstanceOf(NotFoundReservationTimeException.class);
        }

        @Test
        void 예약이_존재하는_시간_삭제_시_예외가_발생한다() {
            Theme theme = themeFixture.createFirstTheme();
            reservationFixture.createFutureReservation(reservationTime, theme, member);

            assertThatThrownBy(() -> reservationTimeService.deleteReservationTime(1L))
                    .isInstanceOf(ReservationReferencedTimeException.class);
        }
    }
}
