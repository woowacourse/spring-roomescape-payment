package roomescape.reservation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import static roomescape.member.domain.Role.USER;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;

import roomescape.client.PaymentException;
import roomescape.client.feign.PaymentClient;
import roomescape.config.DatabaseCleaner;
import roomescape.member.domain.Member;
import roomescape.member.dto.LoginMemberInToken;
import roomescape.member.repository.MemberRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.Status;
import roomescape.reservation.domain.Theme;
import roomescape.reservation.dto.request.PaymentRequest;
import roomescape.reservation.dto.request.ReservationCreateRequest;
import roomescape.reservation.dto.response.MyReservationResponse;
import roomescape.reservation.dto.response.PaymentResponse;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.repository.ReservationTimeRepository;
import roomescape.reservation.repository.ThemeRepository;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class ReservationServiceTest {
    @Autowired
    private DatabaseCleaner databaseCleaner;
    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private ReservationTimeRepository reservationTimeRepository;
    @Autowired
    private MemberRepository memberRepository;
    @MockBean
    private PaymentClient paymentClient;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private ReservationService reservationService;

    @AfterEach
    void init() {
        databaseCleaner.cleanUp();
    }

    @Test
    @DisplayName("예약이 생성한다.")
    void saveReservationWhenAccountIsCompleted() {
        Mockito.when(paymentClient.paymentReservation(anyString(), any(PaymentRequest.class)))
                .thenReturn(ResponseEntity.ok(new PaymentResponse("")));

        Theme theme = themeRepository.save(new Theme("t", "d", "t"));
        ReservationTime time = reservationTimeRepository.save(new ReservationTime(LocalTime.of(1, 0)));
        Member member = memberRepository.save(new Member("n", "e", "p"));

        ReservationCreateRequest reservationCreateRequest
                = new ReservationCreateRequest(LocalDate.now().plusDays(1), theme.getId(), time.getId(), "", "", 1000L,
                "");
        LoginMemberInToken loginMemberInToken = new LoginMemberInToken(member.getId(), member.getRole(),
                member.getName(), member.getEmail());

        Long reservationId = reservationService.save(reservationCreateRequest, loginMemberInToken);

        assertThat(reservationRepository.findById(reservationId)).isPresent();
    }

    @Test
    @DisplayName(" 지난 날짜에 대한 예약 시 예외를 발생 시킨다.")
    void saveShouldThrowExceptionWhenReservationDateIsExpire() {
        Mockito.when(paymentClient.paymentReservation(anyString(), any(PaymentRequest.class)))
                .thenReturn(ResponseEntity.ok(new PaymentResponse("")));

        Theme theme = themeRepository.save(new Theme("t", "d", "t"));
        ReservationTime time = reservationTimeRepository.save(new ReservationTime(LocalTime.of(1, 0)));
        Member member = memberRepository.save(new Member("n", "e", "p"));

        ReservationCreateRequest reservationCreateRequest
                = new ReservationCreateRequest(LocalDate.now().minusDays(1), theme.getId(), time.getId(), "", "", 1000L,
                "");
        LoginMemberInToken loginMemberInToken = new LoginMemberInToken(member.getId(), member.getRole(),
                member.getName(), member.getEmail());

        assertThatThrownBy(() -> reservationService.save(reservationCreateRequest, loginMemberInToken))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("존재하지 않는 예약 시간에 예약을 하면 예외가 발생한다.")
    void notExistReservationTimeIdExceptionTest() {
        Mockito.when(paymentClient.paymentReservation(anyString(), any(PaymentRequest.class)))
                .thenReturn(ResponseEntity.ok(new PaymentResponse("")));

        Theme theme = new Theme("공포", "호러 방탈출", "http://asdf.jpg");
        Long themeId = themeRepository.save(theme).getId();

        LoginMemberInToken loginMemberInToken = new LoginMemberInToken(1L, USER, "카키", "kaki@email.com");
        ReservationCreateRequest reservationCreateRequest = new ReservationCreateRequest(
                LocalDate.now(), 1L, 1L, "a", "a", 1000L, "a");

        assertThatThrownBy(() -> reservationService.save(reservationCreateRequest, loginMemberInToken))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("예약 생성 중 결제에 실패하면 예외를 발생시킨다.")
    void saveShouldThrowExceptionWhenAccountFailed() {
        Mockito.when(paymentClient.paymentReservation(anyString(), any(PaymentRequest.class)))
                .thenThrow(PaymentException.class);

        Theme theme = themeRepository.save(new Theme("t", "d", "t"));
        ReservationTime time = reservationTimeRepository.save(new ReservationTime(LocalTime.of(1, 0)));
        Member member = memberRepository.save(new Member("n", "e", "p"));

        ReservationCreateRequest reservationCreateRequest
                = new ReservationCreateRequest(LocalDate.now().plusDays(1), theme.getId(), time.getId(), "", "", 1000L,
                "");
        LoginMemberInToken loginMemberInToken = new LoginMemberInToken(member.getId(), member.getRole(),
                member.getName(), member.getEmail());

        assertThatThrownBy(() -> reservationService.save(reservationCreateRequest, loginMemberInToken))
                .isInstanceOf(PaymentException.class);
    }

    @Test
    @DisplayName("예약 아이디로 조회 시 존재하지 않는 아이디면 예외가 발생한다.")
    void findByIdExceptionTest() {
        assertThatThrownBy(() -> reservationService.findById(1L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("맴버에 해당하는 모든 예약을 반환한다.")
    void findAllByMemberIdTest() {
        Theme theme = themeRepository.save(new Theme("a", "a", "a"));
        ReservationTime time = reservationTimeRepository.save(new ReservationTime(LocalTime.now()));
        Member member = memberRepository.save(new Member("hogi", "a", "a"));
        Reservation reservation1 = reservationRepository.save(
                new Reservation(member, LocalDate.now(), theme, time, Status.WAITING));
        Reservation reservation2 = reservationRepository.save(
                new Reservation(member, LocalDate.now(), theme, time, Status.SUCCESS));

        List<MyReservationResponse> memberReservations = reservationService.findAllByMemberId(member.getId());
        assertThat(memberReservations).hasSize(2);
    }

    @Test
    @DisplayName("앞의 예약이 사라지면 1번 웨이팅이 예약 확정 된다.")
    void deleteTest() {
        Theme theme = themeRepository.save(new Theme("a", "a", "a"));
        ReservationTime time = reservationTimeRepository.save(new ReservationTime(LocalTime.now()));
        Member member = memberRepository.save(new Member("hogi", "a", "a"));
        Reservation reservation1 = reservationRepository.save(
                new Reservation(member, LocalDate.now(), theme, time, Status.WAITING));
        Reservation reservation2 = reservationRepository.save(
                new Reservation(member, LocalDate.now(), theme, time, Status.SUCCESS));

        reservationService.delete(reservation2.getId());
        Optional<Reservation> findReservation = reservationRepository.findById(reservation1.getId());

        assertThat(findReservation)
                .isPresent();
    }
}

