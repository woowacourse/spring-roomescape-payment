package roomescape.reservation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.dto.LoginMember;
import roomescape.common.dto.MultipleResponses;
import roomescape.reservation.dto.AdminReservationSaveRequest;
import roomescape.reservation.dto.ReservationResponse;
import roomescape.reservation.dto.ReservationSearchConditionRequest;
import roomescape.reservation.dto.ReservationWaitingResponse;
import roomescape.reservation.service.AdminReservationService;

@Tag(name = "관리자용 API", description = "방탈출 관리자용 API 입니다.")
@RestController
@RequestMapping("/admin")
public class AdminReservationApiController {

    private final AdminReservationService adminReservationService;

    public AdminReservationApiController(AdminReservationService adminReservationService) {
        this.adminReservationService = adminReservationService;
    }

    @Operation(summary = "관리자 예약 추가 API", description = "관리자가 회원의 예약을 추가 합니다.")
    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> saveReservation(
            @Valid @RequestBody AdminReservationSaveRequest adminReservationSaveRequest,
            LoginMember loginMember
    ) {
        ReservationResponse reservationResponse = adminReservationService.save(adminReservationSaveRequest, loginMember);

        return ResponseEntity.created(URI.create("/reservations/" + reservationResponse.id()))
                .body(reservationResponse);
    }

    @Operation(summary = "예약 조건 조회 API", description = "회원, 테마, 기간 별로 예약을 조회 합니다.")
    @GetMapping("/reservations/search")
    public ResponseEntity<MultipleResponses<ReservationResponse>> findAllBySearchCond(
            @Valid @ModelAttribute ReservationSearchConditionRequest reservationSearchConditionRequest
    ) {
        List<ReservationResponse> reservationResponses = adminReservationService.findAllBySearchCondition(
                reservationSearchConditionRequest);

        return ResponseEntity.ok(new MultipleResponses<>(reservationResponses));
    }

    @Operation(summary = "예약 대기 조회 API", description = "대기 상태의 예약들을 조회 합니다.")
    @GetMapping("/reservations/waiting")
    public ResponseEntity<MultipleResponses<ReservationWaitingResponse>> findWaitingReservations() {
        List<ReservationWaitingResponse> waitingReservations = adminReservationService.findWaitingReservations();

        return ResponseEntity.ok(new MultipleResponses<>(waitingReservations));
    }
}
