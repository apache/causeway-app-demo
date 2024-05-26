package domainapp.modules.petowner.dom.pet;

import domainapp.modules.petowner.PetOwnerModule;
import domainapp.modules.petowner.dom.petowner.PetOwner;
import domainapp.modules.petowner.types.PetName;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.Comparator;

import javax.inject.Named;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.causeway.applib.annotation.DomainObject;
import org.apache.causeway.applib.annotation.DomainObjectLayout;
import org.apache.causeway.applib.annotation.PropertyLayout;
import org.apache.causeway.applib.annotation.Publishing;
import org.apache.causeway.applib.annotation.Title;
import org.apache.causeway.applib.jaxb.PersistentEntityAdapter;
import org.apache.causeway.persistence.jpa.applib.integration.CausewayEntityListener;

@Entity
@Table(
        schema= PetOwnerModule.SCHEMA,
        uniqueConstraints = {
                @UniqueConstraint(name = "Pet__owner_name__UNQ", columnNames = {"owner_id, name"})
        }
)
@EntityListeners(CausewayEntityListener.class)
@Named(PetOwnerModule.NAMESPACE + ".Pet")
@DomainObject(entityChangePublishing = Publishing.ENABLED)
@DomainObjectLayout()
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@XmlJavaTypeAdapter(PersistentEntityAdapter.class)
@ToString(onlyExplicitlyIncluded = true)
public class Pet implements Comparable<Pet> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    @Getter @Setter
    private Long id;

    @Version
    @Column(name = "version", nullable = false)
    @PropertyLayout(fieldSetId = "metadata", sequence = "999")
    @Getter @Setter
    private long version;


    Pet(PetOwner petOwner, String name) {
        this.petOwner = petOwner;
        this.name = name;
    }


    @ManyToOne(optional = false)
    @JoinColumn(name = "owner_id")
    @PropertyLayout(fieldSetId = "identity", sequence = "1")
    @Getter @Setter
    private PetOwner petOwner;

    @PetName
    @Title
    @Column(name = "name", length = PetName.MAX_LEN, nullable = false)
    @Getter @Setter
    @PropertyLayout(fieldSetId = "identity", sequence = "2")
    private String name;


    private final static Comparator<Pet> comparator =
            Comparator.comparing(Pet::getPetOwner).thenComparing(Pet::getName);

    @Override
    public int compareTo(final Pet other) {
        return comparator.compare(this, other);
    }

}
