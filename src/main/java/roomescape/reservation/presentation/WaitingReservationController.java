package roomescape.reservation.presentation;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import roomescape.common.config.SwaggerConfig;
import roomescape.common.exception.handler.ErrorResponse;
import roomescape.common.security.annotation.RequireRole;
import roomescape.common.security.dto.request.MemberInfo;
import roomescape.member.domain.MemberRole;
import roomescape.payment.presentation.dto.request.PaymentApproveRequest;
import roomescape.reservation.application.WaitingReservationApplicationService;
import roomescape.reservation.application.dto.request.WaitingConfirmRequest;
import roomescape.reservation.application.dto.request.WaitingReservationCreateRequest;
import roomescape.reservation.presentation.dto.request.WaitingConfirmWebRequest;
import roomescape.reservation.presentation.dto.request.WaitingReservationCreateWebRequest;
import roomescape.reservation.presentation.dto.response.WaitingWebResponse;
import roomescape.reservationslot.presentation.dto.response.ReservationResponse;

@RestController
@Tag(name = "예약(대기)", description = "대기 중인 예약 관련 API")
public class WaitingReservationController {

    private final WaitingReservationApplicationService waitingReservationApplicationService;

    public WaitingReservationController(
            final WaitingReservationApplicationService waitingReservationApplicationService) {
        this.waitingReservationApplicationService = waitingReservationApplicationService;
    }

    @Operation(summary = "예약 대기 생성",
            description = "예약 대기를 생성합니다.",
            responses = {
                    @ApiResponse(description = "예약 대기 생성 성공", responseCode = "201", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservationResponse.class))),
                    @ApiResponse(description = "- 과거 시간에 예약할 경우\n\n- 이미 예약 혹은 예약 대기 중인 멤버인 경우", responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(description = "- 해당 날짜, 시간, 테마에 예약이 존재하지 않는 경우\n\n- 존재하지 않는 멤버 id인 경우", responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            },
            security = @SecurityRequirement(name = SwaggerConfig.SECURITY_SCHEME_NAME)
    )
    @RequireRole(MemberRole.REGULAR)
    @PostMapping("/waiting-reservations")
    public ResponseEntity<ReservationResponse> create(
            @RequestBody WaitingReservationCreateWebRequest request,
            @Parameter(hidden = true) MemberInfo memberInfo
    ) {
        ReservationResponse reservationResponse = waitingReservationApplicationService.create(
                new WaitingReservationCreateRequest(request.date(), request.timeId(), request.themeId(),
                        memberInfo.id()));
        return ResponseEntity.status(HttpStatus.CREATED).body(reservationResponse);
    }

    @Operation(summary = "모든 예약 대기 조회",
            description = "대기 중인 예약을 모두 조회합니다.",
            responses = {
                    @ApiResponse(description = "조회 성공", responseCode = "200", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = WaitingWebResponse.class))))
            },
            security = @SecurityRequirement(name = SwaggerConfig.SECURITY_SCHEME_NAME)
    )
    @RequireRole(MemberRole.ADMIN)
    @GetMapping("/admin/waiting-reservations")
    public ResponseEntity<List<WaitingWebResponse>> findAll(
    ) {
        List<WaitingWebResponse> responses = waitingReservationApplicationService.findAll();
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "나의 예약 대기 삭제",
            description = "현재 로그인 중인 멤버가 만든 예약 대기 중 하나를 삭제합니다.",
            responses = {
                    @ApiResponse(description = "삭제 성공", responseCode = "204"),
                    @ApiResponse(description = "존재하지 않는 예약 id이거나 내가 만든 예약 대기가 아닌 경우", responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            },
            security = @SecurityRequirement(name = SwaggerConfig.SECURITY_SCHEME_NAME)
    )
    @RequireRole(MemberRole.REGULAR)
    @DeleteMapping("/waiting-reservations/{reservationSlotId}")
    public ResponseEntity<Void> cancel(
            @PathVariable @Schema(description = "예약 대기를 취소할 예약슬롯의 id") Long reservationSlotId,
            @Parameter(hidden = true) MemberInfo memberInfo
    ) {
        waitingReservationApplicationService.cancelByReservationSlotIdAndMemberId(reservationSlotId, memberInfo.id());
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "예약 대기 삭제",
            description = "대기 중인 예약을 하나 삭제합니다.",
            responses = {
                    @ApiResponse(description = "삭제 성공", responseCode = "204"),
                    @ApiResponse(description = "존재하지 않는 예약 id인 경우", responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            },
            security = @SecurityRequirement(name = SwaggerConfig.SECURITY_SCHEME_NAME)
    )
    @RequireRole(MemberRole.ADMIN)
    @DeleteMapping("/admin/waiting-reservations/{waitingId}")
    public ResponseEntity<Void> cancel(
            @PathVariable @Schema(description = "예약 대기를 취소할 예약의 id") Long waitingId
    ) {
        waitingReservationApplicationService.cancel(waitingId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "결제 승인 후 예약 상태로 변경",
            description = "확정된 예약이 존재하지 않는 예약 슬롯의 가장 첫 번째 순서(Payment-Pending)의 예약 대기를 결제 승인 후 예약(CONFIRMED) 상태로 변경합니다.",
            responses = {
                    @ApiResponse(description = "예약 성공", responseCode = "200"),
                    @ApiResponse(description = "이미 확정된 예약이 존재하는 경우\n\n- Payment-Pending 상태의 예약 대기가 존재하지 않는 경우", responseCode = "400", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
                    @ApiResponse(description = "존재하지 않는 예약 슬롯 id인 경우", responseCode = "404", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ErrorResponse.class))),
            },
            security = @SecurityRequirement(name = SwaggerConfig.SECURITY_SCHEME_NAME)
    )
    @RequireRole(MemberRole.REGULAR)
    @PostMapping("/waiting-reservations/confirm")
    public ResponseEntity<Void> confirm(@RequestBody WaitingConfirmWebRequest request) {
        waitingReservationApplicationService.confirm(WaitingConfirmRequest.of(request), PaymentApproveRequest.from(request));
        return ResponseEntity.ok().build();
    }
}
