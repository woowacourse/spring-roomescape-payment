package roomescape.application.payment;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import roomescape.infrastructure.error.exception.PaymentException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class OrderAmountVerificationCache {

    private final Map<String, Long> cache = new ConcurrentHashMap<>();

    public void register(final String key, final long amount) {
        cache.put(key, amount);
    }

    public void check(final String key, final long amount) {
        final Long savedAmount = get(key);
        delete(key);

        if (savedAmount.equals(amount)) {
            return;
        }

        log.error("결제 금액 불일치 - orderId: {}, savedAmount: {}, requestedAmount: {}", key, savedAmount, amount);
        throw new PaymentException(
                "요청 금액과 승인 금액이 일치하지 않습니다. 현재 결제 금액: %d, 요청 금액: %d".formatted(savedAmount, amount)
        );
    }

    private Long get(final String key) {
        if (cache.containsKey(key)) {
            return cache.get(key);
        }

        log.error("존재하지 않는 결제 정보 조회 시도 - orderId: {}", key);
        throw new PaymentException("존재하지 않는 결제 정보입니다");
    }

    private void delete(final String key) {
        cache.remove(key);
    }
}
