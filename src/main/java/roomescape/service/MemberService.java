package roomescape.service;

import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import roomescape.dto.request.LoginRequest;
import roomescape.dto.request.SignupRequest;
import roomescape.entity.Member;
import roomescape.exception.custom.InvalidMemberException;
import roomescape.repository.MemberRepository;

@Service
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Member addMember(SignupRequest request) {
        log.info("회원 가입 시작 - email: {}, name: {}", request.email(), request.name());

        if (memberRepository.existsByEmail(request.email())) {
            log.error("중복된 이메일로 가입 시도 - email: {}", request.email());
            throw new InvalidMemberException("동일한 이메일로 추가할 수 없습니다.");
        }

        Member member = memberRepository.save(request.toMember());
        log.info("회원 가입 완료 - memberId: {}, email: {}", member.getId(), member.getEmail());
        return member;
    }

    public List<Member> findAll() {
        log.info("회원 목록 조회 시작");
        List<Member> members = memberRepository.findAll();
        log.info("회원 목록 조회 완료 - 총 {}개", members.size());
        return members;
    }

    public Member findByEmailAndPassword(LoginRequest request) {
        log.info("로그인 시도 - email: {}", request.email());

        Member member = memberRepository.findByEmailAndPassword(request.email(), request.password())
                .orElseThrow(() -> {
                    log.error("유효하지 않은 로그인 정보 - email: {}", request.email());
                    return new InvalidMemberException("유효하지 않은 로그인 정보입니다.");
                });

        log.info("로그인 성공 - memberId: {}, email: {}", member.getId(), member.getEmail());
        return member;
    }

    public Member getMemberById(Long id) {
        log.info("회원 조회 시작 - memberId: {}", id);

        Member member = memberRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("존재하지 않는 회원 조회 시도 - memberId: {}", id);
                    return new InvalidMemberException("존재하지 않는 멤버 ID입니다.");
                });

        log.info("회원 조회 완료 - memberId: {}, email: {}", member.getId(), member.getEmail());
        return member;
    }
}
