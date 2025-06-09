package roomescape.payment.domain;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OrderStatus {

    SUCCESS("결제 성공"),
    FAILED("결제 실패"),
    PENDING("결제 대기")
    ;

    private final String description;
}
