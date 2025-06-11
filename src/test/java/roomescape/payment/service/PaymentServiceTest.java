package roomescape.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.LocalDate;
import java.time.LocalTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestClient;

import roomescape.common.exception.business.EntityNotFoundException;
import roomescape.member.domain.Member;
import roomescape.member.domain.Role;
import roomescape.member.repository.MemberRepository;
import roomescape.payment.config.RestClientConfig;
import roomescape.payment.domain.Payment;
import roomescape.payment.dto.request.PaymentRequest;
import roomescape.payment.repository.PaymentRepository;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationId;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.repository.ReservationTimeRepository;
import roomescape.theme.domain.Theme;
import roomescape.theme.repository.ThemeRepository;

@ActiveProfiles("test")
@DataJpaTest
@Import({RestClientConfig.class, PaymentService.class})
class PaymentServiceTest {

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private ReservationTimeRepository reservationTimeRepository;
    @Autowired
    private ThemeRepository themeRepository;
    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private RestClient.Builder builder;
    @Autowired
    private PaymentService paymentService;

    @DisplayName("결제 정보를 저장한다.")
    @Test
    void savePayment() {
        // given
        Member member = memberRepository.save(new Member("피케이", "pk@woowa.com", "12341234", Role.ADMIN));
        ReservationTime reservationTime = reservationTimeRepository.save(new ReservationTime(LocalTime.now()));
        Theme theme = themeRepository.save(new Theme(1L, "테마1", "테마1", "www.m.com"));
        Reservation reservation = reservationRepository.save(
                new Reservation(member, LocalDate.now(), reservationTime, theme)
        );

        // when
        paymentService.savePayment(reservation.idValue(), new PaymentRequest("paymentKey", "orderId", 1000L));

        // then
        assertThat(paymentRepository.findAll()).hasSize(1);
    }

    @DisplayName("예약 번호를 통해 결제 정보를 조회한다.")
    @Test
    void getPaymentWithExistsReservation() {
        // given
        Member member = memberRepository.save(new Member("피케이", "pk@woowa.com", "12341234", Role.ADMIN));
        ReservationTime reservationTime = reservationTimeRepository.save(new ReservationTime(LocalTime.now()));
        Theme theme = themeRepository.save(new Theme(1L, "테마1", "테마1", "www.m.com"));
        Reservation reservation = reservationRepository.save(
                new Reservation(member, LocalDate.now(), reservationTime, theme)
        );
        paymentService.savePayment(reservation.idValue(), new PaymentRequest("paymentKey", "orderId", 1000L));

        // when
        Payment payment = paymentService.getPaymentByReservation(reservation.getId());

        // then
        assertThat(payment.getPaymentKey()).isEqualTo("paymentKey");
    }

    @DisplayName("존재하지 않는 예약 번호로 결제 정보를 조회할 수 없다.")
    @Test
    void getPaymentWithNonExistsReservation() {
        // given
        Member member = memberRepository.save(new Member("피케이", "pk@woowa.com", "12341234", Role.ADMIN));
        ReservationTime reservationTime = reservationTimeRepository.save(new ReservationTime(LocalTime.now()));
        Theme theme = themeRepository.save(new Theme(1L, "테마1", "테마1", "www.m.com"));
        Reservation reservation = reservationRepository.save(
                new Reservation(member, LocalDate.now(), reservationTime, theme)
        );
        paymentService.savePayment(reservation.idValue(), new PaymentRequest("paymentKey", "orderId", 1000L));

        // when & then
        assertThatThrownBy(() -> paymentService.getPaymentByReservation(new ReservationId(0L)))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @DisplayName("예약 번호를 통해 결제 정보를 삭제한다.")
    @Test
    void deletePaymentWithExistsReservation() {
        // given
        Member member = memberRepository.save(new Member("피케이", "pk@woowa.com", "12341234", Role.ADMIN));
        ReservationTime reservationTime = reservationTimeRepository.save(new ReservationTime(LocalTime.now()));
        Theme theme = themeRepository.save(new Theme(1L, "테마1", "테마1", "www.m.com"));
        Reservation reservation = reservationRepository.save(
                new Reservation(member, LocalDate.now(), reservationTime, theme)
        );
        paymentService.savePayment(reservation.idValue(), new PaymentRequest("paymentKey", "orderId", 1000L));

        // when
        paymentService.deleteByReservationId(reservation.getId());

        // then
        assertThat(paymentRepository.findByReservationId(reservation.getId())).isEmpty();
    }

    @DisplayName("존재하지 않는 예약 번호로 결제 정보를 삭제할 수 없다.")
    @Test
    void deletePaymentWithNonExistsReservation() {
        // given
        Member member = memberRepository.save(new Member("피케이", "pk@woowa.com", "12341234", Role.ADMIN));
        ReservationTime reservationTime = reservationTimeRepository.save(new ReservationTime(LocalTime.now()));
        Theme theme = themeRepository.save(new Theme(1L, "테마1", "테마1", "www.m.com"));
        Reservation reservation = reservationRepository.save(
                new Reservation(member, LocalDate.now(), reservationTime, theme)
        );
        paymentService.savePayment(reservation.idValue(), new PaymentRequest("paymentKey", "orderId", 1000L));

        // when & then
        assertThatThrownBy(() -> paymentService.deleteByReservationId(new ReservationId(0L)))
                .isInstanceOf(EntityNotFoundException.class);
    }
}
