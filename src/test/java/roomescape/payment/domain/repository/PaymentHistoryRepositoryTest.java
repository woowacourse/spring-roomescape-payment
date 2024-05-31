package roomescape.payment.domain.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static roomescape.fixture.MemberFixture.getMemberChoco;

import java.math.BigDecimal;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import roomescape.global.entity.Price;
import roomescape.member.domain.Member;
import roomescape.member.domain.repository.MemberRepository;
import roomescape.payment.domain.PaymentHistory;
import roomescape.payment.domain.PaymentStatus;
import roomescape.payment.domain.PaymentType;
import roomescape.util.RepositoryTest;

@DisplayName("결제 내역 히스토리 레포지토리 테스트")
class PaymentHistoryRepositoryTest extends RepositoryTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PaymentHistoryRepository paymentHistoryRepository;

    @DisplayName("결제 내역 히스토리를 저장한다.")
    @Test
    void save() {
        //given & when
        String paymentKey = "paymentKey";
        PaymentType paymentType = PaymentType.CARD;
        PaymentStatus paymentStatus = PaymentStatus.PAID;
        Price price = new Price(BigDecimal.valueOf(10000));
        Member member = memberRepository.save(getMemberChoco());

        PaymentHistory paymentHistory = paymentHistoryRepository.save(
                new PaymentHistory(paymentKey, paymentType, paymentStatus, price, member)
        );

        //then
        assertAll(
                () -> assertThat(paymentHistory.getId()).isNotNull(),
                () -> assertThat(paymentHistory.getPaymentKey()).isEqualTo(paymentKey),
                () -> assertThat(paymentHistory.getPaymentType()).isEqualTo(paymentType),
                () -> assertThat(paymentHistory.getPaymentStatus()).isEqualTo(paymentStatus),
                () -> assertThat(paymentHistory.getPrice()).isEqualTo(price),
                () -> assertThat(paymentHistory.getMember()).isEqualTo(member)
        );
    }
}
