package roomescape.application.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.dto.LoginMember;
import roomescape.dto.response.MemberReservationResponseDto;
import roomescape.dto.response.ReservationTicketResponseDto;
import roomescape.infrastructure.db.MemberJpaRepository;
import roomescape.infrastructure.db.ThemeJpaRepository;
import roomescape.infrastructure.db.TossPaymentJpaRepository;
import roomescape.infrastructure.db.WaitingJpaRepository;
import roomescape.model.Member;
import roomescape.model.PaymentTargetType;
import roomescape.model.Reservation;
import roomescape.model.ReservationTicket;
import roomescape.model.ReservationTime;
import roomescape.model.Role;
import roomescape.model.Theme;
import roomescape.model.TossPayment;
import roomescape.model.Waiting;
import roomescape.persistence.repository.ReservationTicketRepository;
import roomescape.persistence.repository.ReservationTimeRepository;

class ReservationTicketServiceTest extends ServiceTest {

    @Autowired
    ReservationTicketService reservationTicketService;

    @Autowired
    ReservationTicketRepository reservationTicketRepository;

    @Autowired
    ReservationTimeRepository reservationTimeRepository;

    @Autowired
    MemberJpaRepository memberJpaRepository;

    @Autowired
    WaitingJpaRepository waitingJpaRepository;

    @Autowired
    TossPaymentJpaRepository tossPaymentJpaRepository;

    @Autowired
    private ThemeJpaRepository themeJpaRepository;

    @DisplayName("예약을 취소한다")
    @Test
    void test3() {
        // given
        Member member = saveMember(1L);
        Theme theme = saveTheme("공포 테마");
        ReservationTime time = saveTime(LocalTime.of(10, 0));
        LocalDate date = LocalDate.now().plusDays(1);

        LoginMember loginMember = new LoginMember(member.getId(), member.getName(),
                member.getEmail(), member.getRole());

        ReservationTicket reservationTicket = reservationTicketRepository.save(
                new ReservationTicket(
                        new Reservation(
                                LocalDate.now().plusDays(1),
                                time,
                                theme,
                                member,
                                LocalDate.now()
                        )
                )
        );

        // when
        this.reservationTicketService.cancelReservationTicket(reservationTicket.getId());

        // then
        List<ReservationTicketResponseDto> reservations = this.reservationTicketService.getReservationTickets();
        assertThat(reservations).isEmpty();
    }

    @DisplayName("사용자가 예약한 예약 내역을 모두 가져온다")
    @Test
    void test6() {
        //given
        ReservationTime reservationTime = saveTime(LocalTime.of(12, 30));
        Theme theme = saveTheme("테마");
        Member member = saveMember(1L);

        ReservationTicket reservationTicket = new ReservationTicket(
                new Reservation(LocalDate.now().plusDays(1), reservationTime, theme,
                        member, LocalDate.now()));
        ReservationTicket savedReservationTicket = reservationTicketRepository.save(
                reservationTicket);
        TossPayment savedTossPayment = tossPaymentJpaRepository.save(
                new TossPayment(
                        "paymentKey",
                        "orderId",
                        1000L,
                        savedReservationTicket.getId(),
                        PaymentTargetType.RESERVATION_TICKET
                ));

        LoginMember loginMember = new LoginMember(member);

        //when
        List<MemberReservationResponseDto> response = reservationTicketService.getReservationTicketsOfMember(
                loginMember);

        List<MemberReservationResponseDto> comparedResponse = List.of(
                new MemberReservationResponseDto(savedReservationTicket, savedTossPayment));

        //then
        assertAll(
                () -> assertThat(response).hasSize(1),
                () -> assertThat(response).isEqualTo(comparedResponse)
        );
    }

    @DisplayName("사용자의 예약 내역 삭제 시에 가장 높은 우선순위의 웨이팅을 예약으로 전환해 저장한다")
    @Test
    void test7() {
        //given
        Member user = saveMember(1L);
        Theme theme = saveTheme("테마");
        ReservationTime reservationTime = saveTime(LocalTime.of(12, 30));

        Waiting firstWaiting = waitingJpaRepository.save(new Waiting(
                LocalDateTime.now(),
                new Reservation(
                        LocalDate.now().plusDays(1),
                        reservationTime,
                        theme,
                        user,
                        LocalDate.now()
                )
        ));

        Waiting secondWaiting = waitingJpaRepository.save(new Waiting(
                LocalDateTime.now().plusHours(1),
                new Reservation(
                        LocalDate.now().plusDays(1),
                        reservationTime,
                        theme,
                        user,
                        LocalDate.now()
                )
        ));

        ReservationTicket reservationTicket = reservationTicketRepository.save(
                new ReservationTicket(new Reservation(
                        LocalDate.now().plusDays(1),
                        reservationTime,
                        theme,
                        user,
                        LocalDate.now()
                )));

        // when
        reservationTicketService.cancelReservationTicket(reservationTicket.getId());

        // then
        List<ReservationTicket> allReservationTickets = reservationTicketRepository.findAll();
        Optional<ReservationTicket> foundReservation = allReservationTickets.stream()
                .filter(reservation1 -> reservation1.getReservationTime().getId()
                        .equals(reservationTime.getId()))
                .filter(reservation1 -> reservation1.getTheme().getId().equals(theme.getId()))
                .filter(reservation1 -> reservation1.getMember().getId().equals(user.getId()))
                .findAny();

        assertAll(
                () -> assertThat(waitingJpaRepository.findAll()).doesNotContain(firstWaiting),
                () -> assertThat(allReservationTickets).doesNotContain(reservationTicket),
                () -> assertThat(foundReservation).isPresent()
        );
    }

    private Member saveMember(Long tmp) {
        Member member = new Member("이름" + tmp, "이메일" + tmp, "비밀번호" + tmp, Role.USER);
        memberJpaRepository.save(member);

        return member;
    }

    private Theme saveTheme(String name) {
        Theme theme = new Theme(name, "description", "image");
        return themeJpaRepository.save(theme);
    }

    private ReservationTime saveTime(LocalTime reservationTime) {
        ReservationTime time = new ReservationTime(reservationTime);
        reservationTimeRepository.save(time);

        return time;
    }
}
