package roomescape.reservation.controller;

import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.dto.AuthenticatedMember;
import roomescape.auth.web.resolver.AuthenticationPrincipal;
import roomescape.payment.application.PaymentService;
import roomescape.reservation.application.UserReservationService;
import roomescape.reservation.application.dto.response.ReservationServiceResponse;
import roomescape.reservation.controller.dto.request.UserCreateReservationRequest;
import roomescape.reservation.controller.dto.response.ReservationResponse;
import roomescape.reservation.controller.dto.response.UserReservationResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/reservations")
public class UserReservationController {

    private final UserReservationService userReservationService;
    private final PaymentService paymentService;

    //TODO : 멱등키 생성 및 저장으로 중복 호출 방지
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public ReservationResponse create(
            @RequestBody @Valid UserCreateReservationRequest request,
            @AuthenticationPrincipal AuthenticatedMember authenticatedMember
    ) {
        Long memberId = authenticatedMember.id();

        ReservationServiceResponse response = userReservationService.create(request.toServiceRequest(memberId));
        paymentService.requestApprovalToToss(request.toTossPaymentApproveInfo());
        paymentService.updateStatusToSuccess(response.paymentId());
        return ReservationResponse.from(response);
    }

    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/mine")
    public List<UserReservationResponse> getAll(
            @AuthenticationPrincipal AuthenticatedMember member
    ) {
        return userReservationService.getAllByMemberId(member.id())
                .stream()
                .map(UserReservationResponse::from)
                .toList();
    }

    @ResponseStatus(HttpStatus.OK)
    @PostMapping("{id}/cancel")
    public void cancel(
            @PathVariable("id") Long id,
            @AuthenticationPrincipal AuthenticatedMember authenticatedMember
    ) {
        Long memberId = authenticatedMember.id();
        userReservationService.cancel(id, memberId);
    }
}
