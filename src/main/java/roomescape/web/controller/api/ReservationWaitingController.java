package roomescape.web.controller.api;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import roomescape.service.ReservationWaitingService;
import roomescape.service.request.ReservationWaitingSaveAppRequest;
import roomescape.service.response.ReservationWaitingAppResponse;
import roomescape.web.auth.Auth;
import roomescape.web.controller.request.LoginMember;
import roomescape.web.controller.request.ReservationWaitingRequest;
import roomescape.web.controller.response.ReservationWaitingResponse;
import roomescape.web.controller.response.ReservationWaitingWithRankResponse;

@Controller
@RequestMapping("/reservation-waitings")
public class ReservationWaitingController {

    private final ReservationWaitingService reservationWaitingService;

    public ReservationWaitingController(ReservationWaitingService reservationWaitingService) {
        this.reservationWaitingService = reservationWaitingService;
    }

    @PostMapping
    public ResponseEntity<ReservationWaitingResponse> save(@Valid @RequestBody ReservationWaitingRequest request,
                                                           @Valid @Auth LoginMember loginMember) {

        ReservationWaitingAppResponse waitingAppResponse = reservationWaitingService.save(
                ReservationWaitingSaveAppRequest.of(request, loginMember.id())
        );
        ReservationWaitingResponse waitingWebResponse = ReservationWaitingResponse.from(waitingAppResponse);

        return ResponseEntity.created(URI.create("/reservation-waitings/" + waitingWebResponse.id()))
                .body(waitingWebResponse);
    }

    @GetMapping("/mine")
    public ResponseEntity<List<ReservationWaitingWithRankResponse>> findMyWaitingWithRank(
            @Valid @Auth LoginMember loginMember) {
        Long memberId = loginMember.id();
        List<ReservationWaitingWithRankResponse> waitingWithRankWebResponses =
                reservationWaitingService.findWaitingWithRankByMemberId(memberId).stream()
                        .map(ReservationWaitingWithRankResponse::from)
                        .toList();

        return ResponseEntity.ok(waitingWithRankWebResponses);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@Valid @Auth LoginMember loginMember, @PathVariable Long id) {
        reservationWaitingService.deleteMemberWaiting(loginMember.id(), id);

        return ResponseEntity.noContent().build();
    }
}
