package roomescape.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static roomescape.util.Fixture.HORROR_THEME;
import static roomescape.util.Fixture.KAKI;
import static roomescape.util.Fixture.RESERVATION_HOUR_10;
import static roomescape.util.Fixture.TODAY;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import roomescape.config.DatabaseCleaner;
import roomescape.member.domain.Member;
import roomescape.member.repository.MemberRepository;
import roomescape.payment.domain.PaymentStatus;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.PaymentSaveResponse;
import roomescape.payment.dto.TossPaymentResponse;
import roomescape.payment.exception.PaymentFailException;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.Theme;
import roomescape.reservation.repository.ReservationRepository;
import roomescape.reservation.repository.ReservationTimeRepository;
import roomescape.reservation.repository.ThemeRepository;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class PaymentServiceTest {

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ReservationTimeRepository reservationTimeRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private PaymentService paymentService;

    @MockBean
    private TossPaymentClient tossPaymentClient;

    @AfterEach
    void init() {
        databaseCleaner.cleanUp();
    }

    @DisplayName("결제에 성공하면 결제 정보를 저장한다.")
    @Test
    void payForReservation() {
        Member kaki = memberRepository.save(KAKI);
        ReservationTime hour10 = reservationTimeRepository.save(RESERVATION_HOUR_10);
        Theme horrorTheme = themeRepository.save(HORROR_THEME);
        Reservation reservation = reservationRepository.save(new Reservation(kaki, TODAY, horrorTheme, hour10, ReservationStatus.SUCCESS));

        PaymentRequest paymentRequest = new PaymentRequest("testOrderId", 1000, "testkey");
        TossPaymentResponse tossPaymentResponse = new TossPaymentResponse("testKey", "testOrderId","간편결제", 1000);

        doReturn(tossPaymentResponse).when(tossPaymentClient)
                .requestPayment(paymentRequest);

        PaymentSaveResponse paymentSaveResponse = paymentService.payForReservation(paymentRequest, reservation);

        assertAll(
                () -> assertThat(paymentSaveResponse.paymentKey()).isEqualTo(tossPaymentResponse.paymentKey()),
                () -> assertThat(paymentSaveResponse.status()).isEqualTo(PaymentStatus.DONE),
                () -> assertThat(paymentSaveResponse.amount()).isEqualTo(tossPaymentResponse.totalAmount())
        );
    }

    @DisplayName("결제에 실패하면 결제 실패 예외가 발생한다.")
    @Test
    void payFailForReservationE() {
        Member kaki = memberRepository.save(KAKI);
        ReservationTime hour10 = reservationTimeRepository.save(RESERVATION_HOUR_10);
        Theme horrorTheme = themeRepository.save(HORROR_THEME);
        Reservation reservation = reservationRepository.save(new Reservation(kaki, TODAY, horrorTheme, hour10, ReservationStatus.SUCCESS));

        PaymentRequest paymentRequest = new PaymentRequest("testOrderId", 1000, "testkey");

        doThrow(PaymentFailException.class).when(tossPaymentClient)
                .requestPayment(paymentRequest);

        assertThatThrownBy(() -> paymentService.payForReservation(paymentRequest, reservation))
                .isInstanceOf(PaymentFailException.class);
    }
}
