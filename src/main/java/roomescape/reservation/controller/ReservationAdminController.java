package roomescape.reservation.controller;

import java.net.URI;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.common.utils.UriFactory;
import roomescape.member.auth.RoleRequired;
import roomescape.member.domain.Role;
import roomescape.reservation.controller.dto.CreateReservationByAdminWebRequest;
import roomescape.reservation.controller.dto.ReservationSearchWebRequest;
import roomescape.reservation.controller.dto.ReservationWaitWebResponse;
import roomescape.reservation.controller.dto.ReservationWebResponse;
import roomescape.reservation.service.ReservationService;

@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/reservations")
public class ReservationAdminController {

    private final ReservationService reservationService;

    @RoleRequired(value = Role.ADMIN)
    @GetMapping("/wait")
    public List<ReservationWaitWebResponse> getAllReservationWait() {
        return reservationService.getAllReservationWait();
    }

    @RoleRequired(value = Role.ADMIN)
    @GetMapping
    public ResponseEntity<List<ReservationWebResponse>> getReservationsByAdmin(
            @ModelAttribute final ReservationSearchWebRequest reservationSearchWebRequest
    ) {
        return ResponseEntity.ok(reservationService.search(reservationSearchWebRequest));
    }

    @RoleRequired(value = Role.ADMIN)
    @PostMapping
    public ResponseEntity<ReservationWebResponse> createReservationByAdmin(
            @RequestBody final CreateReservationByAdminWebRequest createReservationByAdminWebRequest
    ) {
        final ReservationWebResponse reservationWebResponse = reservationService.create(
                createReservationByAdminWebRequest
        );
        final URI location = UriFactory.buildPath("/reservations", String.valueOf(reservationWebResponse.id()));

        return ResponseEntity.created(location)
                .body(reservationWebResponse);
    }
}
