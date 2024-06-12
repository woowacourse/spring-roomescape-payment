package roomescape.reservation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
import org.springframework.context.annotation.Import;

import roomescape.config.ClientConfig;
import roomescape.config.DatabaseCleaner;
import roomescape.member.domain.Member;
import roomescape.member.dto.LoginMemberInToken;
import roomescape.member.repository.MemberRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.Status;
import roomescape.reservation.domain.Theme;
import roomescape.reservation.dto.request.FreeReservationCreateRequest;
import roomescape.reservation.dto.response.MyReservationResponse;
import roomescape.reservation.dto.response.ReservationResponse;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.repository.ReservationTimeRepository;
import roomescape.reservation.repository.ThemeRepository;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@Import(value = ClientConfig.class)
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

    @AfterEach
    void tearDown() {
        databaseCleaner.cleanUp();
    }

    @Test
    @DisplayName("무료 예약을 생성한다.")
    void saveReservationWhenFreePayment() {
        Theme theme = themeRepository.save(new Theme("t", "d", "t"));
        ReservationTime time = reservationTimeRepository.save(new ReservationTime(LocalTime.of(1, 0)));
        Member member = memberRepository.save(new Member("n", "e", "p"));
        FreeReservationCreateRequest request
                = new FreeReservationCreateRequest(LocalDate.now().plusDays(1), theme.getId(), time.getId());
        LoginMemberInToken loginMemberInToken = new LoginMemberInToken(member.getId(), member.getRole(),
                member.getName(), member.getEmail());

        ReservationResponse response = reservationService.save(request, loginMemberInToken);

        assertThat(reservationRepository.findById(response.id()))
                .isPresent();
    }

    @Test
    @DisplayName("예약 아이디로 조회 시 존재하지 않는 아이디면 예외가 발생한다.")
    void findByIdExceptionTest() {
        assertThatThrownBy(() -> reservationService.findById(1L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("맴버에 해당하는 모든 무료 예약을 반환한다.")
    void findFreeReservationByMemberIdShouldGetFreeReservation() {
        Theme theme = themeRepository.save(new Theme("a", "a", "a"));
        ReservationTime time = reservationTimeRepository.save(new ReservationTime(LocalTime.of(1, 0)));
        Member member = memberRepository.save(new Member("hogi", "a", "a"));
        Reservation reservation1 = new Reservation(member, LocalDate.now().plusDays(10), theme, time, Status.WAITING);
        reservationRepository.save(reservation1);
        Reservation reservation2 = new Reservation(member, LocalDate.now().plusDays(10), theme, time, Status.SUCCESS);
        reservationRepository.save(reservation2);

        List<MyReservationResponse> memberReservations = reservationService.findFreeReservationByMemberId(
                member.getId());
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

