package roomescape.admin.presentation;

import static roomescape.admin.presentation.AdminController.ADMIN_BASE_URL;
import static roomescape.member.presentation.MemberController.RESERVATION_BASE_URL;

import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.admin.dto.AdminReservationRequest;
import roomescape.admin.service.AdminReservationService;
import roomescape.reservation.dto.response.ReservationResponse;

@RestController
@RequestMapping(ADMIN_BASE_URL)
public class AdminController {

    public static final String ADMIN_BASE_URL = "/admin";
    private static final String SLASH = "/";

    private final AdminReservationService adminReservationService;

    public AdminController(AdminReservationService adminReservationService) {
        this.adminReservationService = adminReservationService;
    }

    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> createReservation(@RequestBody final AdminReservationRequest request) {
        ReservationResponse response = adminReservationService.createReservation(request.getReservationRequest(), request.memberId());
        URI locationUri = URI.create(RESERVATION_BASE_URL + SLASH + response.id());
        return ResponseEntity.created(locationUri).body(response);
    }
}
