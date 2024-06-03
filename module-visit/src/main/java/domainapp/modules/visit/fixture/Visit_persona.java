package domainapp.modules.visit.fixture;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import javax.inject.Inject;

import org.apache.causeway.applib.services.wrapper.control.SyncControl;

import org.apache.causeway.applib.services.clock.ClockService;
import org.apache.causeway.applib.services.registry.ServiceRegistry;
import org.apache.causeway.testing.fakedata.applib.services.FakeDataService;
import org.apache.causeway.testing.fixtures.applib.personas.BuilderScriptWithResult;
import org.apache.causeway.testing.fixtures.applib.personas.Persona;
import org.apache.causeway.testing.fixtures.applib.setup.PersonaEnumPersistAll;

import domainapp.modules.petowner.fixture.PetOwner_persona;
import domainapp.modules.visit.contributions.PetOwner_bookVisit;
import domainapp.modules.visit.contributions.PetOwner_visits;
import domainapp.modules.visit.dom.visit.Visit;

import domainapp.modules.visit.dom.visit.VisitRepository;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.val;
import lombok.experimental.Accessors;

import domainapp.modules.petowner.dom.petowner.PetOwner;

/**
 * Returns the most recent Visit, or the one scheduled.
 */
@RequiredArgsConstructor
public enum Visit_persona
implements Persona<Visit, Visit_persona.Builder> {

    JAMAL_VISITS(PetOwner_persona.JAMAL),
    CAMILA_VISITS(PetOwner_persona.CAMILA),
    ARJUN_VISITS(PetOwner_persona.ARJUN),
    NIA_VISITS(PetOwner_persona.NIA),
    OLIVIA_VISITS(PetOwner_persona.OLIVIA),
    LEILA_VISITS(PetOwner_persona.LEILA),
    MATT_VISITS(PetOwner_persona.MATT),
    BENJAMIN_VISITS(PetOwner_persona.BENJAMIN),
    JESSICA_VISITS(PetOwner_persona.JESSICA),
    DANIEL_VISITS(PetOwner_persona.DANIEL);

    private final PetOwner_persona petOwner_p;

    @Override
    public Builder builder() {
        return new Builder().setPersona(this);
    }

    @Override
    public Visit findUsing(final ServiceRegistry serviceRegistry) {
        final var owner = petOwner_p.findUsing(serviceRegistry);
        final var visits = serviceRegistry.lookupService(VisitRepository.class)
                .map(x -> x.findByPetOwner(owner))
                .orElseThrow();
        return lastOf(visits);
    }

    private static Visit lastOf(List<Visit> visits) {
        return visits.get(visits.size()-1);
    }

    @Accessors(chain = true)
    public static class Builder extends BuilderScriptWithResult<Visit> {

        @Getter @Setter private Visit_persona persona;

        @Override
        protected Visit buildResult(final ExecutionContext ec) {

            final var petOwner = ec.executeChildT(this, persona.petOwner_p);

            petOwner.getPets().forEach(pet -> {

                // in the past
                final var numVisits = fakeDataService.ints().between(2, 4);
                for (var i = 0; i < numVisits; i++) {
                    final var daysAgo = fakeDataService.ints().between(5, 500);
                    final var minsInTheDay = randomAppointmentTime();
                    final var appointmentTime = randomAppointmentTimeFromToday(-daysAgo, minsInTheDay);
                    wrapperFactory.wrapMixin(PetOwner_bookVisit.class, petOwner, SyncControl.control().withSkipRules()).act(pet, appointmentTime);
                }

                // in the future
                if (fakeDataService.booleans().coinFlip()) {
                    final var daysAhead = fakeDataService.ints().between(1, 10);
                    final var minsInTheDay = randomAppointmentTime();
                    final var appointmentTime = randomAppointmentTimeFromToday(daysAhead, minsInTheDay);
                    wrapperFactory.wrapMixin(PetOwner_bookVisit.class, petOwner, SyncControl.control().withSkipRules()).act(pet, appointmentTime);
                }
            });

            final var numDaysAgo = fakeDataService.ints().between(2, 100);
            final var lastVisit = clockService.getClock().nowAsLocalDate().minusDays(numDaysAgo);
            petOwner.setLastVisit(lastVisit);

            final var visits = wrapperFactory.wrapMixin(PetOwner_visits.class, petOwner, SyncControl.control().withSkipRules()).coll();
            return lastOf(visits);
        }

        private LocalDateTime randomAppointmentTimeFromToday(int days, int appointmentTime) {
            return clockService.getClock().nowAsLocalDate().atStartOfDay().plusDays(days).plusMinutes(appointmentTime);
        }

        private int randomAppointmentTime() {
            return (9 * 60) + (fakeDataService.ints().between(0, 32) * 15);
        }

        // -- DEPENDENCIES

        @Inject ClockService clockService;
        @Inject FakeDataService fakeDataService;
    }

    public static class PersistAll
            extends PersonaEnumPersistAll<Visit, Visit_persona, Builder> {
        public PersistAll() {
            super(Visit_persona.class);
        }
    }


}
