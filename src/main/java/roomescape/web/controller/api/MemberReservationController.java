package roomescape.web.controller.api;

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
import roomescape.infrastructure.payment.PaymentManager;
import roomescape.service.ReservationService;
import roomescape.service.request.PaymentApproveDto;
import roomescape.service.request.ReservationSaveDto;
import roomescape.service.response.ReservationDto;
import roomescape.web.auth.Auth;
import roomescape.web.controller.request.LoginMember;
import roomescape.web.controller.request.MemberReservationRequest;
import roomescape.web.controller.response.MemberReservationResponse;
import roomescape.web.controller.response.ReservationMineResponse;

@RestController
@RequestMapping("/reservations")
public class MemberReservationController {

    private static final Logger log = LoggerFactory.getLogger(MemberReservationController.class);
    private final ReservationService reservationService;
    private final PaymentManager paymentManager;

    public MemberReservationController(ReservationService reservationService, PaymentManager paymentManager) {
        this.reservationService = reservationService;
        this.paymentManager = paymentManager;
    }

    @PostMapping
    public ResponseEntity<MemberReservationResponse> reserve(@Valid @RequestBody MemberReservationRequest reservationRequest,
                                                             @Valid @Auth LoginMember loginMember) {
        PaymentApproveDto request = new PaymentApproveDto(reservationRequest);
        paymentManager.approve(request);

        ReservationDto appResponse = reservationService.save(
                new ReservationSaveDto(reservationRequest.date(), reservationRequest.timeId(),
                        reservationRequest.themeId(), loginMember.id()), request);

        Long id = appResponse.id();
        MemberReservationResponse memberReservationResponse = MemberReservationResponse.from(appResponse);

        return ResponseEntity.created(URI.create("/reservations/" + id))
                .body(memberReservationResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBy(@PathVariable Long id) {
        reservationService.delete(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<MemberReservationResponse>> getReservations() {
        List<ReservationDto> appResponses = reservationService.findAll();
        List<MemberReservationResponse> memberReservationResponse = appResponses.stream()
                .map(MemberReservationResponse::from)
                .toList();

        return ResponseEntity.ok(memberReservationResponse);
    }

    @GetMapping("/mine")
    public ResponseEntity<List<ReservationMineResponse>> getMyReservations(@Auth LoginMember loginMember) {
        List<ReservationMineResponse> reservationMineRespons = reservationService.findByMemberId(loginMember.id())
                .stream()
                .map(ReservationMineResponse::new)
                .toList();

        return ResponseEntity.ok(reservationMineRespons);
    }

}
