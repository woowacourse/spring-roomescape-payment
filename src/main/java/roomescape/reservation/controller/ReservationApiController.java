package roomescape.reservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.dto.LoginMember;
import roomescape.common.dto.MultipleResponses;
import roomescape.payment.dto.TossPaymentCancelResponse;
import roomescape.reservation.domain.ReservationStatus;
import roomescape.reservation.dto.MemberReservationResponse;
import roomescape.reservation.dto.ReservationCancelReason;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.dto.UserReservationSaveRequest;
import roomescape.reservation.service.ReservationService;

@Tag(name = "회원 예약 API", description = "방탈출 일반 유저과 관리자의 예약 API 입니다.")
@RestController
public class ReservationApiController {

    private final ReservationService reservationService;

    public ReservationApiController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @Operation(summary = "전체 예약 조회 API", description = "전체 예약을 상태별로 조회 합니다.")
    @Parameter(name = "status", description = "예약 상태")
    @GetMapping("/reservations")
    public ResponseEntity<MultipleResponses<ReservationResponse>> findAll(
            @RequestParam(name = "status", defaultValue = "SUCCESS") ReservationStatus reservationStatus
    ) {
        List<ReservationResponse> reservationResponses = reservationService.findAllByStatus(reservationStatus);

        return ResponseEntity.ok(new MultipleResponses<>(reservationResponses));
    }

    @Operation(summary = "회원 별 예약 조회 API", description = "회원 별 예약을 조회 합니다.")
    @GetMapping("/reservations/mine")
    public ResponseEntity<MultipleResponses<MemberReservationResponse>> findMemberReservations(LoginMember loginMember) {
        List<MemberReservationResponse> memberReservationResponses = reservationService.findMemberReservations(loginMember);

        return ResponseEntity.ok(new MultipleResponses<>(memberReservationResponses));
    }

    @Operation(summary = "일반 유저 예약 추가 API", description = "일반 유저의 예약을 추가 합니다.")
    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> saveReservation(
            @Valid @RequestBody UserReservationSaveRequest userReservationSaveRequest,
            LoginMember loginMember
    ) {
        ReservationResponse reservationResponse = reservationService.save(userReservationSaveRequest, loginMember, ReservationStatus.SUCCESS);

        return ResponseEntity.created(URI.create("/reservations/" + reservationResponse.id()))
                .body(reservationResponse);
    }

    @Operation(summary = "예약 대기 추가 API", description = "예약 대기를 추가 합니다.")
    @PostMapping("/reservations/waiting")
    public ResponseEntity<ReservationResponse> saveReservationWaiting(
            @Valid @RequestBody UserReservationSaveRequest userReservationSaveRequest,
            LoginMember loginMember
    ) {
        ReservationResponse reservationResponse = reservationService.save(userReservationSaveRequest, loginMember, ReservationStatus.WAIT);

        return ResponseEntity.created(URI.create("/reservations/" + reservationResponse.id()))
                .body(reservationResponse);
    }

    @Operation(summary = "예약 취소 API", description = "예약을 상태를 취소로 변경합니다.")
    @Parameter(name = "id", description = "취소할 예약의 id", schema = @Schema(type = "integer", example = "1"))
    @PatchMapping("/reservations/{id}")
    public ResponseEntity<TossPaymentCancelResponse> cancel(@PathVariable("id") Long id, @RequestBody ReservationCancelReason reservationCancelReason) {
        TossPaymentCancelResponse tossPaymentCancelResponse = reservationService.cancelById(id, reservationCancelReason);

        return ResponseEntity.ok(tossPaymentCancelResponse);
    }

    @Operation(summary = "예약 삭제 API", description = "예약 데이터를 삭제 합니다.")
    @Parameter(name = "id", description = "삭제할 예약의 id", schema = @Schema(type = "integer", example = "1"))
    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        reservationService.delete(id);

        return ResponseEntity.noContent().build();
    }
}
