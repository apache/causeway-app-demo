package domainapp.modules.petowner.types;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.causeway.applib.annotation.Parameter;
import org.apache.causeway.applib.annotation.ParameterLayout;
import org.apache.causeway.applib.annotation.Property;
import org.apache.causeway.applib.spec.AbstractSpecification;

@Property(maxLength = Name.MAX_LEN, mustSatisfy = Name.Spec.class)
@Parameter(maxLength = Name.MAX_LEN, mustSatisfy = Name.Spec.class)
@ParameterLayout(named = "Name")
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Name {

    int MAX_LEN = 40;
    String PROHIBITED_CHARACTERS = "&%$!";

    class Spec extends AbstractSpecification<String> {
        @Override public String satisfiesSafely(String candidate) {
            for (char prohibitedCharacter : PROHIBITED_CHARACTERS.toCharArray()) {
                if( candidate.contains(""+prohibitedCharacter)) {
                    return "Character '" + prohibitedCharacter + "' is not allowed.";
                }
            }
            return null;
        }
    }
}
