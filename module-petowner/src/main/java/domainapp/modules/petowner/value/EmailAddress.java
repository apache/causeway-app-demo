package domainapp.modules.petowner.value;

import jakarta.persistence.Column;

import lombok.Getter;

import java.io.Serializable;
import java.util.regex.Pattern;

import org.apache.causeway.commons.internal.base._Strings;

@javax.persistence.Embeddable
@org.apache.causeway.applib.annotation.Value
@lombok.EqualsAndHashCode
public class EmailAddress implements Serializable {

    static final int MAX_LEN = 100;
    static final int TYPICAL_LEN = 30;
    static final Pattern REGEX = Pattern.compile("^[\\w-\\+]+(\\.[\\w]+)*@[\\w-]+(\\.[\\w]+)*(\\.[a-zA-Z]{2,})$");

    public static EmailAddress of(String value) {
        if (_Strings.isNullOrEmpty(value)) {
            return null;
        }
        if(!REGEX.matcher(value).matches()) {
            throw new RuntimeException("Invalid email format");
        }

        final var ea = new EmailAddress();
        ea.value = value;
        return ea;
    }

    protected EmailAddress() {} // required by JPA

    @Getter
    @Column(length = MAX_LEN, nullable = true, name = "emailAddress")
    String value;
}