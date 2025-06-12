package roomescape.member.log;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import roomescape.member.domain.Member;

@Slf4j
@Component
public class MemberProbe {

    public void login(Member member) {
        log.atInfo().log("login member: id={}, name={}, role={}", member.getId(), member.getName().getValue(), member.getRole());
    }

    public void signup(Member member) {
        log.atInfo().log("signup member: id={}, name={}, role={}", member.getId(), member.getName().getValue(), member.getRole());
    }
}
