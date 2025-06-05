package roomescape.reservation.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.global.auth.annotation.AuthenticationPrincipal;
import roomescape.global.auth.annotation.RoleRequired;
import roomescape.global.auth.dto.LoginMember;
import roomescape.member.entity.RoleType;
import roomescape.reservation.dto.request.ReservationCreateRequest;
import roomescape.reservation.dto.request.ReservationReadFilteredRequest;
import roomescape.reservation.dto.response.ReservationByMemberResponse;
import roomescape.reservation.dto.response.ReservationCreateResponse;
import roomescape.reservation.dto.response.ReservationReadFilteredResponse;
import roomescape.reservation.dto.response.ReservationReadResponse;
import roomescape.reservation.service.ReservationService;

@Tag(name = "Reservation", description = "예약 관련 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;

    @Operation(summary = "예약 생성", description = "로그인된 사용자의 예약을 생성합니다.")
    @PostMapping
    public ResponseEntity<ReservationCreateResponse> createReservation(
            @AuthenticationPrincipal LoginMember loginMember,
            @RequestBody @Valid ReservationCreateRequest request
    ) {
        ReservationCreateResponse response = reservationService.createReservation(loginMember.id(), request);
        return ResponseEntity.ok().body(response);
    }

    @Operation(summary = "모든 예약 조회", description = "모든 예약 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<ReservationReadResponse>> getAllReservations() {
        List<ReservationReadResponse> responses = reservationService.getAllReservations();
        return ResponseEntity.ok().body(responses);
    }

    @Operation(summary = "내 예약 조회", description = "로그인된 사용자의 예약 목록을 조회합니다.")
    @GetMapping("/mine")
    public ResponseEntity<List<ReservationByMemberResponse>> getMyReservations(
            @AuthenticationPrincipal LoginMember loginMember
    ) {
        List<ReservationByMemberResponse> responses = reservationService.getReservationsByMember(loginMember);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "예약 삭제", description = "ADMIN 권한으로 예약을 삭제합니다.")
    @DeleteMapping("/{id}")
    @RoleRequired(roleType = RoleType.ADMIN)
    public ResponseEntity<Void> deleteReservation(
            @PathVariable("id") long id
    ) {
        reservationService.deleteReservation(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "예약 필터 조회", description = "ADMIN 권한으로 필터 조건에 맞는 예약 목록을 조회합니다.")
    @GetMapping("/filtered")
    @RoleRequired(roleType = RoleType.ADMIN)
    public ResponseEntity<List<ReservationReadFilteredResponse>> getFilteredReservations(
            @ModelAttribute @Valid ReservationReadFilteredRequest request
    ) {
        List<ReservationReadFilteredResponse> responses = reservationService.getFilteredReservations(request);
        return ResponseEntity.ok(responses);
    }
}
