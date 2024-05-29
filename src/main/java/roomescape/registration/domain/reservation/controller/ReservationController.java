package roomescape.registration.domain.reservation.controller;

import java.time.LocalDate;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.annotation.LoginMemberId;
import roomescape.client.payment.dto.PaymentConfirmToTossDto;
import roomescape.client.payment.PaymentClient;
import roomescape.registration.domain.reservation.dto.ReservationRequest;
import roomescape.registration.domain.reservation.dto.ReservationResponse;
import roomescape.registration.domain.reservation.dto.ReservationTimeAvailabilityResponse;
import roomescape.registration.domain.reservation.service.ReservationService;
import roomescape.registration.dto.RegistrationDto;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final PaymentClient paymentClient;

    public ReservationController(ReservationService reservationService, PaymentClient paymentClient) {
        this.reservationService = reservationService;
        this.paymentClient = paymentClient;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> reservationSave(@RequestBody ReservationRequest reservationRequest,
                                                               @LoginMemberId long id) {
        PaymentConfirmToTossDto paymentConfirmToTossDto = PaymentConfirmToTossDto.from(reservationRequest);
        paymentClient.sendPaymentConfirmToToss(paymentConfirmToTossDto);

        RegistrationDto registrationDto = RegistrationDto.of(reservationRequest, id);
        ReservationResponse reservationResponse = reservationService.addReservation(registrationDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(reservationResponse);
    }

    @GetMapping
    public List<ReservationResponse> reservaionList() {
        return reservationService.findReservations();
    }

    @GetMapping("/{themeId}")
    public List<ReservationTimeAvailabilityResponse> reservationTimeList(@PathVariable long themeId,
                                                                         @RequestParam LocalDate date) {
        return reservationService.findTimeAvailability(themeId, date);
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Void> reservationRemove(@PathVariable long reservationId) {
        reservationService.removeReservation(reservationId);
        return ResponseEntity.noContent().build();
    }
}
