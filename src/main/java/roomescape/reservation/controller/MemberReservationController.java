package roomescape.reservation.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.auth.dto.LoginMember;
import roomescape.payment.dto.PaymentRequest;
import roomescape.reservation.dto.MemberReservationCreateRequest;
import roomescape.reservation.dto.MemberReservationResponse;
import roomescape.reservation.dto.MyReservationResponse;
import roomescape.reservation.facade.ReservationFacadeService;

import java.util.List;

@Tag(name = "멤버 예약", description = "멤버 권한으로 예약 조회, 추가, 삭제")
@RestController
@RequestMapping("/reservations")
public class MemberReservationController {

    private final ReservationFacadeService reservationFacadeService;

    public MemberReservationController(ReservationFacadeService reservationFacadeService) {
        this.reservationFacadeService = reservationFacadeService;
    }

    @PostMapping
    public MemberReservationResponse createReservation(
            @Valid @RequestBody MemberReservationCreateRequest request,
            LoginMember member
    ) {
        return reservationFacadeService.createReservation(request, member);
    }

    @PostMapping("/{id}/payments/confirm")
    public ResponseEntity<Void> confirmPendingReservation(
            @PathVariable Long id,
            @RequestBody PaymentRequest paymentRequest
    ) {
        reservationFacadeService.confirmPendingReservation(id, paymentRequest);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/my")
    public List<MyReservationResponse> readMemberReservations(LoginMember loginMember) {
        return reservationFacadeService.readMemberReservations(loginMember);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMemberReservation(@PathVariable Long id, LoginMember loginMember) {
        reservationFacadeService.deleteReservation(id, loginMember);
        return ResponseEntity.noContent().build();
    }
}
