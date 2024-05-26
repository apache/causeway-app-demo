package domainapp.modules.visit.dom.visit;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import domainapp.modules.petowner.dom.petowner.PetOwner;

public interface VisitRepository extends Repository<Visit, Integer> {

    @Query("select v from Visit v where v.pet.petOwner = :petOwner")
    List<Visit> findByPetOwner(PetOwner petOwner);
}
