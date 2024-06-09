package roomescape.payment.infrastructure;

import jakarta.persistence.AttributeConverter;
import org.springframework.stereotype.Component;
import roomescape.global.exception.DataNotConvertedException;
import roomescape.global.exception.NotFoundException;
import roomescape.payment.domain.PGCompany;

@Component
public class PGCompanyPersistConverter implements AttributeConverter<PGCompany, String> {

    @Override
    public String convertToDatabaseColumn(PGCompany attribute) {
        if (attribute == null) {
            throw new DataNotConvertedException("company 속성이 null이어서 db에 저장할 수 없습니다.");
        }
        return attribute.getIdentifier();
    }

    @Override
    public PGCompany convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            throw new DataNotConvertedException("company 속성의 dbData가 비어있습니다.");
        }
        try {
            return PGCompany.from(dbData);
        } catch (NotFoundException e) {
            throw new DataNotConvertedException("company 속성의 dbData가 유효하지 않습니다.");
        }
    }
}
