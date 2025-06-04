package roomescape.reservation.infrastructure;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import roomescape.reservation.domain.Status;

@Converter
public class ReservationStatusConverter implements AttributeConverter<Status, String> {

    @Override
    public String convertToDatabaseColumn(Status attribute) {
        return attribute.name();
    }

    @Override
    public Status convertToEntityAttribute(String dbData) {
        return Status.valueOf(dbData);
    }
}
