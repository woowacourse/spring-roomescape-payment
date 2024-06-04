package roomescape.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.Sql.ExecutionPhase;
import roomescape.exception.PaymentConfirmFailException;
import roomescape.exception.PaymentCredentialMissMatchException;
import roomescape.fixture.PaymentConfirmFixtures;
import roomescape.member.model.Member;
import roomescape.member.repository.MemberRepository;
import roomescape.payment.dto.SavePaymentCredentialRequest;
import roomescape.payment.infrastructure.PaymentGateway;
import roomescape.payment.model.PaymentCredential;
import roomescape.payment.model.PaymentHistory;
import roomescape.payment.repository.PaymentCredentialRepository;
import roomescape.payment.repository.PaymentHistoryRepository;

@SpringBootTest
@Sql(value = "classpath:test-data.sql", executionPhase = ExecutionPhase.BEFORE_TEST_METHOD)
class PaymentServiceTest {

    @Autowired
    private PaymentCredentialRepository paymentCredentialRepository;

    @Autowired
    private PaymentHistoryRepository paymentHistoryRepository;

    @Autowired
    private MemberRepository memberRepository;

    @MockBean
    private PaymentGateway paymentGateway;

    @Autowired
    private PaymentService paymentService;

    @DisplayName("결제 신용 정보를 저장한다.")
    @Test
    void saveCredential() {
        final SavePaymentCredentialRequest request = new SavePaymentCredentialRequest("orderId", 1000L);

        paymentService.saveCredential(request);
        List<PaymentCredential> result = paymentCredentialRepository.findAll();

        assertThat(result).hasSize(1);
    }

    @DisplayName("결제를 제출하면 결제 신용은 삭제되고 결제 이력은 추가된다.")
    @Test
    void submitPayment() {
        final String orderId = "orderId";
        final long amount = 1000L;
        final String primaryKey = "primaryKey";
        final Member member = memberRepository.findById(1L).orElseThrow();
        final SavePaymentCredentialRequest request = new SavePaymentCredentialRequest(orderId, amount);
        paymentService.saveCredential(request);
        given(paymentGateway.confirm(anyString(), anyLong(), anyString()))
                .willReturn(PaymentConfirmFixtures.getDefaultResponse("1234", 1000L));

        paymentService.submitPayment(orderId, amount, primaryKey, member);
        List<PaymentHistory> paymentHistories = paymentHistoryRepository.findAll();
        List<PaymentCredential> paymentCredentials = paymentCredentialRepository.findAll();

        assertAll(
                () -> assertThat(paymentHistories).hasSize(1),
                () -> assertThat(paymentCredentials).isEmpty(),
                () -> verify(paymentGateway, times(1)).confirm(anyString(), anyLong(), anyString())
        );
    }

    @DisplayName("PG 통신에서 예외가 발생하면 해당 예외와 메세지가 던져진다.")
    @Test
    void submitPaymentWhenGatewayThrowError() {
        final String orderId = "orderId";
        final long amount = 1000L;
        final String primaryKey = "primaryKey";
        final Member member = memberRepository.findById(1L).orElseThrow();
        final SavePaymentCredentialRequest request = new SavePaymentCredentialRequest(orderId, amount);
        paymentService.saveCredential(request);
        given(paymentGateway.confirm(anyString(), anyLong(), anyString()))
                .willThrow(new PaymentConfirmFailException("존재하지 않는 결제입니다.", HttpStatus.BAD_REQUEST));

        assertThatThrownBy(() -> paymentService.submitPayment(orderId, amount, primaryKey, member))
                .isInstanceOf(PaymentConfirmFailException.class)
                .hasMessage("존재하지 않는 결제입니다.");
    }

    @DisplayName("주문 아이디와 수량이 저장되어 있지 않는다면 예외가 발생한다.")
    @Test
    void submitPaymentWithoutPaymentCredentials() {
        final String orderId = "orderId";
        final long amount = 1000L;
        final String primaryKey = "primaryKey";
        final Member member = memberRepository.findById(1L).orElseThrow();

        assertThatThrownBy(() -> paymentService.submitPayment(orderId, amount, primaryKey, member))
                .isInstanceOf(PaymentCredentialMissMatchException.class);
    }
}
