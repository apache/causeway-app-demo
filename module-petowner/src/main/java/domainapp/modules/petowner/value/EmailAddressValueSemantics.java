package domainapp.modules.petowner.value;

import domainapp.modules.petowner.PetOwnerModule;

import lombok.NonNull;

import jakarta.inject.Named;

import org.apache.causeway.applib.services.bookmark.IdStringifier;
import org.apache.causeway.applib.value.semantics.DefaultsProvider;
import org.apache.causeway.applib.value.semantics.Parser;
import org.apache.causeway.applib.value.semantics.Renderer;
import org.apache.causeway.applib.value.semantics.ValueDecomposition;
import org.apache.causeway.applib.value.semantics.ValueSemanticsAbstract;
import org.apache.causeway.commons.internal.base._Strings;

import org.apache.causeway.schema.common.v2.ValueType;
import org.apache.causeway.schema.common.v2.ValueWithTypeDto;

import org.springframework.stereotype.Component;

@Named(PetOwnerModule.NAMESPACE + ".EmailAddressValueSemantics")
@Component
public class EmailAddressValueSemantics
        extends ValueSemanticsAbstract<EmailAddress> {

    @Override
    public Class<EmailAddress> getCorrespondingClass() {
        return EmailAddress.class;
    }

    @Override
    public ValueType getSchemaValueType() {
        return ValueType.STRING;
    }

    @Override
    public ValueDecomposition decompose(final EmailAddress value) {
        return decomposeAsNullable(value, EmailAddress::getValue, ()->null);
    }

    @Override
    public EmailAddress compose(final ValueDecomposition decomposition) {
        return composeFromNullable(
                decomposition, ValueWithTypeDto::getString, EmailAddress::of, ()->null);
    }

    @Override
    public DefaultsProvider<EmailAddress> getDefaultsProvider() {
        return () -> null;
    }

    @Override
    public Renderer<EmailAddress> getRenderer() {
        return (context, emailAddress) ->  emailAddress == null ? null : emailAddress.getValue();
    }

    @Override
    public Parser<EmailAddress> getParser() {
        return new Parser<>() {

            @Override
            public String parseableTextRepresentation(Context context, EmailAddress emailAddress) {
                return renderTitle(emailAddress, EmailAddress::getValue);
            }

            @Override
            public EmailAddress parseTextRepresentation(Context context, String text) {
                return EmailAddress.of(text);
            }

            @Override
            public int typicalLength() {
                return EmailAddress.TYPICAL_LEN;
            }

            @Override
            public int maxLength() {
                return EmailAddress.MAX_LEN;
            }
        };
    }

    @Override
    public IdStringifier<EmailAddress> getIdStringifier() {
        return new IdStringifier.EntityAgnostic<>() {
            @Override
            public Class<EmailAddress> getCorrespondingClass() {
                return EmailAddressValueSemantics.this.getCorrespondingClass();
            }

            @Override
            public String enstring(@NonNull EmailAddress value) {
                return _Strings.base64UrlEncode(value.getValue());
            }

            @Override
            public EmailAddress destring(@NonNull String stringified) {
                return EmailAddress.of(_Strings.base64UrlDecode(stringified));
            }
        };
    }
}
