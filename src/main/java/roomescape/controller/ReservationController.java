package roomescape.controller;

import java.net.URI;
import java.util.ArrayList;
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
import roomescape.dto.LoginMemberReservationResponse;
import roomescape.dto.ReservationRequest;
import roomescape.dto.ReservationResponse;
import roomescape.service.PaymentService;
import roomescape.service.ReservationService;
import roomescape.service.ReservationWaitingService;

@RestController
@RequestMapping("/reservations")
public class ReservationController {
    private final ReservationService reservationService;
    private final ReservationWaitingService waitingService;
    private final PaymentService paymentService;

    public ReservationController(ReservationService reservationService, ReservationWaitingService waitingService,
                                 PaymentService paymentService) {
        this.reservationService = reservationService;
        this.waitingService = waitingService;
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> saveReservation(@Auth long memberId,
                                                               @RequestBody ReservationRequest reservationRequest) {
        reservationRequest = new ReservationRequest(reservationRequest.date(), memberId, reservationRequest.timeId(),
                reservationRequest.themeId(), reservationRequest.approveRequest());
        // 결제 성공하면 이후 진행
        paymentService.approve(reservationRequest.approveRequest(), memberId);
        ReservationResponse saved = reservationService.save(reservationRequest);
        return ResponseEntity.created(URI.create("/reservations/" + saved.id()))
                .body(saved);
    }

    @GetMapping
    public List<ReservationResponse> findAllReservations() {
        return reservationService.findAll();
    }

    @GetMapping("/mine")
    public List<LoginMemberReservationResponse> findLoginMemberReservations(@Auth long memberId) {
        List<LoginMemberReservationResponse> reservations = reservationService.findByMemberId(memberId);
        List<LoginMemberReservationResponse> waitings = waitingService.findByMemberId(memberId);

        List<LoginMemberReservationResponse> response = new ArrayList<>(reservations);
        response.addAll(waitings);
        return response;
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Auth long memberId, @PathVariable("id") long reservationId) {
        reservationService.cancel(memberId, reservationId);
        return ResponseEntity.noContent().build();
    }
}
