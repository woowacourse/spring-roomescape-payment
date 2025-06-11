package roomescape.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.net.URI;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.dto.auth.CurrentMember;
import roomescape.dto.auth.LoginInfo;
import roomescape.dto.payment.PaymentConfirmRequest;
import roomescape.dto.reservation.ReservationCreateRequest;
import roomescape.dto.reservation.ReservationPaymentRequest;
import roomescape.dto.reservation.ReservationResponse;
import roomescape.service.ReservationPaymentService;
import roomescape.service.ReservationService;

@Tag(name = "예약 API", description = "예약 관련 기능 API입니다.")
@RestController
@RequestMapping("/reservations")
public class ReservationController {
    private static final Logger log = LoggerFactory.getLogger(ReservationController.class);
    private final ReservationService reservationService;
    private final ReservationPaymentService reservationPaymentService;

    public ReservationController(ReservationService reservationService,
                                 ReservationPaymentService reservationPaymentService) {
        this.reservationService = reservationService;
        this.reservationPaymentService = reservationPaymentService;
    }

    @Operation(summary = "전체 예약 조회", description = "모든 예약 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<ReservationResponse>> getAllReservations() {
        List<ReservationResponse> allReservations = reservationService.findAllReservationResponses();
        return ResponseEntity.ok(allReservations);
    }

    @Operation(summary = "예약 추가 및 결제 확인", description = "사용자가 예약과 결제를 동시에 처리합니다.")
    @ApiResponse(responseCode = "201", description = "예약 성공")
    @PostMapping
    public ResponseEntity<String> addReservation(
            @Parameter(hidden = true) @CurrentMember LoginInfo loginInfo,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "예약 및 결제 정보")
            @RequestBody final ReservationPaymentRequest request) {
        ReservationCreateRequest reservationCreateRequest = new ReservationCreateRequest(
                request.date(), request.themeId(), request.timeId(), loginInfo.id());

        PaymentConfirmRequest paymentConfirmRequest = new PaymentConfirmRequest(
                request.orderId(), request.amount(), request.paymentKey(), request.paymentType());

        ReservationResponse response = reservationPaymentService.confirmPaymentAndAddReservation(
                reservationCreateRequest, paymentConfirmRequest);

        log.info("User reservation: memberId={}, themeId={}, date={}, timeId={}",
                loginInfo.id(), request.themeId(), request.date(), request.timeId());

        return ResponseEntity.created(URI.create("reservations/" + response.id())).body("성공했습니다.");
    }

    @Operation(summary = "예약 삭제", description = "특정 예약을 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(
            @Parameter(description = "삭제할 예약 ID") @PathVariable("id") final Long id) {
        reservationService.deleteReservation(id);
        log.info("Reservation deleted: id={}", id);
        return ResponseEntity.noContent().build();
    }
}
