package roomescape.payment.repository;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import roomescape.member.model.Member;
import roomescape.payment.model.PaymentHistory;
import roomescape.reservation.model.Reservation;

@DataJpaTest
class PaymentHistoryRepositoryTest {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PaymentHistoryRepository paymentHistoryRepository;

    @BeforeEach
    void setUp() {
        paymentHistoryRepository.deleteAllInBatch();
    }

    @DisplayName("회원 아이디로 모든 예약 이력을 조회한다.")
    @Test
    void findAllByMemberId() {
        //given
        final Member member = entityManager.find(Member.class, 1L);

        final Reservation reservation1 = entityManager.find(Reservation.class, 5L);
        final Reservation reservation2 = entityManager.find(Reservation.class, 12L);
        final Reservation reservation3 = entityManager.find(Reservation.class, 13L);

        final PaymentHistory paymentHistory1 =
                new PaymentHistory("order1", "paymentKey", "DONE", 100L, LocalDateTime.now(), reservation1);
        final PaymentHistory paymentHistory2 =
                new PaymentHistory("order2", "paymentKey", "DONE", 100L, LocalDateTime.now(), reservation2);
        final PaymentHistory paymentHistory3 =
                new PaymentHistory("order3", "paymentKey", "DONE", 100L, LocalDateTime.now(), reservation3);

        entityManager.persist(paymentHistory1);
        entityManager.persist(paymentHistory2);
        entityManager.persist(paymentHistory3);

        //when
        final List<PaymentHistory> results = paymentHistoryRepository.findAllByReservation_Member(member);

        //then
        assertThat(results).hasSize(2);
    }
}
