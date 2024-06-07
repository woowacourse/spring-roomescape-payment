package roomescape.infrastructure;

import roomescape.domain.PaymentInfo;
import roomescape.dto.request.MemberReservationRequest;

public interface PaymentClient {

    public PaymentInfo payment(MemberReservationRequest memberReservationRequest);
}
