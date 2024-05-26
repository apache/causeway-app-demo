package domainapp.modules.visit.contributions;

import domainapp.modules.petowner.dom.pet.Pet;
import domainapp.modules.petowner.dom.petowner.PetOwner;
import domainapp.modules.visit.dom.visit.Visit;
import domainapp.modules.visit.dom.visit.VisitRepository;

import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.causeway.applib.annotation.Action;
import org.apache.causeway.applib.annotation.Collection;
import org.apache.causeway.applib.annotation.MemberSupport;
import org.apache.causeway.applib.services.repository.RepositoryService;

@Collection
@RequiredArgsConstructor
public class PetOwner_visits {

    private final PetOwner petOwner;

    @MemberSupport
    public List<Visit> coll() {
        return visitRepository.findByPetOwner(petOwner);
    }

    @Inject VisitRepository visitRepository;

}
