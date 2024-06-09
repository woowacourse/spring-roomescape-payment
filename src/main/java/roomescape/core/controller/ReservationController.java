package roomescape.core.controller;

import io.swagger.v3.oas.annotations.Operation;
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
import roomescape.core.dto.payment.PaymentRequest;
import roomescape.core.dto.payment.TossPaymentRequest;
import roomescape.core.dto.reservation.MemberReservationRequest;
import roomescape.core.dto.reservation.MyReservationResponse;
import roomescape.core.dto.reservation.ReservationRequest;
import roomescape.core.dto.reservation.ReservationResponse;
import roomescape.core.service.PaymentService;
import roomescape.core.service.ReservationService;
import roomescape.infrastructure.PaymentClient;

@Tag(name = "예약 API", description = "예약 관련 API 입니다.")
@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final PaymentService paymentService;
    private final PaymentClient paymentClient;

    public ReservationController(final ReservationService reservationService, final PaymentService paymentService,
                                 final PaymentClient paymentClient) {
        this.reservationService = reservationService;
        this.paymentService = paymentService;
        this.paymentClient = paymentClient;
    }

    @Operation(summary = "예약 API")
    @PostMapping
    public ResponseEntity<ReservationResponse> create(
            @Valid @RequestBody final MemberReservationRequest memberRequest, final LoginMember member) {
        final TossPaymentRequest tossPaymentRequest = new TossPaymentRequest(memberRequest.getPaymentKey(),
                memberRequest.getOrderId(), memberRequest.getAmount());
        paymentClient.approvePayment(tossPaymentRequest, paymentService.createPaymentAuthorization());

        final ReservationRequest request = new ReservationRequest(member.getId(), memberRequest.getDate(),
                memberRequest.getTimeId(), memberRequest.getThemeId(), memberRequest.getStatus());
        final ReservationResponse result = reservationService.create(request);

        final PaymentRequest paymentRequest = new PaymentRequest(memberRequest.getPaymentKey(),
                memberRequest.getOrderId(), memberRequest.getAmount(), result.getId());
        paymentService.save(paymentRequest);
        return ResponseEntity.created(URI.create("/reservations/" + result.getId()))
                .body(result);
    }

    @Operation(summary = "예약 조회 API")
    @GetMapping
    public ResponseEntity<List<ReservationResponse>> findAll() {
        return ResponseEntity.ok(reservationService.findAll());
    }

    @Operation(summary = "예약 대기 조회 API")
    @GetMapping("/waiting")
    public ResponseEntity<List<ReservationResponse>> findAllWaiting() {
        return ResponseEntity.ok(reservationService.findAllWaiting());
    }

    @Operation(summary = "특정 기간, 테마, 사용자 예약 조회 API")
    @GetMapping(params = {"memberId", "themeId", "dateFrom", "dateTo"})
    public ResponseEntity<List<ReservationResponse>> findAllByMemberAndThemeAndPeriod(
            @RequestParam(required = false, name = "memberId") final Long memberId,
            @RequestParam(required = false, name = "themeId") final Long themeId,
            @RequestParam(required = false, name = "dateFrom") final String dateFrom,
            @RequestParam(required = false, name = "dateTo") final String dateTo) {
        return ResponseEntity.ok(
                reservationService.findAllByMemberAndThemeAndPeriod(memberId, themeId, dateFrom, dateTo));
    }

    @Operation(summary = "로그인 회원의 예약 목록 조회 API")
    @GetMapping("/mine")
    public ResponseEntity<List<MyReservationResponse>> findAllByLoginMember(final LoginMember loginMember) {
        return ResponseEntity.ok(reservationService.findAllByMember(loginMember));
    }

    @Operation(summary = "예약 삭제 API")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") final long id) {
        if (paymentService.existPaymentByReservationId(id)) {
            paymentClient.refundPayment(paymentService.findPaymentByReservationId(id),
                    paymentService.createPaymentAuthorization());
        }
        reservationService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
