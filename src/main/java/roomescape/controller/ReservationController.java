package roomescape.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
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
import roomescape.controller.document.DocumentedReservationController;
import roomescape.dto.LoginMemberReservationResponse;
import roomescape.dto.ReservationRequest;
import roomescape.dto.ReservationResponse;
import roomescape.service.MyReservationService;
import roomescape.service.ReservationService;

@RestController
@RequestMapping("/reservations")
@Tag(name = "예약", description = "회원용 예약 API")
public class ReservationController implements DocumentedReservationController {
    private final ReservationService reservationService;
    private final MyReservationService myReservationService;

    public ReservationController(ReservationService reservationService, MyReservationService myReservationService) {
        this.reservationService = reservationService;
        this.myReservationService = myReservationService;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> saveReservation(@Auth long memberId,
                                                               @RequestBody ReservationRequest reservationRequest) {
        reservationRequest = new ReservationRequest(reservationRequest.date(), memberId, reservationRequest.timeId(),
                reservationRequest.themeId());
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
        return myReservationService.findLoginMemberReservations(memberId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@Auth long memberId, @PathVariable("id") long reservationId) {
        reservationService.cancel(memberId, reservationId);
        return ResponseEntity.noContent().build();
    }
}
