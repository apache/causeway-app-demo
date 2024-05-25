package domainapp.modules.petowner.types;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.causeway.applib.annotation.Editing;
import org.apache.causeway.applib.annotation.Optionality;
import org.apache.causeway.applib.annotation.Parameter;
import org.apache.causeway.applib.annotation.ParameterLayout;
import org.apache.causeway.applib.annotation.Property;

@Property(
        editing = Editing.ENABLED,
        maxLength = PhoneNumber.MAX_LEN,
        optionality = Optionality.OPTIONAL,
        regexPattern = PhoneNumber.REGEX_PATTERN,
        regexPatternReplacement = PhoneNumber.REGEX_PATTERN_REPLACEMENT
)
@Parameter(
        maxLength = PhoneNumber.MAX_LEN,
        optionality = Optionality.OPTIONAL,
        regexPattern = PhoneNumber.REGEX_PATTERN,
        regexPatternReplacement = PhoneNumber.REGEX_PATTERN_REPLACEMENT
)
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface PhoneNumber {

    int MAX_LEN = 40;
    String REGEX_PATTERN = "[+]?[0-9 ]+";
    String REGEX_PATTERN_REPLACEMENT =
            "Specify only numbers and spaces, optionally prefixed with '+'.  " +
            "For example, '+353 1 555 1234', or '07123 456789'";

}