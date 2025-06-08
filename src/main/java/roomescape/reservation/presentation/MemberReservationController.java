package roomescape.reservation.presentation;

import jakarta.validation.Valid;
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
import roomescape.auth.annotation.LoginMember;
import roomescape.auth.dto.info.LoginMemberInfo;
import roomescape.reservation.application.ReservationService;
import roomescape.reservation.dto.request.ReservationRequest;
import roomescape.reservation.dto.request.ReservationWithPaymentRequest;
import roomescape.reservation.dto.response.ReservationMineResponse;
import roomescape.reservation.dto.response.ReservationResponse;
import roomescape.reservation.dto.response.WaitingResponse;

@RestController
@RequestMapping("/reservations")
public class MemberReservationController {

    private final ReservationService reservationService;

    public MemberReservationController(final ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> createReservation(
            @Valid @RequestBody final ReservationWithPaymentRequest request,
            @LoginMember final LoginMemberInfo memberInfo) {
        ReservationResponse response = reservationService.createReservationWithPayment(request, memberInfo.id());
        return ResponseEntity.created(URI.create("/reservation")).body(response);
    }

    @PostMapping("/waitings")
    public ResponseEntity<WaitingResponse> createWaiting(
            @RequestBody final ReservationRequest request,
            @LoginMember final LoginMemberInfo memberInfo
    ) {
        WaitingResponse response = reservationService.createWaiting(request, memberInfo.id());
        return ResponseEntity.created(URI.create("/reservations/waiting")).body(response);
    }

    @DeleteMapping("/waitings/{id}")
    public ResponseEntity<Void> deleteWaiting(@PathVariable("id") final Long id) {
        reservationService.deleteWaiting(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/me")
    public ResponseEntity<List<ReservationMineResponse>> getMyReservations(
            @LoginMember final LoginMemberInfo loginMemberInfo) {
        List<ReservationMineResponse> response = reservationService.getMemberReservations(loginMemberInfo);
        return ResponseEntity.ok().body(response);
    }
}
