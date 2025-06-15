package roomescape.payment.service.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import roomescape.common.exception.PaymentException;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberEmail;
import roomescape.member.domain.MemberName;
import roomescape.member.domain.Role;
import roomescape.payment.domain.PaymentVerification;
import roomescape.payment.repository.PaymentVerificationRepository;

@DataJpaTest
class PaymentQueryUseCaseTest {

    @Autowired
    private PaymentVerificationRepository paymentVerificationRepository;

    @Autowired
    private EntityManager entityManager;

    private PaymentQueryUseCase paymentQueryUseCase;

    @BeforeEach
    void setUp() {
        paymentQueryUseCase = new PaymentQueryUseCase(paymentVerificationRepository);
    }

    @DisplayName("orderId를 통해 예약 검증을 가져온다.")
    @Test
    public void getPaymentVerificationByOrderId() {
        // given
        final String orderId = "orderId";
        final Member member = Member.withoutId(
                MemberName.from("테스트"),
                MemberEmail.from("test@test.com"),
                Role.MEMBER
        );

        entityManager.persist(member);
        entityManager.flush();

        paymentVerificationRepository.save(new PaymentVerification(orderId, 1000, member));

        // when
        final PaymentVerification actual = paymentQueryUseCase.getPaymentVerificationByOrderId(orderId);

        // then
        assertAll(
                () -> assertThat(actual.getOrderId()).isEqualTo(orderId),
                () -> assertThat(actual.getAmount()).isEqualTo(1000),
                () -> assertThat(actual.getMember()).isNotNull(),
                () -> assertThat(actual.getMember().getName().getValue()).isEqualTo("테스트")
        );
    }

    @DisplayName("orderId에 해당하는 예약 검증이 존재하지 않는다면 예외가 발생한다.")
    @Test
    public void getPaymentVerificationOrThrowIfOrderIdNotExists() {
        // given
        final String orderId = "orderId";
        final Member member = Member.withoutId(
                MemberName.from("테스트"),
                MemberEmail.from("test@test.com"),
                Role.MEMBER
        );

        entityManager.persist(member);
        entityManager.flush();

        paymentVerificationRepository.save(new PaymentVerification(orderId, 1000, member));

        // when & then
        assertThatThrownBy(() -> paymentQueryUseCase.getPaymentVerificationByOrderId("invalidOrderId"))
                .isInstanceOf(PaymentException.class)
                .satisfies(e -> {
                    final PaymentException ex = (PaymentException) e;
                    assertThat(ex.getStatusCode()).isEqualTo(NOT_FOUND);
                });
    }
}
