package roomescape.core.controller;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import roomescape.core.domain.Payment;
import roomescape.core.dto.auth.PaymentAuthorizationResponse;
import roomescape.core.dto.member.LoginMember;
import roomescape.core.dto.payment.PaymentRequest;
import roomescape.core.dto.payment.PaymentResponse;
import roomescape.core.dto.reservation.MemberReservationRequest;
import roomescape.core.dto.reservation.MyReservationResponse;
import roomescape.core.dto.reservation.ReservationRequest;
import roomescape.core.dto.reservation.ReservationResponse;
import roomescape.core.service.PaymentService;
import roomescape.core.service.ReservationService;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

    private final ReservationService reservationService;
    private final PaymentService paymentService;
    private final RestClient restClient;

    public ReservationController(final ReservationService reservationService, PaymentService paymentService,
                                 RestClient restClient) {
        this.reservationService = reservationService;
        this.paymentService = paymentService;
        this.restClient = restClient;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> create(
            @Valid @RequestBody final MemberReservationRequest memberRequest, final LoginMember member) {
        final PaymentRequest paymentRequest = new PaymentRequest(memberRequest.getPaymentKey(),
                memberRequest.getOrderId(), memberRequest.getAmount());

        approvePayment(paymentRequest, paymentService.createPaymentAuthorization());
        Payment payment = paymentService.save(paymentRequest);

        final ReservationRequest request = new ReservationRequest(member.getId(), memberRequest.getDate(),
                memberRequest.getTimeId(), memberRequest.getThemeId(), memberRequest.getStatus(), payment.getId());
        final ReservationResponse result = reservationService.create(request);
        return ResponseEntity.created(URI.create("/reservations/" + result.getId()))
                .body(result);
    }

    private void approvePayment(final PaymentRequest paymentRequest,
                                final PaymentAuthorizationResponse paymentAuthorizationResponse) {
        restClient.post()
                .uri("/v1/payments/confirm")
                .header("Authorization", paymentAuthorizationResponse.getPaymentAuthorization())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(paymentRequest)
                .retrieve()
                .onStatus(new PaymentErrorHandler());
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> findAll() {
        return ResponseEntity.ok(reservationService.findAll());
    }

    @GetMapping("/waiting")
    public ResponseEntity<List<ReservationResponse>> findAllWaiting() {
        return ResponseEntity.ok(reservationService.findAllWaiting());
    }

    @GetMapping(params = {"memberId", "themeId", "dateFrom", "dateTo"})
    public ResponseEntity<List<ReservationResponse>> findAllByMemberAndThemeAndPeriod(
            @RequestParam(required = false, name = "memberId") final Long memberId,
            @RequestParam(required = false, name = "themeId") final Long themeId,
            @RequestParam(required = false, name = "dateFrom") final String dateFrom,
            @RequestParam(required = false, name = "dateTo") final String dateTo) {
        return ResponseEntity.ok(
                reservationService.findAllByMemberAndThemeAndPeriod(memberId, themeId, dateFrom, dateTo));
    }

    @GetMapping("/mine")
    public ResponseEntity<List<MyReservationResponse>> findAllByLoginMember(final LoginMember loginMember) {
        return ResponseEntity.ok(reservationService.findAllByMember(loginMember));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") final long id) {
        if (reservationService.isNotAdminReservation(id)) {
            refundPayment(reservationService.findPaymentByDeleteReservation(id),
                    paymentService.createPaymentAuthorization());
        }
        reservationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private void refundPayment(final PaymentResponse paymentResponse,
                               final PaymentAuthorizationResponse paymentAuthorizationResponse) {
        restClient.post()
                .uri("/v1/payments/" + paymentResponse.getPaymentKey() + "/cancel")
                .header("Authorization", paymentAuthorizationResponse.getPaymentAuthorization())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .body(Map.of("cancelReason", "고객 변심"))
                .retrieve()
                .onStatus(new PaymentErrorHandler());
    }
}
