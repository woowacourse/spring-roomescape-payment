package roomescape.service.payment;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;
import roomescape.domain.member.MemberRole;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentRepository;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;
import roomescape.domain.reservation.ReservationStatus;
import roomescape.domain.reservationitem.ReservationItem;
import roomescape.domain.reservationitem.ReservationItemRepository;
import roomescape.domain.reservationitem.ReservationTheme;
import roomescape.domain.reservationitem.ReservationThemeRepository;
import roomescape.domain.reservationitem.ReservationTime;
import roomescape.domain.reservationitem.ReservationTimeRepository;

@Transactional
@SpringBootTest
class PaymentServiceTest {

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationItemRepository reservationItemRepository;

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private ReservationThemeRepository reservationThemeRepository;

    @Autowired
    private MemberRepository memberRepository;

    private Member member1;
    private Member member2;
    private ReservationTime time;
    private ReservationTheme theme;
    private ReservationItem item1;
    private ReservationItem item2;
    private Reservation reservation1;
    private Reservation reservation2;
    private Reservation reservation3;
    private Payment payment1;
    private Payment payment2;

    @BeforeEach
    void setUp() {
        member1 = memberRepository.save(new Member("test1@email.com", "password", "test 1", MemberRole.USER));
        member2 = memberRepository.save(new Member("test2@email.com", "password", "test 2", MemberRole.USER));

        time = reservationTimeRepository.save(new ReservationTime(LocalTime.of(10,0)));
        theme = reservationThemeRepository.save(new ReservationTheme("Theme 1", "Description 1", "Thumbnail 1"));

        item1 = reservationItemRepository.save(new ReservationItem(LocalDate.now().plusDays(1), time, theme));
        item2 = reservationItemRepository.save(new ReservationItem(LocalDate.now().plusDays(2), time, theme));

        reservation1 = reservationRepository.save(
                Reservation.builder()
                        .member(member1)
                        .reservationItem(item1)
                        .reservationStatus(ReservationStatus.ACCEPTED)
                        .build()
        );

        reservation2 = reservationRepository.save(
                Reservation.builder()
                        .member(member2)
                        .reservationItem(item2)
                        .reservationStatus(ReservationStatus.ACCEPTED)
                        .build()
        );

        reservation3 = reservationRepository.save(
                Reservation.builder()
                        .member(member1)
                        .reservationItem(item2)
                        .reservationStatus(ReservationStatus.PENDING)
                        .build()
        );

        payment1 = paymentRepository.save(new Payment(reservation1.getId(), "paymentKey1", 25000));
        payment2 = paymentRepository.save(new Payment(reservation2.getId(), "paymentKey2", 30000));
    }

    @Test
    @DisplayName("예약에 대하여 결제가 존재할 때 Map 형식으로 반환한다.")
    void getPaymentMapByReservationsTest() {
        // when
        final Map<Reservation, Payment> payments = paymentService.getPaymentMapByReservations(
                List.of(reservation1, reservation2, reservation3)
        );

        // then
        assertAll(
                () -> assertThat(payments.get(reservation1).getPaymentKey()).isEqualTo(payment1.getPaymentKey()),
                () -> assertThat(payments.get(reservation2).getPaymentKey()).isEqualTo(payment2.getPaymentKey()),
                () -> assertThat(payments.get(reservation3)).isNull()
        );
    }
}