package roomescape.infrastructure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "toss.payments")
public class TossPaymentsProperties {

    private String baseUrl;
    private Api api;
    private String widgetSecretKey;

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Api getApi() {
        return api;
    }

    public void setApi(Api api) {
        this.api = api;
    }

    public String getWidgetSecretKey() {
        return widgetSecretKey;
    }

    public void setWidgetSecretKey(String widgetSecretKey) {
        this.widgetSecretKey = widgetSecretKey;
    }

    public static class Api {
        private String confirmUrl;
        private String refundUrlTemplate;

        public String getConfirmUrl() {
            return confirmUrl;
        }

        public void setConfirmUrl(String confirmUrl) {
            this.confirmUrl = confirmUrl;
        }

        public String getRefundUrlTemplate() {
            return refundUrlTemplate;
        }

        public void setRefundUrlTemplate(String refundUrlTemplate) {
            this.refundUrlTemplate = refundUrlTemplate;
        }
    }
}
