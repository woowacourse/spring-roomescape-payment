package roomescape.web.controller.api;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.service.ReservationService;
import roomescape.service.request.ReservationSaveDto;
import roomescape.service.response.ReservationDto;
import roomescape.web.auth.Auth;
import roomescape.web.controller.request.LoginMember;
import roomescape.web.controller.request.MemberReservationRequest;
import roomescape.service.request.PaymentApproveDto;
import roomescape.web.controller.response.MemberReservationResponse;
import roomescape.web.controller.response.ReservationMineResponse;

@RestController
@RequestMapping("/reservations")
public class MemberReservationController {

    private final ReservationService reservationService;

    public MemberReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping
    public ResponseEntity<MemberReservationResponse> reserve(@Valid @RequestBody MemberReservationRequest memberReservationRequest,
                                                             @Valid @Auth LoginMember loginMember) {
        PaymentApproveDto paymentApproveDto = memberReservationRequest.toPaymentApproveDto();
        ReservationSaveDto reservationSaveDto = memberReservationRequest.toReservationSaveDto(loginMember.id());

        ReservationDto reservationDto = reservationService.save(reservationSaveDto, paymentApproveDto);
        MemberReservationResponse memberReservationResponse = new MemberReservationResponse(reservationDto);

        return ResponseEntity.created(URI.create("/reservations/" + reservationDto.id()))
                .body(memberReservationResponse);
    }

    @GetMapping
    public ResponseEntity<List<MemberReservationResponse>> getReservations() {
        List<ReservationDto> appResponses = reservationService.findAll();
        List<MemberReservationResponse> memberReservationResponse = appResponses.stream()
                .map(MemberReservationResponse::new)
                .toList();

        return ResponseEntity.ok(memberReservationResponse);
    }

    @GetMapping("/mine")
    public ResponseEntity<List<ReservationMineResponse>> getMyReservations(@Auth LoginMember loginMember) {
        List<ReservationMineResponse> reservationMineResponses = reservationService.findByMemberId(loginMember.id())
                .stream()
                .map(ReservationMineResponse::new)
                .toList();

        return ResponseEntity.ok(reservationMineResponses);
    }

}
