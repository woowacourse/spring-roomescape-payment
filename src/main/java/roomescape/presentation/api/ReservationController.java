package roomescape.presentation.api;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.application.ReservationPayService;
import roomescape.application.ReservationService;
import roomescape.application.WaitingService;
import roomescape.presentation.AuthenticationPrincipal;
import roomescape.presentation.dto.request.LoginMember;
import roomescape.presentation.dto.request.ReservationCreateRequest;
import roomescape.presentation.dto.request.ReservationWithPaymentRequest;
import roomescape.presentation.dto.response.InvoiceResponse;
import roomescape.presentation.dto.response.WaitingResponse;

import java.util.List;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final ReservationPayService reservationPayService;
    private final WaitingService waitingService;

    public ReservationController(
            ReservationService reservationService,
            ReservationPayService reservationPayService,
            WaitingService waitingService
    ) {
        this.reservationService = reservationService;
        this.reservationPayService = reservationPayService;
        this.waitingService = waitingService;
    }

    @PostMapping
    public ResponseEntity<InvoiceResponse> createReservation(
            @RequestBody @Valid ReservationWithPaymentRequest request,
            @AuthenticationPrincipal LoginMember loginMember
    ) {
        InvoiceResponse response = reservationPayService.createReservationWithPayment(request, loginMember);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        reservationService.cancelReservationById(id);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/waitings")
    public ResponseEntity<WaitingResponse> createWaiting(
            @RequestBody @Valid ReservationCreateRequest request,
            @AuthenticationPrincipal LoginMember loginMember
    ) {
        WaitingResponse response = waitingService.createWaiting(request, loginMember);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @DeleteMapping("/waitings/{id}")
    public ResponseEntity<Void> deleteWaiting(@PathVariable Long id, @AuthenticationPrincipal LoginMember loginMember) {
        waitingService.deleteWaitingByIdAndMember(id, loginMember);

        return ResponseEntity.noContent().build();
    }
}
