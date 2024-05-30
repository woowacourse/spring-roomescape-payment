package roomescape.infrastructure.payment;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import roomescape.service.PaymentClient;
import roomescape.service.exception.PaymentApproveClientErrorException;
import roomescape.service.exception.PaymentApproveInternalServerErrorException;
import roomescape.service.request.PaymentApproveDto;
import roomescape.service.response.PaymentApproveSuccessDto;

@Component
public class TossPaymentClient implements PaymentClient {

    private static final String CONFIRM_URL = "/confirm";

    private final PaymentAuthorizationGenerator paymentAuthorizationGenerator;
    private final RestClient restClient;

    public TossPaymentClient(PaymentAuthorizationGenerator paymentAuthorizationGenerator,
                             RestClient.Builder restClient,
                             @Value("${payment.base-url}") String baseUrl) {
        this.paymentAuthorizationGenerator = paymentAuthorizationGenerator;
        this.restClient = restClient.baseUrl(baseUrl).build();
    }

    public PaymentApproveSuccessDto approve(PaymentApproveDto paymentApproveDto) {
        String authorizations = paymentAuthorizationGenerator.createAuthorizations();

        return restClient.post()
                .uri(CONFIRM_URL)
                .header(HttpHeaders.AUTHORIZATION, authorizations)
                .contentType(MediaType.APPLICATION_JSON)
                .body(paymentApproveDto)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new PaymentApproveClientErrorException();
                })
                .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                    throw new PaymentApproveInternalServerErrorException();
                })
                .body(PaymentApproveSuccessDto.class);
    }
}
