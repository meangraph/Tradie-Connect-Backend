package CSIT3214.GroupProject.Service;

import CSIT3214.GroupProject.DataAccessLayer.MembershipRepository;
import CSIT3214.GroupProject.Model.Membership;
import CSIT3214.GroupProject.Model.MembershipType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MembershipService {

    @Autowired
    private MembershipRepository membershipRepository;

    public Membership saveMembership(Membership membership) {
        return membershipRepository.save(membership);
    }

    public Membership updateMembership(Membership membership) {
        return membershipRepository.save(membership);
    }

    public MembershipType getMembershipTypeById(Long id) {
        Membership membership = membershipRepository.findById(id).orElse(null);
        return membership != null ? membership.getMembershipType() : null;
    }

    public List<Membership> getAllMemberships() {
        return membershipRepository.findAll();
    }

    public void deleteMembershipById(Long id) {
        membershipRepository.deleteById(id);
    }
}