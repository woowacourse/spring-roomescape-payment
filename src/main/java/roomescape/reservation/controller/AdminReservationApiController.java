package roomescape.reservation.controller;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.dto.LoginMember;
import roomescape.common.dto.MultipleResponses;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.dto.ReservationSaveRequest;
import roomescape.reservation.dto.ReservationSearchConditionRequest;
import roomescape.reservation.dto.ReservationWaitingResponse;
import roomescape.reservation.service.ReservationService;

@RestController
@RequestMapping("/admin")
public class AdminReservationApiController {

    private final ReservationService reservationService;

    public AdminReservationApiController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @GetMapping("/reservations/search")
    public ResponseEntity<MultipleResponses<ReservationResponse>> findAllBySearchCond(
            @Valid @ModelAttribute ReservationSearchConditionRequest reservationSearchConditionRequest
    ) {
        List<ReservationResponse> reservationResponses = reservationService.findAllBySearchCondition(
                reservationSearchConditionRequest);

        return ResponseEntity.ok(new MultipleResponses<>(reservationResponses));
    }

    @GetMapping("/reservations/waiting")
    public ResponseEntity<MultipleResponses<ReservationWaitingResponse>> findWaitingReservations() {
        List<ReservationWaitingResponse> waitingReservations = reservationService.findWaitingReservations();

        return ResponseEntity.ok(new MultipleResponses<>(waitingReservations));
    }

    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> saveReservation(
            @Valid @RequestBody ReservationSaveRequest reservationSaveRequest,
            LoginMember loginMember
    ) {
        ReservationResponse reservationResponse = reservationService.saveReservationSuccess(reservationSaveRequest, loginMember);

        return ResponseEntity.created(URI.create("/reservations/" + reservationResponse.id()))
                .body(reservationResponse);
    }
}
