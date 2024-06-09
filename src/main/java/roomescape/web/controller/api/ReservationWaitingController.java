package roomescape.web.controller.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Reservation-Waiting", description = "예약 대기 API")
@Controller
@RequestMapping("/reservation-waitings")
public class ReservationWaitingController {

    private final ReservationWaitingService reservationWaitingService;

    public ReservationWaitingController(ReservationWaitingService reservationWaitingService) {
        this.reservationWaitingService = reservationWaitingService;
    }

    @Operation(summary = "예약 대기 추가", description = "로그인한 회원이 예약 대기를 추가합니다.")
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

    @Operation(summary = "내 예약 대기 조회",
            description = "로그인한 회원의 id로 저장된 예약 대기와 대기 순서를 모두 조회합니다.")
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

    @Operation(summary = "예약 대기 삭제", description = "로그인한 회원의 id와 예약 대기 id로 예약 대기를 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@Valid @Auth LoginMember loginMember, @PathVariable Long id) {
        reservationWaitingService.deleteMemberWaiting(loginMember.id(), id);

        return ResponseEntity.noContent().build();
    }
}
