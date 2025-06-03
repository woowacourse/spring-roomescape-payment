package roomescape.infrastructure.security;

import javax.crypto.SecretKey;
import java.util.Date;

public record TokenIssueRequest(
        Date issuedAt,
        Date expiration,
        Long identifier,
        SecretKey secretKey
) {

}
