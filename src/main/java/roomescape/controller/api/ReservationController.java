package roomescape.controller.api;

import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.controller.dto.request.ReservationByAdminRequest;
import roomescape.controller.dto.request.ReservationRequest;
import roomescape.controller.dto.response.ApiResponses;
import roomescape.controller.support.Auth;
import roomescape.security.authentication.Authentication;
import roomescape.service.ReservationService;
import roomescape.service.dto.response.PersonalReservationResponse;
import roomescape.service.dto.response.ReservationResponse;

@RestController
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/admin/reservations")
    public ApiResponses<ReservationResponse> getReservationsByConditions(@RequestParam(required = false) Long memberId,
                                                                         @RequestParam(required = false) Long themeId,
                                                                         @RequestParam(required = false) LocalDate dateFrom,
                                                                         @RequestParam(required = false) LocalDate dateTo) {
        List<ReservationResponse> reservationResponses = reservationService
                .getReservationsByConditions(memberId, themeId, dateFrom, dateTo);
        return new ApiResponses<>(reservationResponses);
    }

    @GetMapping("/reservations/mine")
    public ApiResponses<PersonalReservationResponse> getMyReservations(@Auth Authentication authentication) {
        List<PersonalReservationResponse> reservationResponses =
                reservationService.getReservationsByMemberId(authentication.getId());
        return new ApiResponses<>(reservationResponses);
    }

    @PostMapping("/admin/reservations")
    public ResponseEntity<ReservationResponse> addReservationByAdmin(
            @RequestBody @Valid ReservationByAdminRequest request) {
        ReservationResponse response = reservationService.addPendingReservation(request.toCreateReservationRequest());
        return ResponseEntity.created(URI.create("/reservations/" + response.id()))
                .body(response);
    }

    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> addReservation(@RequestBody @Valid ReservationRequest request,
                                                              @Auth Authentication authentication) {
        long memberId = authentication.getId();
        ReservationResponse response = reservationService.addConfirmedReservation(
                request.toCreateReservationRequest(memberId), request.toPaymentRequest()
        );
        return ResponseEntity.created(URI.create("/reservations/" + response.id()))
                .body(response);
    }

    @DeleteMapping("/admin/reservations/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteReservationById(@PathVariable Long id) {
        reservationService.deleteReservationById(id);
    }
}
