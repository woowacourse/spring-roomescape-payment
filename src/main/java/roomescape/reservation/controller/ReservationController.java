package roomescape.reservation.controller;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import roomescape.admin.dto.AdminReservationDetailResponse;
import roomescape.admin.dto.AdminReservationRequest;
import roomescape.application.service.ReservationApplicationService;
import roomescape.auth.annotation.Authenticated;
import roomescape.member.domain.LoginMember;
import roomescape.reservation.dto.ReservationPaymentDetail;
import roomescape.reservation.dto.ReservationPaymentRequest;
import roomescape.reservation.dto.ReservationPaymentResponse;
import roomescape.reservation.dto.ReservationRequest;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.service.ReservationService;

@Tag(name = "Reservation", description = "Reservation API")
@RestController
public class ReservationController {
    private final ReservationService reservationService;
    private final ReservationApplicationService reservationApplicationService;

    public ReservationController(ReservationService reservationService, ReservationApplicationService reservationApplicationService) {
        this.reservationService = reservationService;
        this.reservationApplicationService = reservationApplicationService;
    }

    @Operation(summary = "예약 생성", description = "예약을 하나 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "예약 생성 성공",
                    content = @Content(schema = @Schema(implementation = ReservationPaymentResponse.class))),
            @ApiResponse(responseCode = "400", description = "예약 생성 실패")})
    @PostMapping("/reservations")
    public ResponseEntity<ReservationPaymentResponse> saveReservation(@Parameter(hidden = true)
                                                                      @Authenticated LoginMember loginMember,
                                                                      @RequestBody ReservationPaymentRequest request) {
        ReservationPaymentResponse response = reservationApplicationService.reservationPayment(loginMember, request);
        return ResponseEntity.created(URI.create("/reservations/" + response.reservationResponse().id()))
                .body(response);
    }

    @Operation(summary = "예약 대기 생성", description = "예약 대기를 하나 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "예약 대기 생성 성공",
                    content = @Content(schema = @Schema(implementation = ReservationResponse.class))),
            @ApiResponse(responseCode = "400", description = "예약 대기 생성 실패")})
    @PostMapping("/reservations-waiting")
    public ResponseEntity<ReservationResponse> saveReservationWaiting(@Parameter(hidden = true)
                                                                      @Authenticated LoginMember loginMember,
                                                                      @RequestBody ReservationRequest reservationRequest) {
        ReservationResponse savedReservationResponse = reservationService.saveWaiting(loginMember, reservationRequest);
        return ResponseEntity.created(URI.create("/reservations-waiting/" + savedReservationResponse.id()))
                .body(savedReservationResponse);
    }

    @Operation(summary = "모든 예약 조회", description = "모든 예약을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "모든 예약 리스트 조회 성공",
                    content = @Content(schema = @Schema(implementation = ReservationResponse.class))),
            @ApiResponse(responseCode = "400", description = "모든 예약 리스트 조회 실패")})
    @GetMapping("/reservations")
    public List<ReservationResponse> findAllReservations() {
        return reservationService.findAllReservations();
    }

    @Operation(summary = "특정 예약 조회", description = "특정 예약을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "특정 예약 조회 성공",
                    content = @Content(schema = @Schema(implementation = ReservationResponse.class))),
            @ApiResponse(responseCode = "400", description = "특정 예약 조회 실패")})
    @GetMapping("/reservations/search")
    public List<ReservationResponse> searchReservation(@Parameter(required = true) @RequestParam Long themeId,
                                                       @Parameter(required = true) @RequestParam Long memberId,
                                                       @Parameter(required = true) @RequestParam(defaultValue = "#{T(java.time.LocalDate).now()}")
                                                       LocalDate dateFrom,
                                                       @Parameter(required = true) @RequestParam(defaultValue = "#{T(java.time.LocalDate).now()}")
                                                       LocalDate dateTo) {
        return reservationService.searchReservation(themeId, memberId, dateFrom, dateTo);
    }

    @Operation(summary = "특정 예약 삭제", description = "특정 예약을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "특정 예약 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "특정 예약 삭제 실패")})
    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> delete(@Parameter(required = true, name = "id") @PathVariable long id) {
        reservationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "특정 예약 대기 삭제", description = "특정 예약 대기를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "특정 예약 대기 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "특정 예약 대기 삭제 실패")})
    @DeleteMapping("/reservations-waiting/{id}")
    public ResponseEntity<Void> deleteReservationWaiting(@Parameter(hidden = true)
                                                         @Authenticated LoginMember loginMember,
                                                         @Parameter(required = true, name = "id") @PathVariable long id) {
        reservationService.deleteByMemberIdAndId(loginMember, id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "전체 예약 대기 조회", description = "전체 예약 대기를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "전체 예약 대기 조회 성공",
                    content = @Content(schema = @Schema(implementation = AdminReservationDetailResponse.class))),
            @ApiResponse(responseCode = "400", description = "전체 예약 대기 조회 실패")})
    @GetMapping("admin/reservations-waiting")
    public List<AdminReservationDetailResponse> findAllWaitingReservations() {
        return reservationService.findAllWaitingReservations();
    }

    @Operation(summary = "관리자 권한 예약 생성", description = "관리자 권한으로 예약을 생성합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "관리자 권한 예약 생성 성공",
                    content = @Content(schema = @Schema(implementation = ReservationResponse.class))),
            @ApiResponse(responseCode = "400", description = "관리자 권한 예약 생성 실패")})
    @PostMapping("admin/reservations")
    public ResponseEntity<ReservationResponse> saveReservationByAdmin(@RequestBody AdminReservationRequest reservationRequest) {
        ReservationResponse reservationResponse = reservationService.saveByAdmin(reservationRequest);
        return ResponseEntity.created(URI.create("/reservations/" + reservationResponse.id()))
                .body(reservationResponse);
    }

    @Operation(summary = "관리자 권한 예약 대기 삭제", description = "관리자 권한으로 예약 대기를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "관리자 권한 예약 대기 삭제 성공"),
            @ApiResponse(responseCode = "400", description = "관리자 권한 예약 대기 삭제 실패")})
    @DeleteMapping("admin/reservations-waiting/{id}")
    public ResponseEntity<Void> deleteReservationWaitingByAdmin(
            @Parameter(required = true, name = "id") @PathVariable long id) {
        reservationService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "자신의 예약 내역 조회", description = "자신의 예약 내역을 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "자신의 예약 내역 조회 성공",
                    content = @Content(schema = @Schema(implementation = ReservationPaymentDetail.class))),
            @ApiResponse(responseCode = "400", description = "자신의 예약 내역 조회 실패")})
    @GetMapping("/member/reservation")
    public List<ReservationPaymentDetail> findMemberReservations(
            @Parameter(hidden = true) @Authenticated LoginMember loginMember) {
        return reservationApplicationService.reservationPaymentDetails(loginMember.getId());
    }
}
