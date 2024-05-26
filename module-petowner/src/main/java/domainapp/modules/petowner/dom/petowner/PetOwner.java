package domainapp.modules.petowner.dom.petowner;

import java.time.LocalTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.springframework.lang.Nullable;

import org.apache.causeway.applib.annotation.Action;
import org.apache.causeway.applib.annotation.ActionLayout;
import org.apache.causeway.applib.annotation.BookmarkPolicy;
import org.apache.causeway.applib.annotation.DomainObject;
import org.apache.causeway.applib.annotation.DomainObjectLayout;
import org.apache.causeway.applib.annotation.Editing;
import org.apache.causeway.applib.annotation.MemberSupport;
import org.apache.causeway.applib.annotation.ObjectSupport;
import org.apache.causeway.applib.annotation.Optionality;
import org.apache.causeway.applib.annotation.Property;
import org.apache.causeway.applib.annotation.PropertyLayout;
import org.apache.causeway.applib.annotation.Publishing;
import org.apache.causeway.applib.annotation.TableDecorator;
import org.apache.causeway.applib.jaxb.PersistentEntityAdapter;
import org.apache.causeway.applib.layout.LayoutConstants;
import org.apache.causeway.applib.services.clock.ClockService;
import org.apache.causeway.applib.services.message.MessageService;
import org.apache.causeway.applib.services.repository.RepositoryService;
import org.apache.causeway.applib.services.title.TitleService;
import org.apache.causeway.applib.value.Blob;
import org.apache.causeway.extensions.fullcalendar.applib.CalendarEventable;
import org.apache.causeway.extensions.fullcalendar.applib.value.CalendarEvent;
import org.apache.causeway.extensions.pdfjs.applib.annotations.PdfJsViewer;
import org.apache.causeway.persistence.jpa.applib.integration.CausewayEntityListener;
import org.apache.causeway.persistence.jpa.applib.types.BlobJpaEmbeddable;

import static org.apache.causeway.applib.annotation.SemanticsOf.IDEMPOTENT;
import static org.apache.causeway.applib.annotation.SemanticsOf.NON_IDEMPOTENT_ARE_YOU_SURE;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.val;

import domainapp.modules.petowner.PetOwnerModule;
import domainapp.modules.petowner.types.Name;
import domainapp.modules.petowner.types.Notes;
import domainapp.modules.petowner.types.PhoneNumber;
import domainapp.modules.petowner.value.EmailAddress;


@Entity
@Table(
    schema= PetOwnerModule.SCHEMA,
    uniqueConstraints = {
        @UniqueConstraint(name = "PetOwner__name__UNQ", columnNames = {"name"})
    }
)
@NamedQueries({
        @NamedQuery(
                name = PetOwner.NAMED_QUERY__FIND_BY_NAME_LIKE,
                query = "SELECT so " +
                        "FROM PetOwner so " +
                        "WHERE so.name LIKE :name"
        )
})
@EntityListeners(CausewayEntityListener.class)
@Named(PetOwnerModule.NAMESPACE + ".PetOwner")
@DomainObject(entityChangePublishing = Publishing.ENABLED)
@DomainObjectLayout(
        tableDecorator = TableDecorator.DatatablesNet.class,
        bookmarking = BookmarkPolicy.AS_ROOT)
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@XmlJavaTypeAdapter(PersistentEntityAdapter.class)
@ToString(onlyExplicitlyIncluded = true)
public class PetOwner implements Comparable<PetOwner>, CalendarEventable {

    static final String NAMED_QUERY__FIND_BY_NAME_LIKE = "PetOwner.findByNameLike";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", nullable = false)
    private Long id;

    @Version
    @Column(name = "version", nullable = false)
    @PropertyLayout(fieldSetId = "metadata", sequence = "999")
    @Getter @Setter
    private long version;

    public static PetOwner withName(final String name) {
        val PetOwner = new PetOwner();
        PetOwner.setName(name);
        return PetOwner;
    }

    @Inject @Transient RepositoryService repositoryService;
    @Inject @Transient TitleService titleService;
    @Inject @Transient MessageService messageService;

    @Inject @Transient ClockService clockService;

    @ObjectSupport
    public String title() {
        return getName() + (getKnownAs() != null ? " (" + getKnownAs() + ")" : "");
    }

    @Name
    @Column(length = Name.MAX_LEN, nullable = false, name = "name")
    @Getter @Setter @ToString.Include
    private String name;

    @Column(length = 40, nullable = true, name = "knownAs")
    @Getter @Setter
    @Property(editing = Editing.ENABLED)
    private String knownAs;

