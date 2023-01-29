package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;

    /** 회원가입 */
    public Long join(Member member){
        //잠깐 중복회원 검증
        validateDuplicateMember(member);
        
        memberRepository.save(member);
        return member.getId();
    }
  //그거 아시나요. @GeneratedValue를 하면 영속성에 있어도 Id값 매팽해줌
    private void validateDuplicateMember(Member member) { //완벽한 검증로직은 아님. 보완필요
        //EXCEPTION
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if(!findMembers.isEmpty()){
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }
    /** 회원 전체 조회 */
    @Transactional(readOnly = true)

    public List<Member> findMembers(){
        return  memberRepository.findAll();
    }
    /** 회원 단건 조회 */
    @Transactional(readOnly = true)
    public Member findOne(Long memberId){
        return memberRepository.findOne(memberId);
    }

}
