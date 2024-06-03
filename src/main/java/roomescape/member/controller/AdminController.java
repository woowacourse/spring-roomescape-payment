package roomescape.member.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
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
import roomescape.auth.controller.dto.MemberResponse;
import roomescape.auth.domain.AuthInfo;
import roomescape.global.annotation.LoginUser;
import roomescape.member.service.MemberService;
import roomescape.reservation.controller.dto.MemberReservationRequest;
import roomescape.reservation.controller.dto.ReservationResponse;
import roomescape.reservation.service.ReservationApplicationService;
import roomescape.reservation.service.dto.MemberReservationCreate;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final ReservationApplicationService reservationApplicationService;

    private final MemberService memberService;

    public AdminController(ReservationApplicationService reservationApplicationService, MemberService memberService) {
        this.reservationApplicationService = reservationApplicationService;
        this.memberService = memberService;
    }

    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> create(
            @RequestBody @Valid MemberReservationRequest memberReservationRequest) {
        ReservationResponse reservationResponse = reservationApplicationService.createMemberReservation(
                MemberReservationCreate.from(memberReservationRequest)
        );
        return ResponseEntity.created(URI.create("/admin/reservations/" + reservationResponse.memberReservationId()))
                .body(reservationResponse);
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") @Min(1) long reservationId) {
        reservationApplicationService.delete(reservationId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/members")
    public ResponseEntity<List<MemberResponse>> findAll() {
        return ResponseEntity.ok().body(memberService.findAll());
    }

    @PostMapping("/reservations/{id}/waiting/approve")
    public ResponseEntity<Void> approve(@LoginUser AuthInfo authInfo,
                                        @PathVariable("id") @Min(1) long memberReservationId) {
        reservationApplicationService.approveWaiting(authInfo, memberReservationId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reservations/{id}/waiting/deny")
    public ResponseEntity<Void> deny(@LoginUser AuthInfo authInfo,
                                     @PathVariable("id") @Min(1) long memberReservationId) {
        reservationApplicationService.denyWaiting(authInfo, memberReservationId);
        return ResponseEntity.ok().build();
    }
}
