package roomescape.reservation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import roomescape.member.domain.Member;
import roomescape.member.dto.MemberResponse;
import roomescape.member.repository.MemberRepository;
import roomescape.paymenthistory.service.TossPaymentHistoryService;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.dto.AdminReservationCreateRequest;
import roomescape.reservation.dto.MyReservationResponse;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.dto.ThemeResponse;
import roomescape.theme.repository.ThemeRepository;
import roomescape.time.domain.ReservationTime;
import roomescape.time.dto.TimeResponse;
import roomescape.time.repository.TimeRepository;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private MemberRepository memberRepository;
    @Mock
    private TimeRepository timeRepository;
    @Mock
    private ThemeRepository themeRepository;
    @Mock
    private TossPaymentHistoryService tossPaymentHistoryService;
    @InjectMocks
    private ReservationService reservationService;

    @DisplayName("모든 예약을 조회할 수 있다.")
    @Test
    void findReservationsTest() {
        given(reservationRepository.findAll()).willReturn(List.of(
                new Reservation(
                        1L, new Member(1L, "브라운", "brown@abc.com"),
                        LocalDate.of(2024, 8, 15),
                        new ReservationTime(1L, LocalTime.of(19, 0)),
                        new Theme(1L, "레벨2 탈출", "레벨2 탈출하기", "https://img.jpg"),
                        ReservationStatus.RESERVED),
                new Reservation(
                        2L, new Member(2L, "브리", "bri@abc.com"),
                        LocalDate.of(2024, 8, 20),
                        new ReservationTime(1L, LocalTime.of(19, 0)),
                        new Theme(1L, "레벨2 탈출", "레벨2 탈출하기", "https://img.jpg"),
                        ReservationStatus.RESERVED)));
        List<ReservationResponse> expected = List.of(
                new ReservationResponse(
                        1L, new MemberResponse(1L, "브라운"),
                        LocalDate.of(2024, 8, 15),
                        new TimeResponse(1L, LocalTime.of(19, 0)),
                        new ThemeResponse(1L, "레벨2 탈출", "레벨2 탈출하기", "https://img.jpg")),
                new ReservationResponse(
                        2L, new MemberResponse(2L, "브리"),
                        LocalDate.of(2024, 8, 20),
                        new TimeResponse(1L, LocalTime.of(19, 0)),
                        new ThemeResponse(1L, "레벨2 탈출", "레벨2 탈출하기", "https://img.jpg")));

        List<ReservationResponse> actual = reservationService.findReservations();

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("나의 예약을 조회할 수 있다.")
    @Test
    void findMyReservationsTest() {
        Member member = new Member(1L, "브라운", "brown@abc.com");
        Reservation reservation1 = new Reservation(
                1L,
                new Member(1L, "브라운", "brown@abc.com"),
                LocalDate.of(2100, 1, 1),
                new ReservationTime(1L, LocalTime.of(19, 0)),
                new Theme(1L, "레벨2 탈출", "레벨2 탈출하기", "https://img.jpg"),
                ReservationStatus.RESERVED);
        Reservation reservation2 = new Reservation(
                2L, new Member(1L, "브라운", "bri@abc.com"),
                LocalDate.of(2100, 3, 1),
                new ReservationTime(1L, LocalTime.of(19, 0)),
                new Theme(1L, "레벨1 탈출", "레벨1 탈출하기", "https://img.jpg"),
                ReservationStatus.RESERVED);
        given(reservationRepository.findByMember_id(member.getId()))
                .willReturn(List.of(reservation1, reservation2));

        List<MyReservationResponse> actual = reservationService.findMyReservations(member.getId());
        List<MyReservationResponse> expected = List.of(
                MyReservationResponse.from(reservation1),
                MyReservationResponse.from(reservation2));

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("에약을 생성할 수 있다.")
    @Test
    void createReservationTest() {
        Member member = new Member(1L, "브라운", "brown@abc.com");
        ReservationTime time = new ReservationTime(1L, LocalTime.of(19, 0));
        Theme theme = new Theme(1L, "레벨2 탈출", "레벨2 탈출하기", "https://img.jpg");
        LocalDate date = LocalDate.now().plusDays(7);
        AdminReservationCreateRequest request = new AdminReservationCreateRequest(1L, date, 1L, 1L);

        given(memberRepository.findById(1L))
                .willReturn(Optional.of(member));
        given(timeRepository.findById(1L))
                .willReturn(Optional.of(time));
        given(themeRepository.findById(1L))
                .willReturn(Optional.of(theme));
        given(reservationRepository.save(any())).willReturn(new Reservation(
                1L, member,
                LocalDate.of(2024, 8, 15), time, theme, ReservationStatus.RESERVED));

        ReservationResponse expected = new ReservationResponse(
                1L, new MemberResponse(1L, "브라운"), LocalDate.of(2024, 8, 15),
                new TimeResponse(1L, LocalTime.of(19, 0)),
                new ThemeResponse(1L, "레벨2 탈출", "레벨2 탈출하기", "https://img.jpg"));

        ReservationResponse actual = reservationService.createAdminReservation(request);

        assertThat(actual).isEqualTo(expected);
    }

    @DisplayName("예약 생성 시, memberId에 해당하는 멤버가 없다면 예외를 던진다.")
    @Test
    void createReservationTest_whenMemberNotExist() {
        LocalDate date = LocalDate.now().plusDays(7);
        AdminReservationCreateRequest request = new AdminReservationCreateRequest(1L, date, 1L, 1L);
        given(memberRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.createAdminReservation(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 멤버가 존재하지 않습니다.");
    }

    @DisplayName("예약 생성 시, timeId에 해당하는 예약 시간이 없다면 예외를 던진다.")
    @Test
    void createReservationTest_whenTimeNotExist() {
        LocalDate date = LocalDate.now().plusDays(7);
        AdminReservationCreateRequest request = new AdminReservationCreateRequest(1L, date, 1L, 1L);
        given(memberRepository.findById(1L))
                .willReturn(Optional.of(new Member(1L, "브라운", "brown@abc.com")));
        given(timeRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.createAdminReservation(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 예약 시간이 존재하지 않습니다.");
    }

    @DisplayName("예약 생성 시, themeId에 해당하는 테마가 없다면 예외를 던진다.")
    @Test
    void createReservationTest_whenThemeNotExist() {
        LocalDate date = LocalDate.now().plusDays(7);
        AdminReservationCreateRequest request = new AdminReservationCreateRequest(1L, date, 1L, 1L);
        given(memberRepository.findById(1L))
                .willReturn(Optional.of(new Member(1L, "브라운", "brown@abc.com")));
        given(timeRepository.findById(1L))
                .willReturn(Optional.of(new ReservationTime(1L, LocalTime.of(19, 0))));
        given(themeRepository.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> reservationService.createAdminReservation(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 테마가 존재하지 않습니다.");
    }

    @DisplayName("예약 생성 시, 예약 시간이 현재 시간 이전이라면 예외를 던진다.")
    @Test
    void createReservationTest_whenDateTimeIsBefore() {
        LocalDate date = LocalDate.now().minusDays(7);
        AdminReservationCreateRequest request = new AdminReservationCreateRequest(1L, date, 1L, 1L);
        given(memberRepository.findById(1L))
                .willReturn(Optional.of(new Member(1L, "브라운", "brown@abc.com")));
        given(timeRepository.findById(1L))
                .willReturn(Optional.of(new ReservationTime(1L, LocalTime.of(19, 0))));
        given(themeRepository.findById(1L))
                .willReturn(Optional.of(new Theme(1L, "레벨2 탈출", "레벨2 탈출하기", "https://img.jpg")));

        assertThatThrownBy(() -> reservationService.createAdminReservation(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("예약은 현재 시간 이후여야 합니다.");
    }


    @DisplayName("예약 생성 시, 해당 예약 날짜와 시간에 이미 예약된 테마라면 예외를 던진다.")
    @Test
    void createReservationTest_whenExistsDateAndTimeAndTheme() {
        LocalDate date = LocalDate.now().plusDays(7);
        AdminReservationCreateRequest request = new AdminReservationCreateRequest(1L, date, 1L, 1L);
        given(memberRepository.findById(1L))
                .willReturn(Optional.of(new Member(1L, "브라운", "brown@abc.com")));
        given(timeRepository.findById(1L))
                .willReturn(Optional.of(new ReservationTime(1L, LocalTime.of(19, 0))));
        given(themeRepository.findById(1L))
                .willReturn(Optional.of(new Theme(1L, "레벨2 탈출", "레벨2 탈출하기", "https://img.jpg")));
        given(reservationRepository.existsByDateAndTime_idAndTheme_id(date, 1L, 1L))
                .willReturn(true);

        assertThatThrownBy(() -> reservationService.createAdminReservation(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("해당 날짜와 시간에 이미 예약된 테마입니다.");
    }
}
