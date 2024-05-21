package domainapp.modules.petowner.dom.petowner;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PetOwnerRepository extends JpaRepository<PetOwner, Long> {

    List<PetOwner> findByNameContaining(final String name);

    PetOwner findByName(final String name);

}
