package domainapp.modules.visit.contributions;

import java.util.List;

import jakarta.inject.Inject;

import org.springframework.beans.factory.annotation.Autowired;

import org.apache.causeway.applib.annotation.Action;
import org.apache.causeway.applib.annotation.ActionLayout;
import org.apache.causeway.applib.annotation.MemberSupport;
import org.apache.causeway.applib.services.clock.ClockService;

import lombok.RequiredArgsConstructor;

import domainapp.modules.petowner.dom.petowner.PetOwner;
import domainapp.modules.visit.dom.visit.Visit;
import domainapp.modules.visit.dom.visit.VisitRepository;

@Action
@ActionLayout(associateWith = "visits", sequence = "2")
@RequiredArgsConstructor
public class PetOwner_cancelVisit {

    private final PetOwner petOwner;

    @MemberSupport
    public PetOwner act(Visit visit) {
       return visit.cancel();
    }
    @MemberSupport
    public List<Visit> choices0Act() {
        return visitRepository.findByPetOwnerAndVisitAtAfter(petOwner, clockService.getClock().nowAsLocalDateTime());
    }

    @Inject VisitRepository visitRepository;
    @Inject ClockService clockService;


    @MemberSupport
    public Visit default0Act() {
        List<Visit> visits = choices0Act();
        return visits.size() == 1 ? visits.iterator().next() : null;
    }
    @MemberSupport
    public String disableAct() {
        return choices0Act().isEmpty() ? "No visits to cancel" : null;
    }


}
