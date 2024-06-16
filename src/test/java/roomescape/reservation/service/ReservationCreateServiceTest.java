package roomescape.reservation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import roomescape.exception.BadArgumentRequestException;
import roomescape.member.domain.Member;
import roomescape.member.dto.MemberResponse;
import roomescape.member.repository.MemberRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.Schedule;
import roomescape.reservation.dto.AdminReservationCreateRequest;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.repository.ScheduleRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.dto.ThemeResponse;
import roomescape.theme.repository.ThemeRepository;
import roomescape.time.domain.ReservationTime;
import roomescape.time.dto.TimeResponse;
import roomescape.time.repository.TimeRepository;

@ExtendWith(MockitoExtension.class)
class ReservationCreateServiceTest {
    private static final AdminReservationCreateRequest DEFAULT_REQUEST = new AdminReservationCreateRequest(
            1L, LocalDate.now().plusDays(7), 1L, 1L);
    private static final Member DEFAULT_MEMBER = new Member(1L, "브라운", "brown@abc.com");
    private static final ReservationTime DEFAULT_TIME = new ReservationTime(1L, LocalTime.of(19, 0));
    private static final Theme DEFAULT_THEME = new Theme(1L, "레벨2 탈출", "레벨2 탈출하기", "https://img.jpg");
    private static final Reservation SAVED_RESERVATION = new Reservation(
            1L, new Member(1L, "브라운", "brown@abc.com"),
            LocalDate.of(2024, 8, 15),
            new ReservationTime(1L, LocalTime.of(19, 0)),
            new Theme(1L, "레벨2 탈출", "레벨2 탈출하기", "https://img.jpg"));
    private static final ReservationResponse EXPECTED_RESPONSE = new ReservationResponse(
            1L, new MemberResponse(1L, "브라운"), LocalDate.of(2024, 8, 15),
            new TimeResponse(1L, LocalTime.of(19, 0)),
            new ThemeResponse(1L, "레벨2 탈출", "레벨2 탈출하기", "https://img.jpg"));
    ;

    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private ScheduleRepository scheduleRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private TimeRepository timeRepository;
    @Mock
    private ThemeRepository themeRepository;
    @InjectMocks
    private ReservationCreateService reservationCreateService;

    @DisplayName("에약을 생성할 수 있다.")
    @Test
    void createReservationTest() {
        given(memberRepository.findById(1L)).willReturn(Optional.of(DEFAULT_MEMBER));
        given(timeRepository.findById(1L)).willReturn(Optional.of(DEFAULT_TIME));
        given(themeRepository.findById(1L)).willReturn(Optional.of(DEFAULT_THEME));
        given(reservationRepository.save(any())).willReturn(SAVED_RESERVATION);

        ReservationResponse actual = reservationCreateService.createReservation(DEFAULT_REQUEST);

        assertThat(actual).isEqualTo(EXPECTED_RESPONSE);
    }

    @DisplayName("예약 생성 시, 해당 스케줄이 존재해도 정상적으로 예약이 생성된다.")
    @Test
    void createReservationTest_whenScheduleIsExist() {
        LocalDate date = LocalDate.now().plusDays(7);
        AdminReservationCreateRequest request = new AdminReservationCreateRequest(1L, date, 1L, 1L);
        given(memberRepository.findById(1L)).willReturn(Optional.of(DEFAULT_MEMBER));
        given(timeRepository.findById(1L)).willReturn(Optional.of(DEFAULT_TIME));
        given(themeRepository.findById(1L)).willReturn(Optional.of(DEFAULT_THEME));
        given(scheduleRepository.findByDateAndTimeAndTheme(date, DEFAULT_TIME, DEFAULT_THEME))
                .willReturn(Optional.of(new Schedule(1L, date, DEFAULT_TIME, DEFAULT_THEME)));
        given(reservationRepository.save(any())).willReturn(SAVED_RESERVATION);

        ReservationResponse actual = reservationCreateService.createReservation(DEFAULT_REQUEST);

        assertThat(actual).isEqualTo(EXPECTED_RESPONSE);
    }

    @DisplayName("예약 생성 시, memberId에 해당하는 멤버가 없다면 예외를 던진다.")
    @Test
    void createReservationTest_whenMemberNotExist() {
        given(memberRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> reservationCreateService.createReservation(DEFAULT_REQUEST))
                .isInstanceOf(BadArgumentRequestException.class)
                .hasMessage("해당 멤버가 존재하지 않습니다.");
    }

    @DisplayName("예약 생성 시, timeId에 해당하는 예약 시간이 없다면 예외를 던진다.")
    @Test
    void createReservationTest_whenTimeNotExist() {
        given(memberRepository.findById(1L)).willReturn(Optional.of(DEFAULT_MEMBER));
        given(timeRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> reservationCreateService.createReservation(DEFAULT_REQUEST))
                .isInstanceOf(BadArgumentRequestException.class)
                .hasMessage("해당 예약 시간이 존재하지 않습니다.");
    }

    @DisplayName("예약 생성 시, themeId에 해당하는 테마가 없다면 예외를 던진다.")
    @Test
    void createReservationTest_whenThemeNotExist() {
        given(memberRepository.findById(1L)).willReturn(Optional.of(DEFAULT_MEMBER));
        given(timeRepository.findById(1L)).willReturn(Optional.of(DEFAULT_TIME));
        given(themeRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> reservationCreateService.createReservation(DEFAULT_REQUEST))
                .isInstanceOf(BadArgumentRequestException.class)
                .hasMessage("해당 테마가 존재하지 않습니다.");
    }

    @DisplayName("예약 생성 시, 이미 해당 스케줄이 존재한다면, 해당 스케줄을 이용한다.")
    @Test
    void createReservationTest_whenDateTimeIsBefore() {
        LocalDate date = LocalDate.now().minusDays(7);
        AdminReservationCreateRequest request = new AdminReservationCreateRequest(1L, date, 1L, 1L);
        given(memberRepository.findById(1L)).willReturn(Optional.of(DEFAULT_MEMBER));
        given(timeRepository.findById(1L)).willReturn(Optional.of(DEFAULT_TIME));
        given(themeRepository.findById(1L)).willReturn(Optional.of(DEFAULT_THEME));

        assertThatThrownBy(() -> reservationCreateService.createReservation(request))
                .isInstanceOf(BadArgumentRequestException.class)
                .hasMessage("예약은 현재 시간 이후여야 합니다.");
    }
}
