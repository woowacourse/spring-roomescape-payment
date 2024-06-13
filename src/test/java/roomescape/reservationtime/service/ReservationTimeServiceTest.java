package roomescape.reservationtime.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import roomescape.exception.RoomEscapeException;
import roomescape.exception.model.ReservationTimeExceptionCode;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRole;
import roomescape.registration.domain.reservation.domain.Reservation;
import roomescape.registration.domain.reservation.repository.ReservationRepository;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.reservationtime.dto.ReservationTimeRequest;
import roomescape.reservationtime.dto.ReservationTimeResponse;
import roomescape.reservationtime.repository.ReservationTimeRepository;
import roomescape.theme.domain.Theme;
import roomescape.vo.Name;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReservationTimeServiceTest {

    private static final LocalTime CURRENT_TIME = LocalTime.now();

    private final ReservationTime time = new ReservationTime(1L, LocalTime.of(17, 3));

    @InjectMocks
    private ReservationTimeService reservationTimeService;
    @Mock
    private ReservationTimeRepository reservationTimeRepository;
    @Mock
    private ReservationRepository reservationRepository;

    @Test
    @DisplayName("시간을 추가한다.")
    void addReservationTime() {
        when(reservationTimeRepository.save(any()))
                .thenReturn(time);

        ReservationTimeRequest reservationTimeRequest = new ReservationTimeRequest(time.getStartAt());
        ReservationTimeResponse reservationTimeResponse = reservationTimeService.addReservationTime(
                reservationTimeRequest);

        Assertions.assertThat(reservationTimeResponse.id())
                .isEqualTo(1);
    }

    @Test
    @DisplayName("시간을 찾는다.")
    void findReservationTimes() {
        when(reservationTimeRepository.findAllByOrderByStartAt())
                .thenReturn(List.of(time));

        List<ReservationTimeResponse> reservationTimeRespons = reservationTimeService.findReservationTimes();

        Assertions.assertThat(reservationTimeRespons).hasSize(1);
    }

    @Test
    @DisplayName("중복된 예약 시간 생성 요청시 예외를 던진다.")
    void validation_ShouldThrowException_WhenStartAtIsDuplicated() {
        when(reservationTimeRepository.findByStartAt(any()))
                .thenReturn(Optional.of(time));

        ReservationTimeRequest reservationTimeRequest = new ReservationTimeRequest(CURRENT_TIME);
        assertThatThrownBy(() -> reservationTimeService.addReservationTime(reservationTimeRequest))
                .isExactlyInstanceOf(RoomEscapeException.class)
                .hasMessageContaining("이미 존재하는 예약 시간입니다.");
    }

    @Test
    @DisplayName("시간을 지운다.")
    void removeReservationTime() {
        doNothing()
                .when(reservationTimeRepository)
                .deleteById(time.getId());

        assertDoesNotThrow(() -> reservationTimeService.removeReservationTime(time.getId()));
    }

    @Test
    @DisplayName("예약이 존재하는 예약 시간 삭제 요청시 예외를 던진다.")
    void validateReservationExistence_ShouldThrowException_WhenReservationExistAtTime() {
        List<Reservation> reservations = List.of(new Reservation(
                LocalDate.now().plusDays(1),
                new ReservationTime(1L, LocalTime.of(10,0)),
                new Theme(1L, new Name("테스트 테마"), "테마 설명", "썸네일", 15000L),
                new Member(1L, new Name("레모네"), "lemone@gmail.com", "lemon12", MemberRole.MEMBER))
        );

        when(reservationRepository.findByReservationTimeId(1L))
                .thenReturn(reservations);

        Throwable reservationExistAtTime = assertThrows(
                RoomEscapeException.class,
                () -> reservationTimeService.removeReservationTime(1L));

        assertEquals(ReservationTimeExceptionCode.EXIST_RESERVATION_AT_CHOOSE_TIME.getMessage(),
                reservationExistAtTime.getMessage());
    }

}
