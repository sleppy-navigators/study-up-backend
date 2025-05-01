package sleppynavigators.studyupbackend.presentation.common.argument;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * An annotation that marks a method parameter as a search parameter, which can be used to indicate that the parameter
 * should be treated as a search parameter in the context of a web request (query string)
 * <br>
 * <b>Note!</b> search parameters must <b>always</b> be optional. Be especially careful when implementing an argument
 * resolver
 */
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SearchParam {
}
