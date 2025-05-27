package roomescape.global.annotation;

import static java.lang.annotation.ElementType.METHOD;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import roomescape.member.domain.MemberRole;

@Target(METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckRole {
    MemberRole value();
}
