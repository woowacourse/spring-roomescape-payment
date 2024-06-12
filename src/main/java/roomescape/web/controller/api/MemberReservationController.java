package roomescape.web.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import roomescape.service.ReservationService;
import roomescape.service.request.ReservationSaveAppRequest;
import roomescape.service.response.ReservationAppResponse;
import roomescape.web.auth.Auth;
import roomescape.web.controller.request.LoginMember;
import roomescape.web.controller.request.MemberReservationRequest;
import roomescape.web.controller.response.MemberReservationResponse;
import roomescape.web.controller.response.ReservationMineResponse;

@Tag(name = "Member-Reservation", description = "회원 예약 API")
@RestController
@RequestMapping("/reservations")
public class MemberReservationController {

    private final ReservationService reservationService;

    public MemberReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Operation(summary = "예약 추가", description = "로그인한 회원 권한으로 예약을 추가합니다.")
    @PostMapping
    public ResponseEntity<MemberReservationResponse> reserve(
            @Valid @RequestBody MemberReservationRequest memberReservationRequest,
            @Valid @Auth LoginMember loginMember) {
        ReservationAppResponse reservationAppResponse = reservationService.save(
                ReservationSaveAppRequest.of(memberReservationRequest, loginMember.id()));

        Long id = reservationAppResponse.id();
        MemberReservationResponse memberReservationResponse = MemberReservationResponse.from(reservationAppResponse);

        return ResponseEntity.created(URI.create("/reservations/" + id))
                .body(memberReservationResponse);
    }

    @Operation(summary = "예약 삭제", description = "예약 id로 예약을 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBy(@PathVariable Long id) {
        reservationService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "전체 예약 조회", description = "전체 예약을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<MemberReservationResponse>> getReservations() {
        List<ReservationAppResponse> appResponses = reservationService.findAll();
        List<MemberReservationResponse> memberReservationResponse = appResponses.stream()
                .map(MemberReservationResponse::from)
                .toList();

        return ResponseEntity.ok(memberReservationResponse);
    }

    @Operation(summary = "내 예약 조회", description = "로그인한 회원의 id로 예약된 예약들을 조회합니다.")
    @GetMapping("/mine")
    public ResponseEntity<List<ReservationMineResponse>> getMyReservations(@Auth LoginMember loginMember) {
        List<ReservationMineResponse> responses = reservationService.findByMemberId(loginMember.id()).stream()
                .map(ReservationMineResponse::from)
                .toList();

        return ResponseEntity.ok(responses);
    }
}
