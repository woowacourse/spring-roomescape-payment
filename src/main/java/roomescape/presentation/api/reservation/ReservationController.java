package roomescape.presentation.api.reservation;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.application.reservation.command.ProcessReservationWithTossPaymentUseCase;
import roomescape.application.reservation.query.ReservationQueryService;
import roomescape.application.reservation.query.dto.ReservationResult;
import roomescape.presentation.api.reservation.request.CreateReservationWithPaymentRequest;
import roomescape.presentation.api.reservation.response.ReservationResponse;
import roomescape.presentation.support.methodresolver.AuthInfo;
import roomescape.presentation.support.methodresolver.AuthPrincipal;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reservations")
public class ReservationController {

    private static final String RESERVATIONS_URL = "/reservations/%d";

    private final ReservationQueryService reservationQueryService;
    private final ProcessReservationWithTossPaymentUseCase processReservationWithTossPaymentUseCase;

    @PostMapping
    public ResponseEntity<Void> createReservation(
            @AuthPrincipal final AuthInfo authInfo,
            @Valid @RequestBody final CreateReservationWithPaymentRequest request) {
        final Long id = processReservationWithTossPaymentUseCase.execute(request.toCreateCommand(authInfo.memberId()));
        return ResponseEntity.created(URI.create(RESERVATIONS_URL.formatted(id)))
                .build();
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> findReservations() {
        final List<ReservationResult> reservationResults = reservationQueryService.findAll();
        final List<ReservationResponse> reservationResponses = reservationResults.stream()
                .map(ReservationResponse::from)
                .toList();
        return ResponseEntity.ok(reservationResponses);
    }
}
