package roomescape.core.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.core.dto.member.LoginMember;
import roomescape.core.dto.reservation.MemberReservationRequest;
import roomescape.core.dto.reservation.MyReservationResponse;
import roomescape.core.dto.reservation.ReservationResponse;
import roomescape.core.service.ReservationService;

@Tag(name = "예약 관리 API")
@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    public ReservationController(final ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Operation(summary = "로그인 한 회원의 예약 생성")
    @PostMapping
    public ResponseEntity<ReservationResponse> create(
            @Valid @RequestBody MemberReservationRequest memberReservationRequest,
            @Parameter(hidden = true) LoginMember loginMember) {
        ReservationResponse reservationResponse = reservationService.create(memberReservationRequest, loginMember);
        return ResponseEntity.created(URI.create("/reservations/" + reservationResponse.getId()))
                .body(reservationResponse);
    }

    @Operation(summary = "모든 예약 조회")
    @GetMapping
    public ResponseEntity<List<ReservationResponse>> findAll() {
        return ResponseEntity.ok(reservationService.findAll());
    }

    @Operation(summary = "모든 예약 대기 조회")
    @GetMapping("/waiting")
    public ResponseEntity<List<ReservationResponse>> findAllWaiting() {
        return ResponseEntity.ok(reservationService.findAllWaiting());
    }

    @Operation(summary = "특정 기간동안 특정 테마에 대해 특정 회원의 예약 조회")
    @GetMapping(params = {"memberId", "themeId", "dateFrom", "dateTo"})
    public ResponseEntity<List<ReservationResponse>> findAllByMemberAndThemeAndPeriod(
            @RequestParam(required = false, name = "memberId") final Long memberId,
            @RequestParam(required = false, name = "themeId") final Long themeId,
            @RequestParam(required = false, name = "dateFrom") final String dateFrom,
            @RequestParam(required = false, name = "dateTo") final String dateTo) {
        return ResponseEntity.ok(
                reservationService.findAllByMemberAndThemeAndPeriod(memberId, themeId, dateFrom, dateTo)
        );
    }

    @Operation(summary = "로그인 한 회원의 모든 예약 조회")
    @GetMapping("/mine")
    public ResponseEntity<List<MyReservationResponse>> findAllByLoginMember(
            @Parameter(hidden = true) final LoginMember loginMember) {
        return ResponseEntity.ok(reservationService.findAllByMember(loginMember));
    }

    @Operation(summary = "예약 삭제")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") final long id) {
        reservationService.delete(id);
        return ResponseEntity.noContent()
                .build();
    }
}
