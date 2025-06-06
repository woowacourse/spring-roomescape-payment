package roomescape.payment;

import java.util.concurrent.CancellationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import roomescape.common.exception.PaymentException;
import roomescape.payment.dto.PaymentConfirmRequest;
import roomescape.payment.dto.PaymentConfirmResponse;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentClient paymentClient;

    public PaymentConfirmResponse confirm(PaymentConfirmRequest request) {
        try {
            // TODO 요청과 응답의 값 변조 검증
            return paymentClient.confirm(request);
        } catch (CancellationException | ResourceAccessException e) {
            throw new PaymentException(HttpStatus.INTERNAL_SERVER_ERROR, "결제 서버와의 연결 지연으로 요청에 실패했습니다.", e);
        }
    }
}
