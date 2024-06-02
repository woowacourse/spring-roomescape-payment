package roomescape.config;

import java.util.Map;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.Builder;

public class PaymentClientBuilders {

    private final Map<String, RestClient.Builder> builders;

    public PaymentClientBuilders(Map<String, Builder> builders) {
        this.builders = builders;
    }

    public Builder get(String vendor){
        return builders.get(vendor);
    }
}
