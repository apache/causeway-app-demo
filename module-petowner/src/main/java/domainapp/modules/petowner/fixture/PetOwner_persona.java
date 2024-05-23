package domainapp.modules.petowner.fixture;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;

import javax.inject.Inject;

import org.springframework.core.io.ClassPathResource;

import org.apache.causeway.applib.services.clock.ClockService;
import org.apache.causeway.applib.services.registry.ServiceRegistry;
import org.apache.causeway.applib.value.Blob;
import org.apache.causeway.testing.fakedata.applib.services.FakeDataService;
import org.apache.causeway.testing.fixtures.applib.personas.BuilderScriptWithResult;
import org.apache.causeway.testing.fixtures.applib.personas.Persona;
import org.apache.causeway.testing.fixtures.applib.setup.PersonaEnumPersistAll;

import domainapp.modules.petowner.dom.petowner.PetOwners;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.val;
import lombok.experimental.Accessors;

import domainapp.modules.petowner.dom.petowner.PetOwner;

@RequiredArgsConstructor
public enum PetOwner_persona
implements Persona<PetOwner, PetOwner_persona.Builder> {

    JAMAL("Jamal Washington", "jamal.pdf", "J"),
    CAMILA("Camila González", "camila.pdf", null),
    ARJUN("Arjun Patel", "arjun.pdf", null),
    NIA("Nia Robinson", "nia.pdf", null),
    OLIVIA("Olivia Hartman", "olivia.pdf", null),
    LEILA("Leila Hassan", "leila.pdf", null),
    MATT("Matthew Miller", "matt.pdf", "Matt"),
    BENJAMIN("Benjamin Thatcher", "benjamin.pdf", "Ben"),
    JESSICA("Jessica Raynor", "jessica.pdf", "Jess"),
    DANIEL("Daniel Keating", "daniel.pdf", "Dan");

    private final String name;
    private final String contentFileName;
    private final String knownAs;

    @Override
    public Builder builder() {
        return new Builder().setPersona(this);
    }

    @Override
    public PetOwner findUsing(final ServiceRegistry serviceRegistry) {
        return serviceRegistry.lookupService(PetOwners.class).map(x -> x.findByNameExact(name)).orElseThrow();
    }

    @Accessors(chain = true)
    public static class Builder extends BuilderScriptWithResult<PetOwner> {

        @Getter @Setter private PetOwner_persona persona;

        @Override
        protected PetOwner buildResult(final ExecutionContext ec) {

            val petOwner = wrap(petOwners).create(persona.name);

            if (persona.contentFileName != null) {
                val bytes = toBytes(persona.contentFileName);
                val attachment = new Blob(persona.contentFileName, "application/pdf", bytes);
                petOwner.updateAttachment(attachment);
            }
            if (persona.knownAs != null) {
                petOwner.setKnownAs(persona.knownAs);
            }

            final var numDaysAgo = fakeDataService.ints().between(2, 100);
            final var lastVisit = clockService.getClock().nowAsLocalDate().minusDays(numDaysAgo);
            petOwner.setLastVisit(lastVisit);

            return petOwner;
        }

        @SneakyThrows
        private byte[] toBytes(String fileName){
            InputStream inputStream = new ClassPathResource(fileName, getClass()).getInputStream();
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();

            int nRead;
            byte[] data = new byte[16384];

            while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }

            return buffer.toByteArray();
        }

        // -- DEPENDENCIES

        @Inject PetOwners petOwners;
        @Inject ClockService clockService;
        @Inject FakeDataService fakeDataService;
    }

    public static class PersistAll
            extends PersonaEnumPersistAll<PetOwner, PetOwner_persona, Builder> {
        public PersistAll() {
            super(PetOwner_persona.class);
        }
    }


}
