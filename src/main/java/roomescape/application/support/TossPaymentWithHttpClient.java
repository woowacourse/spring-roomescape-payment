package roomescape.application.support;

import roomescape.dto.request.TossPaymentConfirmDto;
import roomescape.dto.response.TossPaymentConfirmResponseDto;

public interface TossPaymentWithHttpClient {

    TossPaymentConfirmResponseDto requestConfirmation(TossPaymentConfirmDto tossPaymentConfirmDto);

}
