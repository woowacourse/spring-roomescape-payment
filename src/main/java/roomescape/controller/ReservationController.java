package roomescape.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import roomescape.annotation.CheckRole;
import roomescape.dto.request.AddReservationRequest;
import roomescape.dto.request.ConfirmPaymentRequest;
import roomescape.dto.request.CreateReservationRequest;
import roomescape.dto.request.CreateWaitReservationRequest;
import roomescape.dto.request.LoginMemberRequest;
import roomescape.dto.response.MyReservationResponse;
import roomescape.dto.response.ReservationResponse;
import roomescape.dto.response.ReservationWaitResponse;
import roomescape.global.Role;
import roomescape.service.PaymentService;
import roomescape.service.ReservationService;
import roomescape.service.ReservingService;

@Tag(name = "예약", description = "예약 관련 API")
@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final ReservingService reservingService;

    public ReservationController(ReservationService reservationService, ReservingService reservingService) {
        this.reservationService = reservationService;
        this.reservingService = reservingService;
    }

    @Operation(summary = "모든 예약 조회", description = "관리자 권한으로 모든 예약을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "예약 목록 조회 성공")
    @ApiResponse(responseCode = "403", description = "권한 없음")
    @GetMapping
    @CheckRole(Role.ADMIN)
    public ResponseEntity<List<ReservationResponse>> getReservations() {
        List<ReservationResponse> responses = reservationService.findAll();

        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "내 예약 조회", description = "로그인한 사용자의 예약 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "내 예약 목록 조회 성공")
    @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    @GetMapping("/mine")
    @CheckRole({ Role.USER, Role.ADMIN })
    public ResponseEntity<List<MyReservationResponse>> getMyReservation(
            @Parameter(description = "로그인한 사용자 정보") LoginMemberRequest loginMemberRequest) {
        List<MyReservationResponse> responses = reservationService.findAllReservationOfMember(
                loginMemberRequest.id());

        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "예약 생성", description = "새로운 예약을 생성하고 결제를 진행합니다.")
    @ApiResponse(responseCode = "201", description = "예약 생성 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청")
    @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    @PostMapping
    @CheckRole(value = { Role.ADMIN, Role.USER })
    public ResponseEntity<ReservationResponse> addReservations(
            @Parameter(description = "예약 생성 요청") @RequestBody @Valid CreateReservationRequest request,
            @Parameter(description = "로그인한 사용자 정보") LoginMemberRequest loginMemberRequest) {

        ReservationResponse response = reservingService.reserveAndPay(request, loginMemberRequest);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @Operation(summary = "대기 예약 생성", description = "새로운 대기 예약을 생성합니다.")
    @ApiResponse(responseCode = "201", description = "대기 예약 생성 성공")
    @ApiResponse(responseCode = "400", description = "잘못된 요청")
    @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    @PostMapping("/waiting")
    @CheckRole(value = { Role.ADMIN, Role.USER })
    public ResponseEntity<ReservationWaitResponse> addWaitReservation(
            @Parameter(description = "대기 예약 생성 요청") @RequestBody @Valid CreateWaitReservationRequest request,
            @Parameter(description = "로그인한 사용자 정보") LoginMemberRequest loginMemberRequest) {
        ReservationWaitResponse response = reservationService.addWaitReservation(request, loginMemberRequest);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @Operation(summary = "예약 삭제", description = "기존 예약을 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "예약 삭제 성공")
    @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    @ApiResponse(responseCode = "403", description = "권한 없음")
    @DeleteMapping("/{id}")
    @CheckRole(value = { Role.ADMIN, Role.USER })
    public ResponseEntity<Void> deleteReservations(
            @Parameter(description = "예약 ID") @PathVariable Long id) {
        reservationService.deleteReservation(id);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "대기 예약 삭제", description = "대기 예약을 삭제합니다.")
    @ApiResponse(responseCode = "204", description = "대기 예약 삭제 성공")
    @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자")
    @ApiResponse(responseCode = "403", description = "권한 없음")
    @DeleteMapping("/waiting/{id}")
    @CheckRole(value = { Role.ADMIN, Role.USER })
    public ResponseEntity<Void> deleteWaitReservation(
            @Parameter(description = "대기 예약 ID") @PathVariable Long id) {
        reservationService.deleteReservation(id);

        return ResponseEntity.noContent().build();
    }
}
