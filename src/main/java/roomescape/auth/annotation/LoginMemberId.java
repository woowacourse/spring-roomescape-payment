package roomescape.auth.annotation;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Tag(name = "로그인 멤버 id 어노테이션", description = "해당 어노테이션을 파라미터 앞에 사용하면 현재 로그인 중인 멤버의 id를 반환한다.")
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginMemberId {
}
