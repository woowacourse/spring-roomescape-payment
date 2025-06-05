package roomescape.payment.service.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberEmail;
import roomescape.member.domain.MemberName;
import roomescape.member.domain.Role;
import roomescape.member.service.usecase.MemberQueryUseCase;
import roomescape.payment.controller.dto.PaymentVerificationWebResponse;
import roomescape.payment.domain.Payment;
import roomescape.payment.repository.PaymentRepository;
import roomescape.payment.repository.PaymentVerificationRepository;
import roomescape.payment.service.dto.CreatePaymentServiceRequest;

@DataJpaTest
class PaymentCommandUseCaseTest {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private PaymentVerificationRepository paymentVerificationRepository;

    @Autowired
    private EntityManager entityManager;

    @Mock
    private MemberQueryUseCase memberQueryUseCase;

    private PaymentCommandUseCase paymentCommandUseCase;

    @BeforeEach
    void setUp() {
        paymentCommandUseCase = new PaymentCommandUseCase(
                paymentRepository,
                paymentVerificationRepository,
                memberQueryUseCase
        );
    }

    @DisplayName("결제를 추가한다.")
    @Test
    void create() {
        // given
        final CreatePaymentServiceRequest request = new CreatePaymentServiceRequest(
                "paymentKey", "orderId", 1000
        );

        // when
        final Payment actual = paymentCommandUseCase.create(request);

        // then
        assertAll(
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getPaymentKey()).isEqualTo("paymentKey"),
                () -> assertThat(actual.getOrderId()).isEqualTo("orderId"),
                () -> assertThat(actual.getAmount()).isEqualTo(1000)
        );
    }

    @DisplayName("결제 검증을 추가한다.")
    @Test
    void createPaymentVerification() {
        // given
        final String orderId = "orderId";
        final int amount = 1000;
        final Long memberId = 1L;

        final Member member = Member.withoutId(
                MemberName.from("테스트"),
                MemberEmail.from("test@test.com"),
                Role.MEMBER
        );

        entityManager.persist(member);
        entityManager.flush();

        when(memberQueryUseCase.get(memberId)).thenReturn(member);

        // when
        final PaymentVerificationWebResponse actual = paymentCommandUseCase.createPaymentVerification(
                orderId, amount, memberId
        );

        // then
        assertAll(
                () -> assertThat(actual.orderId()).isEqualTo(orderId),
                () -> assertThat(actual.amount()).isEqualTo(amount),
                () -> assertThat(actual.memberInfo().id()).isEqualTo(memberId)
        );
    }
}
