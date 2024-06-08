package roomescape.payment.dto.response;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class PaymentCancelResponseDeserializerTest {

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        SimpleModule simpleModule = new SimpleModule();
        simpleModule.addDeserializer(PaymentCancelResponse.class, new PaymentCancelResponseDeserializer());
        objectMapper.registerModule(simpleModule);
    }

    @Test
    @DisplayName("결제 취소 정보를 역직렬화하여 PaymentCancelResponse 객체를 생성한다.")
    void deserialize() {
        // given
        String json = """
                {
                  "notUsedField": "notUsedValue",
                  "cancels": [
                    {
                      "cancelStatus": "CANCELLED",
                      "cancelReason": "테스트 결제 취소",
                      "cancelAmount": 10000,
                      "canceledAt": "2021-07-01T10:10:10+09:00",
                      "notUsedField": "notUsedValue"
                    }
                  ]
                }""";

        // when
        PaymentCancelResponse response = assertDoesNotThrow(
                () -> objectMapper.readValue(json, PaymentCancelResponse.class));

        // then
        assertEquals("CANCELLED", response.cancelStatus());
        assertEquals("테스트 결제 취소", response.cancelReason());
        assertEquals(10000, response.cancelAmount());
        assertEquals("2021-07-01T10:10:10+09:00", response.canceledAt().toString());
    }
}
