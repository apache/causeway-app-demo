package domainapp.webapp.application.services.homepage;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.apache.causeway.applib.annotation.Collection;
import org.apache.causeway.applib.annotation.CollectionLayout;
import org.apache.causeway.applib.annotation.DomainObject;
import org.apache.causeway.applib.annotation.DomainObjectLayout;
import org.apache.causeway.applib.annotation.HomePage;
import org.apache.causeway.applib.annotation.Nature;
import org.apache.causeway.applib.annotation.ObjectSupport;
import org.apache.causeway.applib.annotation.TableDecorator;
import org.apache.causeway.applib.services.clock.ClockService;

import domainapp.modules.petowner.dom.petowner.PetOwner;
import domainapp.modules.petowner.dom.petowner.PetOwners;
import domainapp.modules.simple.SimpleModule;
import domainapp.modules.visit.dom.visit.Visit;
import domainapp.modules.visit.dom.visit.VisitRepository;

@Named(SimpleModule.NAMESPACE + ".HomePageViewModel")
@DomainObject(nature = Nature.VIEW_MODEL)
@HomePage
@DomainObjectLayout()
public class HomePageViewModel {

    @ObjectSupport public String title() {
        return getPetOwners().size() + " pet owners, " + getFutureVisits().size() + " future visits";
    }

    @Collection
    @CollectionLayout(tableDecorator = TableDecorator.DatatablesNet.class)
    public List<PetOwner> getPetOwners() {
        return petOwners.listAll();
    }

    @Collection
    @CollectionLayout(tableDecorator = TableDecorator.DatatablesNet.class)
    public List<Visit> getFutureVisits() {                                  // <.>
        LocalDateTime now = clockService.getClock().nowAsLocalDateTime();
        return visitRepository.findByVisitAtAfter(now);
    }

    @Inject ClockService clockService;
    @Inject VisitRepository visitRepository;
    @Inject PetOwners petOwners;
}
