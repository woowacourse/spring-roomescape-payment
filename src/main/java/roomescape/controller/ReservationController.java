package roomescape.controller;

import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.annotation.Auth;
import roomescape.dto.MyReservationResponse;
import roomescape.dto.ReservationRequest;
import roomescape.dto.ReservationResponse;
import roomescape.service.ReservationService;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

//    @PostMapping
//    public ResponseEntity<PaidReservationResponse> savePaidReservation(
//            @Auth long memberId,
//            @RequestBody PaymentRequest reservationRequest
//    ) {
//        reservationRequest = new PaymentRequest(
//                reservationRequest.date(),
//                memberId,
//                reservationRequest.timeId(),
//                reservationRequest.themeId(),
//                reservationRequest.paymentKey(),
//                reservationRequest.orderId(),
//                reservationRequest.amount()
//        );
//
//        PaidReservationResponse saved = reservationService.savePaid(reservationRequest);
//        return ResponseEntity.created(URI.create("/reservations/" + saved.id()))
//                .body(saved);
//    }

    @PostMapping()
    public ResponseEntity<ReservationResponse> saveReservation(
            @Auth long memberId,
            @RequestBody ReservationRequest reservationRequest
    ) {
        reservationRequest = new ReservationRequest(
                reservationRequest.date(),
                memberId,
                reservationRequest.timeId(),
                reservationRequest.themeId()
        );
        ReservationResponse saved = reservationService.save(reservationRequest);
        return ResponseEntity.created(URI.create("/reservations/" + saved.id()))
                .body(saved);
    }

    @GetMapping
    public List<ReservationResponse> findAllReservations() {
        return reservationService.findAll();
    }

    @GetMapping("/mine")
    public List<MyReservationResponse> findLoginMemberReservations(@Auth long memberId) {
        return reservationService.findByMemberId(memberId);
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Void> delete(@PathVariable long reservationId, @Auth long memberId) {
        reservationService.delete(reservationId, memberId);
        return ResponseEntity.noContent().build();
    }
}
