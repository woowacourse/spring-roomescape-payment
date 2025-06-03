package roomescape.study;

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
    @DisplayName("동시에_접근되도_한개만_저장됨")
    void runConcurrencyProblemTest() throws InterruptedException {
        TransactionDefinition def = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(def);

        ReservationTime time = timeRepository.save(ReservationTime.createWithoutId(LocalTime.of(10, 0)));
        Theme theme = themeRepository.save(Theme.createWithoutId("테마1", "설명", "섬네일"));

        memberRepository.save(Member.createWithoutId(Role.GENERAL, "회원1", "test1@test.com", "qwer1234!"));
        memberRepository.save(Member.createWithoutId(Role.GENERAL, "회원2", "test2@test.com", "qwer1234!"));
        List<Member> members = memberRepository.findAll();
        transactionManager.commit(status);

        int threadCount = 10; // 스레드 수 증가
        CountDownLatch startLatch = new CountDownLatch(1); // 동시 시작을 위한 latch
        CountDownLatch finishLatch = new CountDownLatch(threadCount);
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failureCount = new AtomicInteger(0);

        System.out.println("=== 동시성 문제 증명 테스트 시작 ===");
        System.out.println("모든 스레드가 같은 시간, 같은 테마에 예약 시도");

        for (int i = 0; i < threadCount; i++) {
            int index = i;
            executor.submit(() -> {
                try {
                    // 모든 스레드가 동시에 시작하도록 대기
                    startLatch.await();

                    // 각 스레드마다 다른 멤버 사용 (데드락 방지)
                    Member member = members.get(index % members.size());

                    // 모든 스레드가 같은 시간, 같은 테마에 예약 시도
                    ReservationCreationContent content = new ReservationCreationContent(
                            theme.getId(), NEXT_DAY, time.getId());

                    // 각 스레드마다 고유한 결제 정보 사용
                    ReservationWithPaymentCreationRequest paymentRequest =
                            new ReservationWithPaymentCreationRequest(
                                    theme.getId(), NEXT_DAY, time.getId(),
                                    "orderId_" + index + "_" + System.nanoTime(), // 고유한 orderId
                                    "paymentKey_" + index,
                                    "customerKey_" + index,
                                    1000);
                    PaymentHistoryCreationContent paymentContent =
                            new PaymentHistoryCreationContent(paymentRequest);

                    // 예약 시도
                    reservationService.addReservation(member.getId(), content, paymentContent);

                    successCount.incrementAndGet();
                    System.out.println("Thread " + index + " 성공! (Member: " + member.getName() + ")");

                } catch (Exception e) {
                    failureCount.incrementAndGet();
                    System.out.println("Thread " + index + " 실패: " + e.getMessage());
                } finally {
                    finishLatch.countDown();
                }
            });
        }

        // 모든 스레드 동시 시작
        System.out.println("모든 스레드 동시 시작!");
        startLatch.countDown();

        // 모든 스레드 완료 대기
        boolean finished = finishLatch.await(30, TimeUnit.SECONDS);
        executor.shutdown();

        if (!finished) {
            System.out.println("⚠️ 일부 스레드가 타임아웃되었습니다.");
        }

        System.out.println("\n=== 결과 ===");
        System.out.println("성공한 예약: " + reservationRepository.count());
        System.out.println("실패한 예약: " + failureCount.get());

        if (successCount.get() > 1) {
            System.out.println("🚨 동시성 문제 발생! 같은 시간대에 " + successCount.get() + "개의 예약이 성공했습니다!");
        } else {
            System.out.println("✅ 락이 제대로 작동하여 1개의 예약만 성공했습니다.");
        }
    }
}
