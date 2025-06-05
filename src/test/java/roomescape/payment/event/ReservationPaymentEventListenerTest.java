package roomescape.payment.event;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.concurrent.TimeUnit;
import org.assertj.core.api.SoftAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import roomescape.common.CleanUp;
import roomescape.fixture.db.ReservationDbFixture;
import roomescape.payment.client.TossPaymentClient;
import roomescape.payment.dto.TossPaymentResponse;
import roomescape.payment.exception.PaymentServerException;
import roomescape.payment.repository.OrdersRepository;
import roomescape.reservation.controller.request.PaymentInfoRequest;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.repository.ReservationRepository;

@SpringBootTest(webEnvironment = WebEnvironment.NONE)
class ReservationPaymentEventListenerTest {

    @Autowired
    private ReservationDbFixture reservationDbFixture;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private OrdersRepository ordersRepository;

    @MockitoBean
    private TossPaymentClient tossPaymentClient;

    @Autowired
    private ReservationPaymentEventListener reservationPaymentEventListener;

    @Autowired
    private CleanUp cleanUp;

    @BeforeEach
    void setUp() {
        cleanUp.all();
    }

    @Test
    void 결제_이벤트_처리_시_예약상태가_RESERVED로_변경된다() {
        // Arrange
        Reservation 예약대기 = reservationDbFixture.pending();
        String 결제키 = "paymentKey";
        String 주문ID = "orderId";
        TossPaymentRequestedEvent 결제이벤트 = new TossPaymentRequestedEvent(
                예약대기.getId(),
                new PaymentInfoRequest(결제키, 주문ID, 10000L, "NORMAL")
        );
        TossPaymentResponse 결제응답 = new TossPaymentResponse(주문ID, 결제키);
        given(tossPaymentClient.getPayment(any())).willReturn(결제응답);
        given(tossPaymentClient.getPaymentConfirm(any())).willReturn(결제응답);

        // Act
        reservationPaymentEventListener.handlePaymentEvent(결제이벤트);

        // Assert
        await().atMost(1, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    Reservation reservation = reservationRepository.findById(예약대기.getId()).get();

                    SoftAssertions.assertSoftly(softly -> {
                        softly.assertThat(reservation.getStatus())
                                .as("예약 상태가 RESERVED로 변경되어야 합니다.")
                                .isEqualTo(ReservationStatus.RESERVED);
                        softly.assertThat(reservation.getOrders().getPaymentKey())
                                .as("결제 성공 시 결제 키가 저장되어야 합니다.")
                                .isEqualTo(결제키);
                        assertThat(ordersRepository.findByPaymentKey(결제키).isPresent())
                                .as("결제키로 주문이 저장되어야 합니다.")
                                .isTrue();
                    });
                });
    }

    @Test
    void 결제_실패_시에는_PAYMENT_FAILED로_변경된다() {
        // Arrange
        Reservation 예약대기 = reservationDbFixture.pending();
        String 결제키 = "failPaymentKey";
        String 주문ID = "failOrderId";
        TossPaymentRequestedEvent 결제이벤트 = new TossPaymentRequestedEvent(
                예약대기.getId(),
                new PaymentInfoRequest(결제키, 주문ID, 10000L, "NORMAL")
        );
        given(tossPaymentClient.getPayment(any())).willThrow(new PaymentServerException("결제 실패"));

        // Act
        reservationPaymentEventListener.handlePaymentEvent(결제이벤트);

        // Assert
        await().atMost(1, TimeUnit.SECONDS)
                .untilAsserted(() -> {
                    Reservation reservation = reservationRepository.findById(예약대기.getId()).get();
                    assertThat(reservation.getStatus())
                            .as("결제 실패 시 예약 상태가 PAYMENT_FAILED로 변경되어야 합니다.")
                            .isEqualTo(ReservationStatus.PAYMENT_FAILED);
                    assertThat(reservation.getOrders().getPaymentKey())
                            .as("결제 실패 시 결제 키가 저장되어야 합니다.")
                            .isEqualTo(결제키);
                });
    }
}
