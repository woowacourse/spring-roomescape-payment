package roomescape.controller;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Base64;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;
import roomescape.dto.AdminReservationRequest;
import roomescape.dto.LoginMemberRequest;
import roomescape.dto.PaymentRequest;
import roomescape.dto.PaymentResponse;
import roomescape.dto.ReservationDetailResponse;
import roomescape.dto.ReservationRequest;
import roomescape.dto.ReservationResponse;
import roomescape.dto.ReservationWithPaymentRequest;
import roomescape.service.ReservationService;

@RestController
public class ReservationController {
    private final ReservationService reservationService;


    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponse> saveReservation(@Authenticated LoginMemberRequest loginMemberRequest,
                                                               @RequestBody ReservationWithPaymentRequest reservationWithPaymentRequest) {

        RestClient tossApi = RestClient.builder().baseUrl("https://api.tosspayments.com").build();
        String widgetSecretKey = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6";
        Base64.Encoder encoder = Base64.getEncoder();
        byte[] encodedBytes = encoder.encode((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
        String authorizations = "Basic " + new String(encodedBytes);

        PaymentResponse body = tossApi.post()
                .uri("v1/payments/confirm")
                .header("Authorization", authorizations)
                .contentType(MediaType.APPLICATION_JSON)
                .body(PaymentRequest.from(reservationWithPaymentRequest))
                .retrieve()
                .body(PaymentResponse.class);

        ReservationResponse savedReservationResponse =
                reservationService.saveByUser(loginMemberRequest,
                        ReservationRequest.from(reservationWithPaymentRequest));
        return ResponseEntity.created(URI.create("/reservations/" + savedReservationResponse.id()))
                .body(savedReservationResponse);
    }

    @PostMapping("admin/reservations")
    public ResponseEntity<ReservationResponse> saveReservationByAdmin(
            @RequestBody AdminReservationRequest reservationRequest) {
        ReservationResponse reservationResponse = reservationService.saveByAdmin(reservationRequest);
        return ResponseEntity.created(URI.create("/reservations/" + reservationResponse.id()))
                .body(reservationResponse);
    }

    @GetMapping("/reservations")
    public List<ReservationResponse> findAllReservations() {
        return reservationService.findAll();
    }

    @GetMapping("/reservations/waiting")
    @AdminOnly
    public List<ReservationResponse> findAllRemainedWaiting() {
        return reservationService.findAllRemainedWaiting();
    }

    @GetMapping("/reservations/mine")
    public List<ReservationDetailResponse> findMemberReservations(
            @Authenticated LoginMemberRequest loginMemberRequest) {
        return reservationService.findAllByMemberId(loginMemberRequest.id());
    }

    @GetMapping("/reservations/search")
    public List<ReservationResponse> searchReservation(@RequestParam(required = false) Long themeId,
                                                       @RequestParam(required = false) Long memberId,
                                                       @RequestParam(defaultValue = "#{T(java.time.LocalDate).now()}")
                                                       LocalDate dateFrom,
                                                       @RequestParam(defaultValue = "#{T(java.time.LocalDate).now()}")
                                                       LocalDate dateTo) {
        return reservationService.searchReservation(themeId, memberId, dateFrom, dateTo);
    }

    @DeleteMapping("/reservations/{id}")
    public ResponseEntity<Void> delete(@Authenticated LoginMemberRequest loginMemberRequest,
                                       @PathVariable long id) {
        reservationService.deleteByUser(loginMemberRequest, id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/admin/reservations/{id}")
    @AdminOnly
    public ResponseEntity<Void> deleteByAdmin(@PathVariable long id) {
        reservationService.deleteWaitingByAdmin(id);
        return ResponseEntity.noContent().build();
    }
}
