package roomescape.study;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static roomescape.test.fixture.DateFixture.NEXT_DAY;

import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import roomescape.domain.Member;
import roomescape.domain.ReservationTime;
import roomescape.domain.Role;
import roomescape.domain.Theme;
import roomescape.dto.business.PaymentHistoryCreationContent;
import roomescape.dto.business.ReservationCreationContent;
import roomescape.dto.request.ReservationWithPaymentCreationRequest;
import roomescape.repository.MemberRepository;
import roomescape.repository.ReservationRepository;
import roomescape.repository.ReservationTimeRepository;
import roomescape.repository.ThemeRepository;
import roomescape.service.ReservationService;

@SpringBootTest
class ReservationConcurrencyTest {

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ThemeRepository themeRepository;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationTimeRepository timeRepository;

    @Autowired
    private ReservationService reservationService;

    @Test
    @DisplayName("동시에 예약해도 한개만 저장됨")
    void runConcurrencyProblemTest() throws InterruptedException {
        TransactionDefinition def = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(def);

        ReservationTime time = timeRepository.save(ReservationTime.createWithoutId(LocalTime.of(10, 0)));
        Theme theme = themeRepository.save(Theme.createWithoutId("테마1", "설명", "섬네일"));

        memberRepository.save(Member.createWithoutId(Role.GENERAL, "회원1", "test1@test.com", "qwer1234!"));
        memberRepository.save(Member.createWithoutId(Role.GENERAL, "회원2", "test2@test.com", "qwer1234!"));
        List<Member> members = memberRepository.findAll();
        transactionManager.commit(status);

        int threadCount = 10;
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch finishLatch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        for (int i = 0; i < threadCount; i++) {
            int index = i;
            executor.submit(() -> {
                try {
                    startLatch.await();

                    Member member = members.get(index % members.size());

                    ReservationCreationContent content = new ReservationCreationContent(
                            theme.getId(), NEXT_DAY, time.getId());

                    ReservationWithPaymentCreationRequest paymentRequest =
                            new ReservationWithPaymentCreationRequest(
                                    theme.getId(), NEXT_DAY, time.getId(),
                                    "orderId_" + index + "_" + System.nanoTime(),
                                    "paymentKey_" + index,
                                    "customerKey_" + index,
                                    1000);
                    PaymentHistoryCreationContent paymentContent =
                            new PaymentHistoryCreationContent(paymentRequest);

                    reservationService.addReservation(member.getId(), content, paymentContent);

                    successCount.incrementAndGet();
                } catch (Exception e) {
                    failureCount.incrementAndGet();
                } finally {
                    finishLatch.countDown();
                }
            });
        }

        startLatch.countDown();

        finishLatch.await(5, TimeUnit.SECONDS);
        executor.shutdown();

        assertThat(reservationRepository.count()).isEqualTo(1L);
    }
}
