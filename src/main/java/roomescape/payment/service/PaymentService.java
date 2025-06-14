package roomescape.payment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import roomescape.global.error.exception.BadPaymentRequestException;
import roomescape.payment.client.PaymentClient;
import roomescape.payment.dto.response.PaymentConfirmResponse;
import roomescape.payment.dto.response.PaymentRequestInfoResponse;
import roomescape.payment.entity.Payment;
import roomescape.payment.entity.PaymentRequestInfo;
import roomescape.payment.repository.PaymentRepository;
import roomescape.payment.repository.PaymentRequestInfoRepository;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentClient paymentClient;
    private final PaymentRepository paymentRepository;
    private final PaymentRequestInfoRepository paymentRequestInfoRepository;

    public Payment confirmPayment(String paymentKey, String orderId, Long clientRequestAmount) {
        validateAmountNotTampered(orderId, clientRequestAmount);

        PaymentConfirmResponse response = paymentClient.requestPaymentConfirm(
                paymentKey,
                orderId,
                clientRequestAmount
        );

        Payment payment = new Payment(
                response.paymentKey(),
                response.orderId(),
                response.totalAmount(),
                response.type()
        );
        return paymentRepository.save(payment);
    }

    private void validateAmountNotTampered(String orderId, Long clientRequestAmount) {
        PaymentRequestInfo paymentRequestInfo = findPaymentRequestInfoByOrderIdOrThrow(orderId);

        if (!paymentRequestInfo.getAmount().equals(clientRequestAmount)) {
            throw new BadPaymentRequestException("서버 저장된 결제 요청 금액과 클라이언트 결제 금액이 일치하지 않습니다.");
        }
    }

    private PaymentRequestInfo findPaymentRequestInfoByOrderIdOrThrow(String orderId) {
        return paymentRequestInfoRepository.findByOrderId(orderId)
                .orElseThrow(() -> new IllegalArgumentException("해당 주문 ID에 대한 결제 요청 정보가 없습니다."));
    }

    public PaymentRequestInfoResponse createPaymentRequestInfo(String orderName, Long amount) {
        String orderId = generateOrderId();
        PaymentRequestInfo paymentRequestInfo = new PaymentRequestInfo(orderId, orderName, amount);
        paymentRequestInfoRepository.save(paymentRequestInfo);

        return new PaymentRequestInfoResponse(
                paymentRequestInfo.getOrderId(),
                paymentRequestInfo.getOrderName(),
                paymentRequestInfo.getAmount()
        );
    }

    private String generateOrderId() {
        return "ROOM_" + generateUUID();
    }

    private String generateUUID() {
        return java.util.UUID.randomUUID().toString();
    }
}
