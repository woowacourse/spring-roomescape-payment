package roomescape.presentation.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import roomescape.application.service.ReservationTicketService;
import roomescape.dto.LoginMember;
import roomescape.dto.response.MemberReservationResponseDto;

@Tag(name = "회원 전용 예약 관련 API")
@RestController
@RequestMapping("/reservations-mine")
@RequiredArgsConstructor
public class MemberReservationTicketController {

    private final ReservationTicketService reservationTicketService;

    @Operation(summary = "회원의 예약 정보 조회")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<MemberReservationResponseDto> getMemberReservations(LoginMember loginMember) {
        return reservationTicketService.getReservationTicketsOfMember(loginMember);
    }
}
