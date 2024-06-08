package roomescape.payment.dto.response;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class PaymentCancelResponseDeserializer extends StdDeserializer<PaymentCancelResponse> {

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(
            "yyyy-MM-dd'T'HH:mm:ssXXX");

    public PaymentCancelResponseDeserializer() {
        this(null);
    }

    public PaymentCancelResponseDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public PaymentCancelResponse deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException {
        JsonNode cancels = (JsonNode) jsonParser.getCodec().readTree(jsonParser).get("cancels").get(0);
        return new PaymentCancelResponse(
                cancels.get("cancelStatus").asText(),
                cancels.get("cancelReason").asText(),
                cancels.get("cancelAmount").asLong(),
                OffsetDateTime.parse(cancels.get("canceledAt").asText())
        );
    }
}
