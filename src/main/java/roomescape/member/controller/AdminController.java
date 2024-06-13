package roomescape.member.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import roomescape.auth.controller.dto.MemberResponse;
import roomescape.auth.domain.AuthInfo;
import roomescape.global.annotation.LoginUser;
import roomescape.member.domain.specification.AdminControllerSpecification;
import roomescape.member.service.MemberService;
import roomescape.reservation.controller.dto.MemberReservationRequest;
import roomescape.reservation.controller.dto.ReservationResponse;
import roomescape.reservation.service.ReservationApplicationService;
import roomescape.reservation.service.dto.MemberReservationCreate;

@RestController
@RequestMapping("/admin")
public class AdminController implements AdminControllerSpecification {

    private final ReservationApplicationService reservationApplicationService;

    private final MemberService memberService;

    public AdminController(final ReservationApplicationService reservationApplicationService, final MemberService memberService) {
        this.reservationApplicationService = reservationApplicationService;
        this.memberService = memberService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/reservations")
    public ReservationResponse create(@RequestBody final MemberReservationRequest memberReservationRequest) {
        return reservationApplicationService.createMemberReservation(
                MemberReservationCreate.from(memberReservationRequest)
        );
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/reservations/{id}")
    public void delete(@PathVariable("id") final long reservationId) {
        reservationApplicationService.delete(reservationId);
    }

    @GetMapping("/members")
    public List<MemberResponse> findAll() {
        return memberService.findAll();
    }

    @PostMapping("/reservations/{id}/waiting/approve")
    public void approve(@LoginUser AuthInfo authInfo, @PathVariable("id") long memberReservationId) {
        reservationApplicationService.approveWaiting(authInfo, memberReservationId);
    }

    @PostMapping("/reservations/{id}/waiting/deny")
    public void deny(@LoginUser AuthInfo authInfo,
                     @PathVariable("id") long memberReservationId) {
        reservationApplicationService.denyWaiting(authInfo, memberReservationId);
    }
}