    @PhoneNumber
    @Column(length = PhoneNumber.MAX_LEN, nullable = true, name = "telephoneNumber")
    @Getter @Setter
    @PropertyLayout(fieldSetId = "contact", sequence = "1.1")
    private String telephoneNumber;

    @javax.persistence.Embedded
    @Getter @Setter
    @Property(editing = Editing.ENABLED, optionality = Optionality.OPTIONAL)
    @PropertyLayout(fieldSetId = "contact", sequence = "1.2")
    private EmailAddress emailAddress;

    @Notes
    @Column(length = Notes.MAX_LEN, nullable = true)
    @Getter @Setter
    @Property(commandPublishing = Publishing.ENABLED, executionPublishing = Publishing.ENABLED)
    @PropertyLayout(fieldSetId = LayoutConstants.FieldSetId.DETAILS, sequence = "2")
    private String notes;



    @AttributeOverrides({
            @AttributeOverride(name="name",    column=@Column(name="attachment_name")),
            @AttributeOverride(name="mimeType",column=@Column(name="attachment_mimeType")),
            @AttributeOverride(name="bytes",   column=@Column(name="attachment_bytes"))
    })
    @Embedded
    private BlobJpaEmbeddable attachment;

    @PdfJsViewer
    @Property(optionality = Optionality.OPTIONAL)
    @PropertyLayout(fieldSetId = "content", sequence = "1")
    public Blob getAttachment() {
        return BlobJpaEmbeddable.toBlob(attachment);
    }
    public void setAttachment(final Blob attachment) {
        this.attachment = BlobJpaEmbeddable.fromBlob(attachment);
    }



    @Property(optionality = Optionality.OPTIONAL, editing = Editing.ENABLED)
    @PropertyLayout(fieldSetId = LayoutConstants.FieldSetId.DETAILS, sequence = "3")
    @Column(nullable = true)
    @Getter @Setter
    private java.time.LocalDate lastVisit;


    @Property
    @PropertyLayout(fieldSetId = LayoutConstants.FieldSetId.DETAILS, sequence = "3.1")
    public Long getDaysSinceLastVisit() {
        return getLastVisit() != null
                ? ChronoUnit.DAYS.between(getLastVisit(), clockService.getClock().nowAsLocalDate())
                : null;
    }


    @Override
    public String getCalendarName() {
        return "Last checked-in";
    }

    @Override
    public CalendarEvent toCalendarEvent() {
        if (getLastVisit() != null) {
            long epochMillis = getLastVisit().toEpochSecond(LocalTime.MIDNIGHT, ZoneOffset.systemDefault().getRules().getOffset(getLastVisit().atStartOfDay())) * 1000L;
            return new CalendarEvent(epochMillis, getCalendarName(), titleService.titleOf(this), getNotes());
        } else {
            return null;
        }
    }


    @Action(semantics = IDEMPOTENT, commandPublishing = Publishing.ENABLED, executionPublishing = Publishing.ENABLED)
    @ActionLayout(
            associateWith = "name",
            // promptStyle = PromptStyle.INLINE,
            describedAs = "Updates the name of this object, certain characters (" + Name.PROHIBITED_CHARACTERS + ") are not allowed.")
    public PetOwner updateName(
            @Name final String name) {
        setName(name);
        return this;
    }
    @MemberSupport public String default0UpdateName() {
        return getName();
    }



    @Action(semantics = IDEMPOTENT, commandPublishing = Publishing.ENABLED, executionPublishing = Publishing.ENABLED)
    @ActionLayout(associateWith = "attachment", position = ActionLayout.Position.PANEL)
    public PetOwner updateAttachment(
            @Nullable final Blob attachment) {
        setAttachment(attachment);
        return this;
    }
    @MemberSupport public Blob default0UpdateAttachment() {
        return getAttachment();
    }



    @Action(semantics = NON_IDEMPOTENT_ARE_YOU_SURE)
    @ActionLayout(
            fieldSetId = LayoutConstants.FieldSetId.IDENTITY,
            position = ActionLayout.Position.PANEL,
            describedAs = "Deletes this object from the persistent datastore")
    public void delete() {
        final String title = titleService.titleOf(this);
        messageService.informUser(String.format("'%s' deleted", title));
        repositoryService.removeAndFlush(this);
    }



    private final static Comparator<PetOwner> comparator =
            Comparator.comparing(PetOwner::getName);

    @Override
    public int compareTo(final PetOwner other) {
        return comparator.compare(this, other);
    }

}
