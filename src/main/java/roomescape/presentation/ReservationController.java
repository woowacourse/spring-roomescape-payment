package roomescape.presentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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
import roomescape.auth.Authenticated;
import roomescape.dto.request.PaymentRequest;
import roomescape.dto.request.ReservationCreateRequest;
import roomescape.dto.response.ReservationForMemberResponse;
import roomescape.dto.response.ReservationWithStatusResponse;
import roomescape.service.ReservationFacade;
import roomescape.service.ReservationService;

@Tag(name = "예약 API", description = "예약 API 입니다.")
@RestController
@RequestMapping(value = "/api/reservations")
public class ReservationController {
    private static final Logger logger = LoggerFactory.getLogger(ReservationController.class);

    private final ReservationService reservationService;
    private final ReservationFacade reservationFacade;

    public ReservationController(final ReservationService reservationService,
                                 final ReservationFacade reservationFacade) {
        this.reservationService = reservationService;
        this.reservationFacade = reservationFacade;
    }

    @Operation(summary = "회원 예약 생성", description = "결제와 함께 회원의 예약을 저장합니다.")
    @PostMapping
    public ResponseEntity<ReservationForMemberResponse> createNewReservation(
            @Authenticated Long memberId,
            @Valid @RequestBody ReservationCreateRequest request) {
        PaymentRequest paymentRequest = new PaymentRequest(request.amount(), request.paymentKey(), request.orderId());

        ReservationForMemberResponse reservationResponse = reservationFacade.processReservationForMember(
                memberId, request.timeId(), request.themeId(), request.date(), paymentRequest
        );
        logger.info("회원 예약 생성: 회원ID = {}, 테마ID = {}, 날짜 = {}, 시간ID = {}, 금액 = {}",
                memberId,
                request.themeId(),
                request.date(),
                request.timeId(),
                request.amount()
        );
        return ResponseEntity
                .created(URI.create("/reservations/" + reservationResponse.id()))
                .body(reservationResponse);
    }

    @Operation(summary = "예약 삭제", description = "예약을 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReservation(@PathVariable Long id) {
        reservationService.deleteReservationById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "내 예약 조회", description = "특정 회원의 예약을 조회합니다.")
    @GetMapping("/my")
    public List<ReservationWithStatusResponse> getMyBookingHistory(@Authenticated Long id) {
        return reservationService.findBookingHistory(id);
    }
}
