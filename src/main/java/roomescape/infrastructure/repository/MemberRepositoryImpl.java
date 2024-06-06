package roomescape.infrastructure.repository;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import roomescape.domain.member.Member;
import roomescape.domain.member.MemberRepository;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepository {
    private final MemberJpaRepository memberJpaRepository;

    @Override
    public Member save(Member user) {
        return memberJpaRepository.save(user);
    }

    @Override
    public Optional<Member> findMember(Long id) {
        return memberJpaRepository.findById(id);
    }

    @Override
    public Optional<Member> findMember(String email, String password) {
        return memberJpaRepository.findByEmailAndPassword(email, password);
    }

    @Override
    public List<Member> getAll() {
        return memberJpaRepository.findAll();
    }

    @Override
    public boolean existsMember(String email) {
        return memberJpaRepository.existsByEmail(email);
    }

    @Override
    public void deleteMember(Member member) {
        memberJpaRepository.delete(member);
    }
}
