package roomescape.infrastructure.payment;

import java.util.Map;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.Builder;

public class PaymentClientBuliders {
    private final Map<String, Builder> builders;

    public PaymentClientBuliders(Map<String, RestClient.Builder> builders) {
        this.builders = builders;
    }

    public Builder get(String name){
        return builders.get(name);
    }
}
