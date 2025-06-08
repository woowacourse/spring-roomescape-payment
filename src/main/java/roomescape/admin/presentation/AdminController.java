package roomescape.admin.presentation;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.admin.adaptor.AdminApiAdaptor;
import roomescape.admin.docs.AdminReservationRequestDocs;
import roomescape.admin.dto.AdminReservationRequest;
import roomescape.reservation.adaptor.ReservationApiAdaptor;
import roomescape.reservation.docs.ReservationResponseDocs;
import roomescape.reservation.dto.response.ReservationResponse;
import roomescape.reservation.service.ReservationService;

import java.net.URI;

import static roomescape.admin.presentation.AdminController.ADMIN_BASE_URL;
import static roomescape.member.presentation.MemberController.RESERVATION_BASE_URL;

@RestController
@RequestMapping(ADMIN_BASE_URL)
public class AdminController {

    public static final String ADMIN_BASE_URL = "/admin";
    private static final String SLASH = "/";

    private final ReservationService reservationService;
    private final AdminApiAdaptor adminApiAdaptor;
    private final ReservationApiAdaptor reservationApiAdaptor;

    public AdminController(final ReservationService reservationService, final AdminApiAdaptor adminApiAdaptor, final ReservationApiAdaptor reservationApiAdaptor) {
        this.reservationService = reservationService;
        this.adminApiAdaptor = adminApiAdaptor;
        this.reservationApiAdaptor = reservationApiAdaptor;
    }

    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponseDocs> createReservation(@RequestBody final AdminReservationRequestDocs requestDocs) {
        AdminReservationRequest request = requestDocs.toReservationRequest();

        ReservationResponse response = reservationService.createPendingReservation(request.getReservationRequest(),
                request.memberId());
        URI locationUri = URI.create(RESERVATION_BASE_URL + SLASH + response.id());

        ReservationResponseDocs reservationResponseDocs = reservationApiAdaptor.toReservationResponseDocs(response);
        return ResponseEntity.created(locationUri).body(reservationResponseDocs);
    }
}
