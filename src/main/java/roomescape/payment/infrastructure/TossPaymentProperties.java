package roomescape.payment.infrastructure;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;


@Getter
@Validated
@RequiredArgsConstructor
@ConfigurationProperties("toss-payment")
public class TossPaymentProperties {

    @NotBlank(message = "widgetSecretKey은 비어있을 수 없습니다.")
    private final String widgetSecretKey;
}
