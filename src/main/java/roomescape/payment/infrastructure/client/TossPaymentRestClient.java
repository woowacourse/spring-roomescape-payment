package roomescape.payment.infrastructure.client;

import static org.springframework.http.MediaType.APPLICATION_JSON;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import roomescape.payment.model.TossPaymentApprovalInfo;
import roomescape.payment.model.TossPaymentGateway;

@Slf4j
@RequiredArgsConstructor
public class TossPaymentRestClient implements TossPaymentGateway {

    private final RestClient restClient;
    private final TossPaymentClientErrorHandler tossPaymentClientErrorHandler;

    @Override
    public void requestApprove(final TossPaymentApprovalInfo tossPaymentApprovalInfo) {
        try {
            long startTime = System.currentTimeMillis();
            log.debug("Toss 결제 승인 요청 시작 - paymentKey: {}", tossPaymentApprovalInfo.paymentKey());

            restClient.post()
                    .uri("/v1/payments/confirm")
                    .contentType(APPLICATION_JSON)
                    .body(tossPaymentApprovalInfo)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, tossPaymentClientErrorHandler::handleResponseError)
                    .toBodilessEntity();

            long duration = System.currentTimeMillis() - startTime;
            log.debug("Toss 결제 승인 완료 - paymentKey: {}, duration: {}ms",
                    tossPaymentApprovalInfo.paymentKey(), duration);

        } catch (ResourceAccessException e) {
            log.error("Toss 결제 타임아웃 - paymentKey: {}, paymentAction: {}, endPoint: {}",
                    tossPaymentApprovalInfo.paymentKey(), "결제 승인", "/v1/payments/confirm");
            tossPaymentClientErrorHandler.handleTimeoutError(e);
        }
    }
}
