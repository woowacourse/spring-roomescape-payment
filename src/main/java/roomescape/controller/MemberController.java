package roomescape.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.LoginMemberId;
import roomescape.config.swagger.SwaggerAuthToken;
import roomescape.service.member.MemberService;
import roomescape.service.member.dto.MemberReservationResponse;
import roomescape.service.member.dto.MemberResponse;
import roomescape.service.reservation.ReservationService;

@Tag(name = "Member", description = "사용자 컨트롤러입니다.")
@RestController
@RequestMapping("/members")
public class MemberController {
    private final MemberService memberService;
    private final ReservationService reservationService;

    public MemberController(MemberService memberService, ReservationService reservationService) {
        this.memberService = memberService;
        this.reservationService = reservationService;
    }

    @GetMapping
    public List<MemberResponse> findAllMembers() {
        return memberService.findAll();
    }

    @GetMapping("/reservations")
    public List<MemberReservationResponse> findReservations(@LoginMemberId @SwaggerAuthToken long memberId) {
        return reservationService.findReservationsOf(memberId);
    }
}
