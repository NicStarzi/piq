package edu.udo.piq;

import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * <p>This annotation should be used to annotate methods and constructors 
 * which modify internal state in a way that is required for other methods 
 * to work correctly. A subclass which is overwriting such a method or 
 * constructor should either call super or modify the internal state in the 
 * same or a similar way to ensure the dependent methods will behave as 
 * expected.</p>
 * <p>Any method that invalidates or otherwise updates cached values should 
 * be annotated with this annotation.</p>
 */
@Documented
@Retention(SOURCE)
@Target({ METHOD, CONSTRUCTOR })
public @interface CallSuper {}