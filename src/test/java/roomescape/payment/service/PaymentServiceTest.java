package roomescape.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static roomescape.fixture.MemberFixture.getMemberChoco;
import static roomescape.fixture.ReservationTimeFixture.getNoon;
import static roomescape.fixture.ThemeFixture.getTheme1;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.member.domain.Member;
import roomescape.member.domain.repository.MemberRepository;
import roomescape.payment.service.dto.PaymentErrorResponse;
import roomescape.payment.service.dto.PaymentRequest;
import roomescape.payment.service.dto.PaymentResponse;
import roomescape.payment.domain.PayAmount;
import roomescape.payment.domain.Payment;
import roomescape.payment.domain.PaymentType;
import roomescape.payment.domain.repository.PaymentRepository;
import roomescape.payment.exception.PaymentException;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.Theme;
import roomescape.util.ServiceTest;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest extends ServiceTest {

    ReservationTime time;
    Theme theme1;
    Member memberChoco;
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private PaymentRepository paymentRepository;

    @BeforeEach
    void setUp() {
        time = reservationTimeRepository.save(getNoon());
        theme1 = themeRepository.save(getTheme1());
        memberChoco = memberRepository.save(getMemberChoco());

    }

    @DisplayName("결제에 성공하면, 응답을 반환한다.")
    @Test
    void successfulPayment() {
        //given
        String paymentKey = "tgen_20240528172021mxEG4";
        String paymentType = "카드";
        long totalAmount = 1000L;
        PaymentRequest paymentRequest = new PaymentRequest(totalAmount, "MC45NTg4ODYxMzA5MTAz", paymentKey);
        PaymentResponse okResponse = new PaymentResponse(paymentKey, "DONE", "MC4wOTA5NzEwMjg3MjQ2", totalAmount, paymentType);
        doReturn(okResponse).when(paymentClient).confirm(any());

        //when
        paymentService.pay(paymentRequest, 1l);

        //then
        Optional<Payment> optionalPayment = paymentRepository.findByPaymentKey(paymentKey);

        assertAll(
                () -> assertThat(optionalPayment).isNotNull(),
                () -> assertThat(optionalPayment.get().getPaymentType()).isEqualTo(PaymentType.from(paymentType)),
                () -> assertThat(optionalPayment.get().getAmount()).isEqualTo(PayAmount.from(totalAmount))
        );
    }

    @DisplayName("결제 중 예외가 발생한다.")
    @Test
    void throw_exception() {
        //given
        doThrow(new PaymentException(
                new PaymentErrorResponse("NOT_FOUND_PAYMENT", "결제 시간이 만료되어 결제 진행 데이터가 존재하지 않습니다.")))
                .when(paymentClient)
                .confirm(any());
        PaymentRequest paymentRequest = new PaymentRequest(1000L, "MC45NTg4ODYxMzA5MTAz", "tgen_20240528172021mxEG4");

        //when&then
        assertThatThrownBy(() -> paymentService.pay(paymentRequest, 1l))
                .isInstanceOf(PaymentException.class);
    }
}
