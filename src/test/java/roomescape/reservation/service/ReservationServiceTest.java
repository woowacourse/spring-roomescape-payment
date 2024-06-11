package roomescape.reservation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import roomescape.config.DatabaseCleaner;
import roomescape.member.domain.Member;
import roomescape.member.domain.Role;
import roomescape.member.dto.LoginMemberInToken;
import roomescape.member.repository.MemberRepository;
import roomescape.payment.domain.Payment;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.Status;
import roomescape.reservation.domain.Theme;
import roomescape.reservation.dto.request.ReservationCreateRequest;
import roomescape.reservation.dto.response.MyReservationResponse;
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

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private PaymentRepository paymentRepository;

    @AfterEach
    void init() {
        databaseCleaner.cleanUp();
    }

    @Test
    @DisplayName("존재하지 않는 예약 시간에 예약을 하면 예외가 발생한다.")
    void notExistReservationTimeIdExceptionTest() {
        Theme theme = new Theme("공포", "호러 방탈출", "http://asdf.jpg");
        Long themeId = themeRepository.save(theme).getId();

        LoginMemberInToken loginMemberInToken = new LoginMemberInToken(1L, Role.USER, "카키", "kaki@email.com");
        ReservationCreateRequest reservationCreateRequest = new ReservationCreateRequest(
                LocalDate.now(), 1L, 1L, "a", "a", 1000, "a");

        assertThatThrownBy(() -> reservationService.save(reservationCreateRequest, loginMemberInToken))
                .isInstanceOf(IllegalArgumentException.class);
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
        Payment payment1 = paymentRepository.save(new Payment("a", 15000, reservation1));
        Payment payment2 = paymentRepository.save(new Payment("b", 15000, reservation2));

        List<MyReservationResponse> memberReservations = reservationService.findAllByMemberId(member.getId());
        assertThat(memberReservations.size()).isEqualTo(2);
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
        Payment payment = paymentRepository.save(new Payment("a", 10000, reservation2));

        reservationService.delete(reservation2.getId());
        Reservation findReservation = reservationRepository.findById(reservation1.getId()).get();

        Optional<Payment> findPayment = paymentRepository.findByReservationId(reservation2.getId());

        assertAll(
                () -> assertThat(findReservation.getStatus()).isEqualTo(Status.SUCCESS),
                () -> assertThat(findPayment.isEmpty()).isTrue()
        );
    }
}

