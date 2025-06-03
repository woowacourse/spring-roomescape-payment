package roomescape.infrastructure.payment.toss;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;
import roomescape.dto.request.TossPaymentConfirmDto;
import roomescape.dto.response.TossPaymentConfirmResponseDto;

@HttpExchange
public interface TossPaymentWithRestClient {

    @PostExchange("/confirm")
    TossPaymentConfirmResponseDto requestConfirmation(@RequestBody TossPaymentConfirmDto tossPaymentConfirmDto);

}


