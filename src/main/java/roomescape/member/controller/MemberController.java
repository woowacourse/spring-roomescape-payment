
package roomescape.member.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.member.domain.dto.ReservationWithStateDto;
import roomescape.user.domain.User;
import roomescape.user.domain.dto.UserResponseDto;
import roomescape.user.service.UserService;

import java.util.List;

@Tag(name = "멤버 API", description = "멤버 관련 API입니다.")
@RestController
@RequestMapping("/members")
public class MemberController {

    private final UserService memberService;

    public MemberController(UserService memberService) {
        this.memberService = memberService;
    }

    @Operation(summary = "모든 멤버 조회", description = "모든 멤버 정보 조회")
    @GetMapping
    public ResponseEntity<List<UserResponseDto>> findAll() {
        List<UserResponseDto> userResponseDtos = memberService.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(userResponseDtos);
    }

    @Operation(summary = "나의 예약 페이지 조회", description = "멤버 계정으로 나의 예약 목록을 조회합니다.")
    @GetMapping("/reservations-mine")
    public ResponseEntity<List<ReservationWithStateDto>> findAllReservationsByMember(User member) {
        List<ReservationWithStateDto> reservations = memberService.findAllReservationByMember(member);
        return ResponseEntity.status(HttpStatus.OK).body(reservations);
    }

    @Operation(summary = "나의 예약 페이지에서 예약 대기 취소", description = "나의 예약 페이지에서 예약 대기를 취소합니다.")
    @DeleteMapping("/reservations-mine/{id}")
    public ResponseEntity<Void> deleteWaitingByMember(@PathVariable(value = "id") Long waitingId, User member) {
        memberService.deleteWaitingByMember(waitingId, member);
        return ResponseEntity.noContent().build();
    }
}
