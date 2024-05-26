package domainapp.webapp.application.services.homepage.mixins;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.apache.causeway.applib.annotation.Action;
import org.apache.causeway.applib.annotation.ActionLayout;
import org.apache.causeway.applib.annotation.MemberSupport;
import org.apache.causeway.applib.services.factory.FactoryService;
import org.apache.causeway.applib.services.wrapper.WrapperFactory;

import lombok.RequiredArgsConstructor;

import domainapp.modules.petowner.dom.pet.Pet;
import domainapp.modules.petowner.dom.petowner.PetOwner;
import domainapp.modules.petowner.dom.petowner.PetOwnerRepository;
import domainapp.modules.visit.contributions.PetOwner_bookVisit;
import domainapp.modules.visit.dom.visit.VisitRepository;
import domainapp.webapp.application.services.homepage.HomePageViewModel;

@Action
@ActionLayout(associateWith = "futureVisits")
@RequiredArgsConstructor
public class HomePageViewModel_bookVisit {

    final HomePageViewModel homePageViewModel;

    @MemberSupport
    public Object act(
            PetOwner petOwner, Pet pet, LocalDateTime visitAt,
            boolean showVisit) {
        wrapperFactory.wrapMixin(PetOwner_bookVisit.class, petOwner).act(pet, visitAt);
        if (showVisit) {
            return visitRepository.findByPetAndVisitAt(pet, visitAt);
        }
        return homePageViewModel;
    }
    @MemberSupport
    public List<PetOwner> autoComplete0Act(final String lastName) {
        return petOwnerRepository.findByNameContaining(lastName);
    }
    @MemberSupport
    public Set<Pet> choices1Act(PetOwner petOwner) {
        if(petOwner == null) {
            return Collections.emptySet();
        }
        return petOwner.getPets();
    }
    @MemberSupport
    public LocalDateTime default2Act(PetOwner petOwner, Pet pet) {
        if(petOwner == null || pet == null) {
            return null;
        }
        return factoryService.mixin(PetOwner_bookVisit.class, petOwner).default1Act();
    }
    @MemberSupport
    public String validate2Act(PetOwner petOwner, Pet pet, LocalDateTime visitAt) {
        return factoryService.mixin(PetOwner_bookVisit.class, petOwner).validate1Act(visitAt);
    }

    @Inject VisitRepository visitRepository;
    @Inject PetOwnerRepository petOwnerRepository;
    @Inject WrapperFactory wrapperFactory;
    @Inject FactoryService factoryService;
}
