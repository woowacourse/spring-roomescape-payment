package roomescape.member.controller;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import roomescape.member.dto.MemberRegisterRequest;
import roomescape.member.dto.MemberRegisterResponse;
import roomescape.member.dto.MemberResponse;
import roomescape.member.service.MemberService;
import roomescape.reservation.service.ReservationService;

@Controller
public class MemberController {

    private final MemberService memberService;
    private final ReservationService reservationService;

    public MemberController(final MemberService memberService, ReservationService reservationService) {
        this.memberService = memberService;
        this.reservationService = reservationService;
    }

    @PostMapping("/members")
    public ResponseEntity<MemberRegisterResponse> registerMember(@RequestBody final MemberRegisterRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(memberService.addMember(request));
    }

    @GetMapping("/members")
    public ResponseEntity<List<MemberResponse>> getAllMembers() {
        return ResponseEntity.status(HttpStatus.OK).body(memberService.getAllMembers());
    }

//    @GetMapping("/members/reservations")
//    public ResponseEntity<List<MyPageReservationResponse>> getMyReservations(Long memberId) {
//        List<MyPageReservationResponse> reservations = reservationService.getReservationsByMemberId(memberId);
//        return ResponseEntity.ok(reservations);
//    }
}
