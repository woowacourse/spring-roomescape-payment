package roomescape.service;

import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.PostExchange;
import roomescape.dto.payment.PaymentConfirmRequest;
import roomescape.dto.payment.PaymentConfirmResponse;

@Service
public interface PaymentClientService {
    @PostExchange("/payments/confirm")
    PaymentConfirmResponse confirm(@RequestHeader("Authorization") String authorization,
                                   @RequestBody PaymentConfirmRequest request);
}
