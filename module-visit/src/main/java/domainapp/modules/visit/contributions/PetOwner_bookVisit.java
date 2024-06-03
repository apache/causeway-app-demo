package domainapp.modules.visit.contributions;

import java.time.LocalDateTime;
import java.util.Set;

import jakarta.inject.Inject;

import org.apache.causeway.applib.annotation.Action;
import org.apache.causeway.applib.annotation.ActionLayout;
import org.apache.causeway.applib.annotation.MemberSupport;
import org.apache.causeway.applib.services.clock.ClockService;
import org.apache.causeway.applib.services.repository.RepositoryService;

import lombok.RequiredArgsConstructor;

import domainapp.modules.petowner.dom.pet.Pet;
import domainapp.modules.petowner.dom.petowner.PetOwner;
import domainapp.modules.visit.dom.visit.Visit;

@Action
@ActionLayout(associateWith = "visits")
@RequiredArgsConstructor
public class PetOwner_bookVisit {

    private final PetOwner petOwner;

    @MemberSupport
    public PetOwner act(Pet pet, LocalDateTime visitAt) {
        Visit visit = new Visit(pet, visitAt);
        repositoryService.persistAndFlush(visit);
        return petOwner;
    }
    @MemberSupport
    public Set<Pet> choices0Act() {
        return petOwner.getPets();
    }
    @MemberSupport
    public Pet default0Act() {
        Set<Pet> pets = petOwner.getPets();
        return pets.size() == 1 ? pets.iterator().next() : null;
    }
    @MemberSupport
    public LocalDateTime default1Act() {
        return officeHoursTomorrow();
    }
    @MemberSupport
    public String validate1Act(LocalDateTime visitAt) {
        if (visitAt.isBefore(officeHoursTomorrow())) {
            return "Must book in the future";
        }
        return null;
    }

    private LocalDateTime officeHoursTomorrow() {
        return clockService.getClock().nowAsLocalDate().atStartOfDay().plusDays(1).plusHours(9);
    }


    @Inject ClockService clockService;
    @Inject RepositoryService repositoryService;

}
