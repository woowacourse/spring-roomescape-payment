package roomescape.theme.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import roomescape.exception.RoomEscapeException;
import roomescape.exception.model.ThemeExceptionCode;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRole;
import roomescape.registration.domain.reservation.domain.Reservation;
import roomescape.registration.domain.reservation.repository.ReservationRepository;
import roomescape.reservationtime.domain.ReservationTime;
import roomescape.theme.domain.Theme;
import roomescape.vo.Name;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ThemeServiceTest {

    @InjectMocks
    private ThemeService themeService;
    @Mock
    private ReservationRepository reservationRepository;

    @Test
    @DisplayName("예약이 존재하는 테마는 삭제하지 못한다.")
    void validateReservationExistence_ShouldThrowException_WhenReservationExist() {
        List<Reservation> reservations = List.of(new Reservation(
                LocalDate.now().plusDays(1),
                new ReservationTime(1L, LocalTime.of(10,0)),
                new Theme(1L, new Name("테스트 테마"), "테마 설명", "썸네일", 15000L),
                new Member(1L, new Name("레모네"), "lemone@gmail.com", "lemon12", MemberRole.MEMBER))
        );

        when(reservationRepository.findByThemeId(1L))
                .thenReturn(reservations);

        Throwable reservationExistAtTime = assertThrows(
                RoomEscapeException.class,
                () -> themeService.removeTheme(1L));

        assertEquals(ThemeExceptionCode.USING_THEME_RESERVATION_EXIST.getMessage(),
                reservationExistAtTime.getMessage());
    }
}
