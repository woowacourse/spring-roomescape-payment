package roomescape.auth.dto;

import java.time.LocalDate;

public record SearchCondition(Long memberId, Long themeId, LocalDate dateFrom, LocalDate dateTo) {
}
