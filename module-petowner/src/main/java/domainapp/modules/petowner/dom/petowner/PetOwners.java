package domainapp.modules.petowner.dom.petowner;

import java.util.List;

import javax.annotation.Priority;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.TypedQuery;

import org.apache.causeway.applib.annotation.Action;
import org.apache.causeway.applib.annotation.ActionLayout;
import org.apache.causeway.applib.annotation.DomainService;
import org.apache.causeway.applib.annotation.PriorityPrecedence;
import org.apache.causeway.applib.annotation.PromptStyle;
import org.apache.causeway.applib.annotation.SemanticsOf;
import org.apache.causeway.applib.query.Query;
import org.apache.causeway.applib.services.repository.RepositoryService;
import org.apache.causeway.persistence.jpa.applib.services.JpaSupportService;

import lombok.RequiredArgsConstructor;

import domainapp.modules.petowner.PetOwnerModule;
import domainapp.modules.petowner.types.Name;

@Named(PetOwnerModule.NAMESPACE + ".PetOwners")
@DomainService
@Priority(PriorityPrecedence.EARLY)
@RequiredArgsConstructor(onConstructor_ = {@Inject} )
public class PetOwners {

    final RepositoryService repositoryService;
    final JpaSupportService jpaSupportService;
    final PetOwnerRepository petOwnerRepository;


    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(promptStyle = PromptStyle.DIALOG_SIDEBAR)
    public PetOwner create(
            @Name final String name) {
        return repositoryService.persist(PetOwner.withName(name));
    }


    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    @ActionLayout(promptStyle = PromptStyle.DIALOG_SIDEBAR)
    public List<PetOwner> findByNameLike(
            @Name final String name) {
        return repositoryService.allMatches(
                Query.named(PetOwner.class, PetOwner.NAMED_QUERY__FIND_BY_NAME_LIKE)
                     .withParameter("name", "%" + name + "%"));
    }


    @Action(semantics = SemanticsOf.SAFE)
    @ActionLayout(promptStyle = PromptStyle.DIALOG_SIDEBAR)
    public List<PetOwner> findByName(
            @Name final String name
            ) {
        return petOwnerRepository.findByNameContaining(name);
    }


    public PetOwner findByNameExact(final String name) {
        return petOwnerRepository.findByName(name);
    }



    @Action(semantics = SemanticsOf.SAFE)
    public List<PetOwner> listAll() {
        return petOwnerRepository.findAll();
    }



    public void ping() {
        jpaSupportService.getEntityManager(PetOwner.class)
            .mapEmptyToFailure()
            .mapSuccessAsNullable(entityManager -> {
                final TypedQuery<PetOwner> q = entityManager.createQuery(
                                "SELECT p FROM PetOwner p ORDER BY p.name",
                                PetOwner.class)
                        .setMaxResults(1);
                return q.getResultList();
            })
        .ifFailureFail();
    }

}
