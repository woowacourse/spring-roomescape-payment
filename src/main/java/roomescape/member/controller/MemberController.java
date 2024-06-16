package roomescape.member.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.stream.Stream;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import roomescape.auth.annotation.LoginMemberId;
import roomescape.exception.PaymentConfirmException;
import roomescape.member.dto.MemberResponse;
import roomescape.member.service.MemberService;
import roomescape.client.payment.Payment;
import roomescape.client.payment.service.PaymentService;
import roomescape.reservation.dto.ReservationDto;
import roomescape.reservation.service.ReservationService;
import roomescape.waiting.service.WaitingService;
import roomescape.reservation.dto.RegistrationInfoResponse;

@Tag(name = "멤버 컨트롤러", description = "모든 사용자를 반환하거나 특정 사용자의 예약 내역을 반환한다.")
@RestController
public class MemberController {

    private final MemberService memberService;
    private final ReservationService reservationService;
    private final WaitingService waitingService;
    private final PaymentService paymentService;

    public MemberController(MemberService memberService, ReservationService reservationService,
                            WaitingService waitingService, PaymentService paymentService) {
        this.memberService = memberService;
        this.reservationService = reservationService;
        this.waitingService = waitingService;
        this.paymentService = paymentService;
    }

    @GetMapping("/members")
    public List<MemberResponse> memberIdList() {
        return memberService.findMembersId();
    }

    @GetMapping("/member/registrations")
    public List<RegistrationInfoResponse> memberReservationList(@LoginMemberId long memberId) {
        List<ReservationDto> reservationInfo = reservationService.findMemberReservations(memberId);
        List<RegistrationInfoResponse> reservationsOfMember = reservationInfo.stream()
                .map(reservationDto -> {
                    try {
                        Payment payment = paymentService.findPaymentByReservationId(reservationDto.id());
                        return RegistrationInfoResponse.of(reservationDto, payment);
                    } catch (PaymentConfirmException e) {
                        return RegistrationInfoResponse.of(reservationDto);
                    }
                })
                .toList();

        List<RegistrationInfoResponse> waitingsOfMember = waitingService.findMemberWaitingWithRank(memberId)
                .stream()
                .map(RegistrationInfoResponse::from)
                .toList();

        return Stream.concat(reservationsOfMember.stream(), waitingsOfMember.stream())
                .toList();
    }
}
