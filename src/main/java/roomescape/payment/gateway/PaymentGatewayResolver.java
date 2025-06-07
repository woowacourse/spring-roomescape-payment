package roomescape.payment.gateway;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import roomescape.payment.domain.PaymentMethod;

@Component
public class PaymentGatewayResolver {

    private final Map<PaymentMethod, PaymentGateway> gatewayMap = new HashMap<>();

    public PaymentGatewayResolver(List<PaymentGateway> gateWays) {
        for (PaymentGateway gateway : gateWays) {
            gatewayMap.put(gateway.supports(), gateway);
        }
    }

    public PaymentGateway resolve(PaymentMethod method) {
        return gatewayMap.get(method);
    }
}
