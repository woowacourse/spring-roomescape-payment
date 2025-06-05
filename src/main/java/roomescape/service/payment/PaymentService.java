package roomescape.service.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import roomescape.domain.payment.Payment;
import roomescape.domain.payment.PaymentRepository;
import roomescape.domain.reservation.Reservation;
import roomescape.domain.reservation.ReservationRepository;

import java.util.NoSuchElementException;
import java.util.UUID;

import static roomescape.dto.response.TossPaymentResponse.PaymentStatus;

@Slf4j
@RequiredArgsConstructor
@Service
public class PaymentService {

    private final PaymentApproveClient paymentApproveClient;
    private final PaymentCheckClient paymentCheckClient;
    private final PaymentCancelClient paymentCancelClient;
    private final PaymentRepository paymentRepository;
    private final ReservationRepository reservationRepository;

    @Transactional
    public void approveAndSave(String paymentKey, String orderId, int amount, Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NoSuchElementException("[ERROR] 존재하지 않는 예약입니다."));
        paymentRepository.save(new Payment(paymentKey, amount, reservation));
        approvePaymentSafely(paymentKey, orderId, amount);
    }

    private void approvePaymentSafely(String paymentKey, String orderId, int amount) {
        try {
            log.info("토스 결제 승인 API 호출 - paymentKey : {}, orderId : {}, amount : {}", paymentKey, orderId, amount);
            paymentApproveClient.approve(paymentKey, orderId, amount);
        } catch (RestClientException e) {
            log.error("토스 결제 승인 API 실패", e);
            // 결제 승인 요청 중 실패 -> 결제 승인 여부를 모르므로 결제 확인 요청
            int attempts = 0;
            while (attempts < 3) {
                try {
                    attempts++;
                    log.warn("토스 결제 확인 API 호출, {}번째 시도", attempts);
                    if (paymentCheckClient.checkStatus(paymentKey) == PaymentStatus.DONE) {
                        log.info("토스 결제 승인 정상처리 확인");
                        return;
                    }
                } catch (HttpServerErrorException | ResourceAccessException ignored) {
                }
            }

            log.warn("토스 결제 확인 API 실패");
            // 결제 확인 요청도 실패 -> 안전하게 결제 취소 (멱등성 보장)
            // 토스 API 멱등성 관련 문서 : https://docs.tosspayments.com/reference#%EA%B2%B0%EC%A0%9C-%EC%B7%A8%EC%86%8C
            String idempotencyKey = UUID.randomUUID().toString();
            boolean cancelSuccess = false;
            attempts = 0;
            while (attempts < 3) {
                try {
                    attempts++;
                    log.warn("토스 결제 취소 API 호출, {}번째 시도", attempts);
                    paymentCancelClient.cancelIdempotently(paymentKey, idempotencyKey);
                    cancelSuccess = true;
                } catch (HttpServerErrorException | ResourceAccessException ignored) {
                }
            }
            if (cancelSuccess) {
                log.info("토스 결제 취소 성공");
                throw new IllegalStateException("[ERROR] 결제 중 문제가 발생하여 결제를 취소했습니다.");
            } else {
                log.warn("토스 결제 취소 실패, 취소 대기열에 추가 - paymentKey : {}, idempotencyKey : {}", paymentKey, idempotencyKey);
                // 결제 취소 시도도 실패했다면, 일단 저장해놓고 추후에 취소 (5분 주기)
                paymentCancelClient.addToCancelSchedule(paymentKey, idempotencyKey);
                throw new IllegalStateException("[ERROR] 결제 중 문제가 발생했습니다. 시간이 지나도 결제가 취소되지 않는다면 관리자에게 문의하세요.");
            }
        } catch (Exception e) {
            log.error("토스 결제 승인 시도 중 예상치 못한 예외 발생", e);
            // 예상하지 못한 예외 -> 관리자 문의
            // TODO : 개발자 수준에서 더 처리할 수 있는 것이 있다면 추가하기
            throw new IllegalStateException("[ERROR] 결제 중 예기치 못한 문제가 발생하였습니다. 관리자에게 문의하세요.");
        }
    }
}
