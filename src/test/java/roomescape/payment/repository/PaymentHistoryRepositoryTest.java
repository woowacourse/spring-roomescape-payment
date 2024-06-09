package roomescape.payment.repository;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import roomescape.member.model.Member;
import roomescape.member.model.MemberRole;
import roomescape.payment.model.PaymentHistory;

@DataJpaTest
class PaymentHistoryRepositoryTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PaymentHistoryRepository paymentHistoryRepository;

    @DisplayName("회원 아이디로 모든 예약 이력을 조회한다.")
    @Test
    void findAllByMemberId() {
        //given
        entityManager.persist(new Member(MemberRole.ADMIN, "daon", "1234", "test@test.com"));
        entityManager.persist(new Member(MemberRole.ADMIN, "charlie", "1234", "test@test.com"));
        Member member1 = entityManager.find(Member.class, 1);

        PaymentHistory paymentHistory1 =
                new PaymentHistory("order1", "paymentKey", 100L, LocalDateTime.now(), null, member1);
        PaymentHistory paymentHistory2 =
                new PaymentHistory("order2", "paymentKey", 100L, LocalDateTime.now(), null, member1);
        PaymentHistory paymentHistory3 =
                new PaymentHistory("order3", "paymentKey", 100L, LocalDateTime.now(), null,
                        entityManager.find(Member.class, 2));

        entityManager.persist(paymentHistory1);
        entityManager.persist(paymentHistory2);
        entityManager.persist(paymentHistory3);

        //when
        List<PaymentHistory> results = paymentHistoryRepository.findAllByMember_Id(member1.getId());

        //then
        assertThat(results).hasSize(4);
    }
}
