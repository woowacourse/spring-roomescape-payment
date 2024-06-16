package roomescape.auth.annotation;

import io.swagger.v3.oas.annotations.tags.Tag;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import roomescape.member.domain.MemberRole;

@Tag(name = "Auth 어노테이션", description = "해당 어노테이션을 붙여 사용자 Role을 부여할 수 있다.")
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Auth {

    MemberRole[] roles() default MemberRole.MEMBER;
}
