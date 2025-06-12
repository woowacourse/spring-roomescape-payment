package roomescape.reservation.domain.utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Convert;
import roomescape.member.domain.Role;
import roomescape.reservation.domain.PaymentMethod;

@Convert
public class PaymentMethodConverter implements AttributeConverter<PaymentMethod, String> {

    @Override
    public String convertToDatabaseColumn(PaymentMethod attribute) {
        return attribute.getCode();
    }

    @Override
    public PaymentMethod convertToEntityAttribute(String dbData) {
        return PaymentMethod.ofCode(dbData);
    }
}
