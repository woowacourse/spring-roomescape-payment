package roomescape.service.conponent;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public interface PaymentWithRestClient {

    RestClient getRestClient();

    String getAuthorizations();

    String getPaymentServerURL();
}
