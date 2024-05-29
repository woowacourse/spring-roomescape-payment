package roomescape.core.controller;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
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
import roomescape.core.dto.member.LoginMember;
import roomescape.core.dto.payment.PaymentConfirmRequest;
import roomescape.core.dto.payment.PaymentConfirmResponse;
import roomescape.core.dto.reservation.MyReservationResponse;
import roomescape.core.dto.reservation.ReservationPaymentRequest;
import roomescape.core.dto.reservation.ReservationRequest;
import roomescape.core.dto.reservation.ReservationResponse;
import roomescape.core.service.ReservationService;
import roomescape.infrastructure.PaymentSecretKeyEncoder;

@RestController
@RequestMapping("/reservations")
public class ReservationController {
    private final ReservationService reservationService;
    private final RestClient restClient;
    private final PaymentSecretKeyEncoder paymentSecretKeyEncoder;

    public ReservationController(final ReservationService reservationService,
                                 final RestClient restClient,
                                 final PaymentSecretKeyEncoder paymentSecretKeyEncoder) {
        this.reservationService = reservationService;
        this.restClient = restClient;
        this.paymentSecretKeyEncoder = paymentSecretKeyEncoder;
    }

    @PostMapping
    public ResponseEntity<ReservationResponse> create(
            @Valid @RequestBody final ReservationPaymentRequest memberRequest,
            final LoginMember member) {
        final PaymentConfirmResponse confirmResponse = getPaymentConfirmResponse(memberRequest);

        final ReservationRequest request = new ReservationRequest(member.getId(),
                memberRequest.getDate(), memberRequest.getTimeId(), memberRequest.getThemeId(),
                confirmResponse.getPaymentKey(), confirmResponse.getOrderId());

        final ReservationResponse response = reservationService.create(request);
        return ResponseEntity.created(URI.create("/reservations/" + response.getId()))
                .body(response);
    }

    private PaymentConfirmResponse getPaymentConfirmResponse(
            final ReservationPaymentRequest memberRequest) {
        final String authorizations = paymentSecretKeyEncoder.getEncodedSecretKey();

        return restClient.post()
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", authorizations)
                .body(new PaymentConfirmRequest(memberRequest))
                .retrieve()
                .body(PaymentConfirmResponse.class);
    }

    @GetMapping
    public ResponseEntity<List<ReservationResponse>> findAll() {
        return ResponseEntity.ok(reservationService.findAll());
    }

    @GetMapping(params = {"memberId", "themeId", "dateFrom", "dateTo"})
    public ResponseEntity<List<ReservationResponse>> findAllByMemberAndThemeAndPeriod(
            @RequestParam(required = false, name = "memberId") final Long memberId,
            @RequestParam(required = false, name = "themeId") final Long themeId,
            @RequestParam(required = false, name = "dateFrom") final String dateFrom,
            @RequestParam(required = false, name = "dateTo") final String dateTo) {
        return ResponseEntity.ok(reservationService
                .findAllByMemberAndThemeAndPeriod(memberId, themeId, dateFrom, dateTo));
    }

    @GetMapping("/mine")
    public ResponseEntity<List<MyReservationResponse>> findAllByLoginMember(
            final LoginMember loginMember) {
        return ResponseEntity.ok(reservationService.findAllByMember(loginMember));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") final long id,
                                       final LoginMember loginMember) {
        reservationService.delete(id, loginMember);
        return ResponseEntity.noContent().build();
    }
}
