package roomescape.service;

import roomescape.service.request.PaymentApproveAppRequest;
import roomescape.service.response.PaymentApproveSuccessAppResponse;

public interface PaymentClient {

    PaymentApproveSuccessAppResponse approve(PaymentApproveAppRequest request);
}
