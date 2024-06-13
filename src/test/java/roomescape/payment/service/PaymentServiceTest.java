package roomescape.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.when;
import static roomescape.Fixture.HORROR_THEME;
import static roomescape.Fixture.MEMBER_JOJO;
import static roomescape.Fixture.RESERVATION_TIME_10_00;
import static roomescape.Fixture.TOMORROW;

import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import roomescape.common.util.DatabaseCleaner;
import roomescape.member.domain.Member;
import roomescape.member.repository.MemberRepository;
import roomescape.payment.domain.Payment;
import roomescape.payment.repository.PaymentRepository;
import roomescape.payment.service.dto.request.PaymentConfirmRequest;
import roomescape.payment.service.dto.resonse.PaymentConfirmResponse;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.Theme;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.repository.ReservationTimeRepository;
import roomescape.reservation.repository.ThemeRepository;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
@ActiveProfiles("test")
class PaymentServiceTest {

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @MockBean
    private TossPaymentClient tossPaymentClient;

    @AfterEach
    void init() {
        databaseCleaner.cleanUp();
    }

    @DisplayName("결제 시, 토스 결제 요청이 성공하면 payment 엔티티를 반환한다.")
    @Test
    void confirmPayment() {
        Member jojo = memberRepository.save(MEMBER_JOJO);
        ReservationTime reservationTime = reservationTimeRepository.save(RESERVATION_TIME_10_00);
        Theme theme = themeRepository.save(HORROR_THEME);
        Reservation reservation = reservationRepository.save(
                new Reservation(jojo, TOMORROW, theme, reservationTime));

        PaymentConfirmRequest confirmRequest = new PaymentConfirmRequest("paymentKey", "orderId", 1000);

        when(tossPaymentClient.confirmPayment(confirmRequest)).thenReturn(
                new PaymentConfirmResponse(
                        confirmRequest.paymentKey(),
                        confirmRequest.orderId(),
                        confirmRequest.amount(),
                        "orderName",
                        "DONE",
                        "2024-02-13T12:17:57+09:00",
                        "2024-02-13T12:18:14+09:00"
                )
        );

        Payment payment = paymentService.confirm(confirmRequest, reservation);

        assertAll(
                () -> assertThat(payment.getPaymentKey()).isEqualTo("paymentKey"),
                () -> assertThat(payment.getOrderId()).isEqualTo("orderId"),
                () -> assertThat(payment.getAmount()).isEqualTo(1000),
                () -> assertThat(payment.getReservation().getId()).isEqualTo(reservation.getId())
        );
    }

    @DisplayName("예약 id로 결제 내역을 삭제한다.")
    @Test
    void deleteByReservationId() {
        Member jojo = memberRepository.save(MEMBER_JOJO);
        ReservationTime reservationTime = reservationTimeRepository.save(RESERVATION_TIME_10_00);
        Theme theme = themeRepository.save(HORROR_THEME);
        Reservation reservation = reservationRepository.save(new Reservation(jojo, TOMORROW, theme, reservationTime));
        Payment payment = paymentRepository.save(new Payment("paymentKey", "orderId", 10000L, reservation));

        paymentService.deleteByReservationId(reservation.getId());

        Optional<Payment> savedPayment = paymentRepository.findById(payment.getId());
        assertThat(savedPayment).isEmpty();
    }

    @DisplayName("결제 목록을 조회한다.")
    @Test
    void findAllPayment() {
        Member jojo = memberRepository.save(MEMBER_JOJO);
        ReservationTime reservationTime = reservationTimeRepository.save(RESERVATION_TIME_10_00);
        Theme theme = themeRepository.save(HORROR_THEME);
        Reservation reservation = reservationRepository.save(new Reservation(jojo, TOMORROW, theme, reservationTime));
        paymentRepository.save(new Payment("paymentKey", "orderId", 10000L, reservation));

        assertThat(paymentService.findAll()).hasSize(1);
    }
}
