package roomescape.member.service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import roomescape.common.exception.InvalidReservationException;
import roomescape.member.domain.Member;
import roomescape.member.domain.MemberRepository;
import roomescape.member.domain.Role;
import roomescape.member.dto.request.SignupRequest;
import roomescape.member.dto.response.MemberResponse;
import roomescape.member.dto.response.SignupResponse;

@Service
public class MemberService {

    private static final Logger log = LoggerFactory.getLogger(MemberService.class);
    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Transactional
    public SignupResponse createUser(SignupRequest request) {
        log.info("회원 가입 시도 - email: {}", request.email());
        if (memberRepository.existsByEmail(request.email())) {
            log.warn("회원 가입 실패 - 이미 존재하는 이메일: {}", request.email());
            throw new InvalidReservationException("이미 가입된 이메일입니다");
        }

        Member member = Member.createWithoutId(request.name(), request.email(), request.password(), Role.USER);
        Member save = memberRepository.save(member);
        log.info("회원 가입 성공 - memberId: {}, email: {}", save.getId(), save.getEmail());
        return SignupResponse.from(save);
    }

    @Transactional(readOnly = true)
    public List<MemberResponse> findAllMember() {
        List<Member> members = memberRepository.findAll();
        return members.stream()
                .map(MemberResponse::from)
                .toList();
    }
}
