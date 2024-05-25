package domainapp.modules.petowner.dom.petowner;

import java.util.List;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.Column;

import org.apache.causeway.applib.annotation.Action;
import org.apache.causeway.applib.annotation.ActionLayout;
import org.apache.causeway.applib.annotation.DomainService;
import org.apache.causeway.applib.annotation.Editing;
import org.apache.causeway.applib.annotation.Optionality;
import org.apache.causeway.applib.annotation.Parameter;
import org.apache.causeway.applib.annotation.PriorityPrecedence;
import org.apache.causeway.applib.annotation.PromptStyle;
import org.apache.causeway.applib.annotation.Property;
import org.apache.causeway.applib.annotation.PropertyLayout;
import org.apache.causeway.applib.annotation.SemanticsOf;
import org.apache.causeway.applib.layout.LayoutConstants;
import org.apache.causeway.applib.query.Query;
import org.apache.causeway.applib.services.repository.RepositoryService;
import org.apache.causeway.persistence.jpa.applib.services.JpaSupportService;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import domainapp.modules.petowner.PetOwnerModule;
import domainapp.modules.petowner.types.Name;

import lombok.Setter;

@Named(PetOwnerModule.NAMESPACE + ".PetOwners")
@DomainService
@Priority(PriorityPrecedence.EARLY)
@RequiredArgsConstructor(onConstructor_ = {@Inject} )
public class PetOwners {

    final RepositoryService repositoryService;
    final JpaSupportService jpaSupportService;
    final PetOwnerRepository petOwnerRepository;


    @Action(semantics = SemanticsOf.NON_IDEMPOTENT)
    // @ActionLayout(promptStyle = PromptStyle.DIALOG_SIDEBAR)
    public PetOwner create(
            @Name final String name,
            @Parameter(maxLength = 40, optionality = Optionality.OPTIONAL)
            final String knownAs,
            @Parameter(maxLength = 40, optionality = Optionality.OPTIONAL)
            final String telephoneNumber,
            @Parameter(maxLength = 40, optionality = Optionality.OPTIONAL)
            final String emailAddress) {
        final var petOwner = PetOwner.withName(name);
        petOwner.setKnownAs(knownAs);
        petOwner.setTelephoneNumber(telephoneNumber);
        petOwner.setEmailAddress(emailAddress);
        return repositoryService.persist(petOwner);
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
