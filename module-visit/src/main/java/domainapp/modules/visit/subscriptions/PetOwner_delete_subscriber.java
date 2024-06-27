package domainapp.modules.visit.subscriptions;

import domainapp.modules.petowner.dom.petowner.PetOwner;
import domainapp.modules.visit.dom.visit.Visit;
import domainapp.modules.visit.dom.visit.VisitRepository;

import java.util.List;

import jakarta.inject.Inject;

import org.apache.causeway.applib.services.repository.RepositoryService;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class PetOwner_delete_subscriber {

    @EventListener(PetOwner.DeleteActionDomainEvent.class)
    void on(PetOwner.DeleteActionDomainEvent event) {
        PetOwner subject = event.getSubject();
        switch (event.getEventPhase()) {
            case HIDE:
                break;
            case DISABLE:
                break;
            case VALIDATE:
                break;
            case EXECUTING:
                List<Visit> visits = visitRepository.findByPetOwner(subject);
                for (Visit visit : visits) {
                    repositoryService.remove(visit);
                }
                break;
            case EXECUTED:
                break;
        }
    }

    @Inject VisitRepository visitRepository;
    @Inject RepositoryService repositoryService;
}
