package roomescape.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.LoginMemberId;
import roomescape.config.swagger.ApiErrorResponse;
import roomescape.config.swagger.ApiSuccessResponse;
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
    @ApiSuccessResponse.Ok("전체 멤버 조회")
    @ApiErrorResponse.Forbidden
    public List<MemberResponse> findAllMembers() {
        return memberService.findAll();
    }

    @GetMapping("/reservations")
    @ApiSuccessResponse.Ok("로그인된 사용자의 예약 조회")
    @ApiErrorResponse.Unauthorized
    public List<MemberReservationResponse> findReservations(@LoginMemberId @SwaggerAuthToken long memberId) {
        return reservationService.findReservationsOf(memberId);
    }
}
