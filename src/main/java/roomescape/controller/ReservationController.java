package roomescape.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import roomescape.domain.LoginMember;
import roomescape.dto.*;
import roomescape.service.ReservationService;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@Tag(name = "일반 사용자 예약 API", description = "일반 사용자 예약 API 입니다.")
@RestController
public class ReservationController {
    private final ReservationService reservationService;

    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Operation(summary = "일반 사용자 예약", description = "일반 사용자가 예약을 추가합니다.")
    @PostMapping("/reservations")
    public ResponseEntity<ReservationPaymentResponse> saveReservation(@Authenticated LoginMember loginMember,
                                                                      @RequestBody ReservationPaymentRequest request) {
        ReservationPaymentResponse response = reservationService.save(loginMember, request);
        return ResponseEntity.created(URI.create("/reservations/" + response.reservationResponse().id()))
                .body(response);
    }

    @Operation(summary = "일반 사용자 예약 대기", description = "일반 사용자가 예약 대기를 추가합니다.")
    @PostMapping("/reservations-waiting")
    public ResponseEntity<ReservationResponse> saveReservationWaiting(@Authenticated LoginMember loginMember,
                                                                      @RequestBody ReservationRequest reservationRequest) {
        ReservationResponse savedReservationResponse = reservationService.saveWaiting(loginMember, reservationRequest);
        return ResponseEntity.created(URI.create("/reservations-waiting/" + savedReservationResponse.id()))
                .body(savedReservationResponse);
    }

    @Operation(summary = "모든 사용자의 전체 예약 조회", description = "모든 사용자의 전체 예약을 조회합니다.")
    @GetMapping("/reservations")
    public List<ReservationResponse> findAllReservations() {
        return reservationService.findAllReservations();
    }

    @Operation(summary = "특정 사용자의 전체 예약 조회", description = "로그인한 특정 사용자의 전체 예약을 조회합니다.")
    @GetMapping("/member/reservation")
    public List<ReservationDetailResponse> findMemberReservations(@Authenticated LoginMember loginMember) {
        return reservationService.findReservationsByMemberId(loginMember.getId());
    }

    @Operation(summary = "특정 조건에 해당하는 모든 사용자의 전체 예약 조회", description = "특정 조건에 해당하는 모든 사용자의 전체 예약을 조회합니다.")
    @GetMapping("/reservations/search")
    public List<ReservationResponse> searchReservation(@RequestParam Long themeId,
                                                       @RequestParam Long memberId,
                                                       @RequestParam(defaultValue = "#{T(java.time.LocalDate).now()}")
                                                       LocalDate dateFrom,
                                                       @RequestParam(defaultValue = "#{T(java.time.LocalDate).now()}")
                                                       LocalDate dateTo) {
        return reservationService.searchReservation(themeId, memberId, dateFrom, dateTo);
    }

    @Operation(summary = "사용자의 예약 삭제", description = "사용자의 예약을 삭제합니다.")
    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id,
                                       @RequestBody ReservationCancelRequest request) {
        reservationService.deleteById(id, request);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "사용자의 예약 대기 삭제", description = "사용자의 예약 대기를 삭제합니다.")
    @DeleteMapping("/reservations-waiting/{id}")
    public ResponseEntity<Void> deleteReservationWaiting(@Authenticated LoginMember loginMember, @PathVariable long id) {
        reservationService.deleteByMemberIdAndId(loginMember, id);
        return ResponseEntity.noContent().build();
    }
}
