package roomescape.controller;

import io.swagger.v3.oas.annotations.Operation;
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
import roomescape.dto.LoginMemberReservationResponse;
import roomescape.dto.ReservationRequest;
import roomescape.dto.ReservationResponse;
import roomescape.service.MyReservationService;
import roomescape.service.ReservationService;

@RestController
@RequestMapping("/reservations")
@Tag(name = "예약", description = "회원용 예약 API")
public class ReservationController {
    private final ReservationService reservationService;
    private final MyReservationService myReservationService;

    public ReservationController(ReservationService reservationService, MyReservationService myReservationService) {
        this.reservationService = reservationService;
        this.myReservationService = myReservationService;
    }

    @PostMapping
    @Operation(summary = "예약 생성", description = "회원이 자신의 예약을 생성할 때 사용하는 API")
    public ResponseEntity<ReservationResponse> saveReservation(@Auth long memberId,
                                                               @RequestBody ReservationRequest reservationRequest) {
        reservationRequest = new ReservationRequest(reservationRequest.date(), memberId, reservationRequest.timeId(),
                reservationRequest.themeId());
        ReservationResponse saved = reservationService.save(reservationRequest);
        return ResponseEntity.created(URI.create("/reservations/" + saved.id()))
                .body(saved);
    }

    @GetMapping
    @Operation(summary = "전체 예약 조회", description = "전체 예약을 조회할 때 사용하는 API")
    public List<ReservationResponse> findAllReservations() {
        return reservationService.findAll();
    }

    @GetMapping("/mine")
    @Operation(summary = "회원 예약 목록 조회", description = "회원이 자신의 예약 목록을 조회할 때 사용하는 API")
    public List<LoginMemberReservationResponse> findLoginMemberReservations(@Auth long memberId) {
        return myReservationService.findLoginMemberReservations(memberId);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "예약 취소", description = "예약을 취련할 때 사용하는 API")
    public ResponseEntity<Void> delete(@Auth long memberId, @PathVariable("id") long reservationId) {
        reservationService.cancel(memberId, reservationId);
        return ResponseEntity.noContent().build();
    }
}
