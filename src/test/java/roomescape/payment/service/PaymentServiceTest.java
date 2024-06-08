package roomescape.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static roomescape.util.Fixture.HORROR_THEME;
import static roomescape.util.Fixture.KAKI;
import static roomescape.util.Fixture.ORDER_ID;
import static roomescape.util.Fixture.PAYMENT_KEY;
import static roomescape.util.Fixture.RESERVATION_CANCEL_REASON;
import static roomescape.util.Fixture.RESERVATION_HOUR_10;
import static roomescape.util.Fixture.TODAY;

import java.math.BigDecimal;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import roomescape.config.DatabaseCleaner;
import roomescape.member.domain.Member;
import roomescape.member.repository.MemberRepository;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentCurrency;
import roomescape.payment.domain.PaymentStatus;
import roomescape.payment.dto.PaymentRequest;
import roomescape.payment.dto.PaymentSaveResponse;
import roomescape.payment.dto.TossPaymentResponse;
import roomescape.payment.exception.PaymentFailException;
import roomescape.payment.repository.PaymentRepository;
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
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentService paymentService;

    @MockBean
    private TossPaymentClient tossPaymentClient;

    @AfterEach
    void init() {
        databaseCleaner.cleanUp();
    }

    @DisplayName("결제")
    @Nested
    class PayTest {

        @DisplayName("결제에 성공하면 결제 정보를 저장한다.")
        @Test
        void payForReservation() {
            Member kaki = memberRepository.save(KAKI);
            ReservationTime hour10 = reservationTimeRepository.save(RESERVATION_HOUR_10);
            Theme horrorTheme = themeRepository.save(HORROR_THEME);
            Reservation reservation = reservationRepository.save(new Reservation(kaki, TODAY, horrorTheme, hour10, ReservationStatus.SUCCESS));

            PaymentRequest paymentRequest = new PaymentRequest(ORDER_ID, BigDecimal.valueOf(1000), PAYMENT_KEY);
            TossPaymentResponse tossPaymentResponse = new TossPaymentResponse(PAYMENT_KEY, ORDER_ID,"간편결제", PaymentCurrency.KRW, BigDecimal.valueOf(1000));

            doReturn(tossPaymentResponse).when(tossPaymentClient)
                    .requestPayment(paymentRequest);

            PaymentSaveResponse paymentSaveResponse = paymentService.payForReservation(paymentRequest, reservation);

            assertAll(
                    () -> assertThat(paymentSaveResponse.paymentKey()).isEqualTo(tossPaymentResponse.paymentKey()),
                    () -> assertThat(paymentSaveResponse.status()).isEqualTo(PaymentStatus.PAID),
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

            PaymentRequest paymentRequest = new PaymentRequest(ORDER_ID, BigDecimal.valueOf(1000), PAYMENT_KEY);

            doThrow(PaymentFailException.class).when(tossPaymentClient)
                    .requestPayment(paymentRequest);

            assertThatThrownBy(() -> paymentService.payForReservation(paymentRequest, reservation))
                    .isInstanceOf(PaymentFailException.class);
        }

        @DisplayName("결제 완료된 예약을 추가로 결제할 경우 예외가 발생한다..")
        @Test
        void payFailForDuplicatedPayment() {
            Member kaki = memberRepository.save(KAKI);
            ReservationTime hour10 = reservationTimeRepository.save(RESERVATION_HOUR_10);
            Theme horrorTheme = themeRepository.save(HORROR_THEME);
            Reservation reservation = reservationRepository.save(new Reservation(kaki, TODAY, horrorTheme, hour10, ReservationStatus.SUCCESS));

            PaymentRequest paymentRequest = new PaymentRequest(ORDER_ID, BigDecimal.valueOf(1000), PAYMENT_KEY);
            TossPaymentResponse tossPaymentResponse = new TossPaymentResponse(PAYMENT_KEY, ORDER_ID,"간편결제",  PaymentCurrency.KRW, BigDecimal.valueOf(1000));

            doReturn(tossPaymentResponse).when(tossPaymentClient)
                    .requestPayment(paymentRequest);

            paymentService.payForReservation(paymentRequest, reservation);

            assertThatThrownBy(() -> paymentService.payForReservation(paymentRequest, reservation))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @DisplayName("결제 취소")
    @Nested
    class PayCancelTest {

        @DisplayName("결제를 취소하면 결제 상태가 CANCEL로 변경된다.")
        @Test
        void cancel() {
            Member kaki = memberRepository.save(KAKI);
            ReservationTime hour10 = reservationTimeRepository.save(RESERVATION_HOUR_10);
            Theme horrorTheme = themeRepository.save(HORROR_THEME);
            Reservation reservation = reservationRepository.save(new Reservation(kaki, TODAY, horrorTheme, hour10, ReservationStatus.SUCCESS));

            PaymentRequest paymentRequest = new PaymentRequest(ORDER_ID, BigDecimal.valueOf(1000), PAYMENT_KEY);
            TossPaymentResponse tossPaymentResponse = new TossPaymentResponse(PAYMENT_KEY, ORDER_ID,"간편결제",  PaymentCurrency.KRW, BigDecimal.valueOf(1000));

            doReturn(tossPaymentResponse).when(tossPaymentClient)
                    .requestPayment(paymentRequest);

            paymentService.payForReservation(paymentRequest, reservation);

            paymentService.cancel(reservation.getId(), RESERVATION_CANCEL_REASON);

            Payment canceledPayment = paymentRepository.findByPaymentKey(PAYMENT_KEY).get();

            assertThat(canceledPayment.getStatus()).isEqualTo(PaymentStatus.CANCELED);
        }

        @DisplayName("결제 되지 않은 예약을 취소할 경우 예외가 발생한다.")
        @Test
        void cancelExceptionForNotExistReservationId() {
            Member kaki = memberRepository.save(KAKI);
            ReservationTime hour10 = reservationTimeRepository.save(RESERVATION_HOUR_10);
            Theme horrorTheme = themeRepository.save(HORROR_THEME);
            Reservation reservation = reservationRepository.save(new Reservation(kaki, TODAY, horrorTheme, hour10, ReservationStatus.SUCCESS));

            assertThatThrownBy(() -> paymentService.cancel(reservation.getId(), RESERVATION_CANCEL_REASON))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
