package roomescape.admin.adaptor;

import org.springframework.stereotype.Component;
import roomescape.admin.docs.AdminReservationRequestDocs;
import roomescape.admin.dto.AdminReservationRequest;

@Component
public class AdminApiAdaptor {

    public AdminReservationRequest toReservationRequest(AdminReservationRequestDocs dto) {
        return dto.toReservationRequest();
    }
}
