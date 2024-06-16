package roomescape.auth.provider.model;

import io.swagger.v3.oas.annotations.tags.Tag;
import roomescape.auth.domain.Token;

@Tag(name = "토큰 제공자 인터페이스", description = "access token을 제공하고 토큰을 파싱하는 메서드를 구현해야 한다.")
public interface TokenProvider {

    Token getAccessToken(long principal);

    String resolveToken(String token);
}
