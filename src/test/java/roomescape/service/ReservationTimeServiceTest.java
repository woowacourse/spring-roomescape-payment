package roomescape.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;

import static roomescape.fixture.TestFixture.DATE_MAY_EIGHTH;
import static roomescape.fixture.TestFixture.MEMBER_TENNY;
import static roomescape.fixture.TestFixture.RESERVATION_TIME_SEVEN;
import static roomescape.fixture.TestFixture.RESERVATION_TIME_SIX;
import static roomescape.fixture.TestFixture.START_AT_SEVEN;
import static roomescape.fixture.TestFixture.START_AT_SIX;
import static roomescape.fixture.TestFixture.THEME_HORROR;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.domain.reservation.ReservationTime;
import roomescape.dto.reservation.AvailableReservationTimeResponse;
import roomescape.dto.reservation.AvailableReservationTimeSearch;
import roomescape.dto.reservation.ReservationTimeResponse;
import roomescape.exception.RoomescapeException;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ReservationTimeRepository;

@ExtendWith(MockitoExtension.class)
class ReservationTimeServiceTest {
    @Mock
    private ReservationTimeRepository reservationTimeRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ReservationTimeService reservationTimeService;

    @Test
    @DisplayName("예약 시간을 생성한다.")
    void create() {
        // given
        final ReservationTime reservationTime = RESERVATION_TIME_SIX(1L);
        given(reservationTimeRepository.save(any())).willReturn(reservationTime);

        // when
        final ReservationTimeResponse response = reservationTimeService.create(reservationTime);

        // then
        assertAll(
                () -> assertThat(response.id()).isEqualTo(reservationTime.getId()),
                () -> assertThat(response.startAt()).isEqualTo(reservationTime.getStartAt().toString())
        );
    }

    @Test
    @DisplayName("예약 시간 목록을 조회한다.")
    void getAll() {
        // given
        final ReservationTime reservationTime = RESERVATION_TIME_SIX();
        given(reservationTimeRepository.findAll()).willReturn(List.of(reservationTime));

        // when
        final List<ReservationTimeResponse> responses = reservationTimeService.findAll();

        // then
        assertThat(responses).hasSize(1)
                .extracting(ReservationTimeResponse::startAt)
                .contains(START_AT_SIX);
    }

    @Test
    @DisplayName("예약 시간을 삭제한다.")
    void delete() {
        // given
        final ReservationTime reservationTime = RESERVATION_TIME_SIX(1L);

        given(reservationTimeRepository.findById(anyLong())).willReturn(Optional.of(reservationTime));

        // when & then
        assertThatCode(() -> reservationTimeService.delete(1L))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("삭제하려는 예약 시간에 이미 예약이 존재할 경우 예외가 발생한다.")
    void throwExceptionWhenHasReservation() {
        // given
        final ReservationTime reservationTime = RESERVATION_TIME_SIX(1L);

        given(reservationTimeRepository.findById(anyLong())).willReturn(Optional.of(reservationTime));
        given(reservationRepository.countByTimeId(anyLong())).willReturn(1);

        // when & then
        assertThatThrownBy(() -> reservationTimeService.delete(1L))
                .isInstanceOf(RoomescapeException.class);
    }

    @Test
    @DisplayName("예약 시간들의 예약 가능 여부 목록을 조회한다.")
    void findAvailableReservationTimes() {
        // given
        final ReservationTime reservedTime = RESERVATION_TIME_SIX(1L);
        final AvailableReservationTimeSearch availableReservationTimeSearch
                = new AvailableReservationTimeSearch(DATE_MAY_EIGHTH, 1L);
        final Reservation reservation = new Reservation(null, MEMBER_TENNY(1L), DATE_MAY_EIGHTH, reservedTime,
                THEME_HORROR(1L), ReservationStatus.RESERVED, null);
        given(reservationRepository.findByDateAndThemeId(DATE_MAY_EIGHTH, 1L))
                .willReturn(List.of(reservation));
        given(reservationTimeRepository.findAll()).willReturn(List.of(reservedTime, RESERVATION_TIME_SEVEN(2L)));

        // when
        final List<AvailableReservationTimeResponse> availableReservationTimes
                = reservationTimeService.findAvailableReservationTimes(availableReservationTimeSearch);

        // then
        assertAll(() -> {
            assertThat(isReserved(availableReservationTimes, START_AT_SIX)).isTrue();
            assertThat(isReserved(availableReservationTimes, START_AT_SEVEN)).isFalse();
        });
    }

    private boolean isReserved(
            final List<AvailableReservationTimeResponse> availableReservationTimes,
            final String time
    ) {
        return availableReservationTimes.stream()
                .filter(response -> response.startAt().equals(time))
                .findFirst()
                .get()
                .isReserved();
    }
}
