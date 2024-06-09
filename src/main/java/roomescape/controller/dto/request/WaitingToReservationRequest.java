package roomescape.controller.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import roomescape.domain.member.Member;
import roomescape.service.dto.request.WaitingAcceptRequest;

import java.math.BigDecimal;

public record WaitingToReservationRequest(
        @NotBlank(message = "계좌번호를 입력해주세요.")
        String accountNumber,
        @NotBlank(message = "예금주를 입력해주세요.")
        String accountHolder,
        @NotBlank(message = "은행명을 입력해주세요.")
        String bankName,
        @NotNull(message = "결제 금액을 입력해주세요.")
        @Positive(message = "결제 금액은 양수만 가능합니다.")
        @Max(value = Integer.MAX_VALUE, message = "결제 금액은 2,147,483,647 이하여야 합니다.")
        BigDecimal amount
) {

    public WaitingAcceptRequest toWaitingAcceptRequest(long reservationId, Member member) {
        return new WaitingAcceptRequest(reservationId, member, accountNumber, accountHolder, bankName, amount);
    }
}
