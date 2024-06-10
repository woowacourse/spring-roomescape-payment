package roomescape.registration.domain.reservation.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
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
import roomescape.client.payment.dto.TossPaymentConfirmRequest;
import roomescape.payment.PaymentService;
import roomescape.registration.domain.reservation.domain.Reservation;
import roomescape.registration.domain.reservation.dto.ReservationRequest;
import roomescape.registration.domain.reservation.dto.ReservationResponse;
import roomescape.registration.domain.reservation.dto.ReservationTimeAvailabilityResponse;
import roomescape.registration.domain.reservation.service.ReservationService;
import roomescape.registration.dto.RegistrationDto;

@Tag(name = "예약 컨트롤러", description = "예약을 저장 또는 삭제하고, 모든 예약 정보를 반환하고, 특정 시간대에 존재하는 예약을 반환한다.")
@RequestMapping("/reservations")
@RestController
public class ReservationController {

    private final ReservationService reservationService;
    private final PaymentService paymentService;

    public ReservationController(ReservationService reservationService, PaymentService paymentService) {
        this.reservationService = reservationService;
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> reservationSave(@RequestBody ReservationRequest reservationRequest,
                                                               @LoginMemberId long memberId) {
        TossPaymentConfirmRequest tossPaymentConfirmRequest = TossPaymentConfirmRequest.from(reservationRequest);

        RegistrationDto registrationDto = new RegistrationDto(
                reservationRequest.date(),
                reservationRequest.themeId(),
                reservationRequest.timeId(),
                memberId
        );

        Reservation reservation = reservationService.addReservation(registrationDto);
        paymentService.confirmPayment(tossPaymentConfirmRequest, reservation);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ReservationResponse.from(reservation));
    }

    @GetMapping
    public List<ReservationResponse> reservaionList() {
        return reservationService.findReservations().stream()
                .map(ReservationResponse::from)
                .toList();
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
