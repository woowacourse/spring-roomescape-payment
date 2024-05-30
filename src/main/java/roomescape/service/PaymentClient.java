package roomescape.service;

import roomescape.service.request.PaymentApproveDto;
import roomescape.service.response.PaymentApproveSuccessDto;

public interface PaymentClient {

    PaymentApproveSuccessDto approve(PaymentApproveDto request);
}
