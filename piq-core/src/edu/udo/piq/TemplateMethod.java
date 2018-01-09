package edu.udo.piq;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * <p>This annotation should be used to annotate any methods which exist only 
 * to be overwritten by subclasses to extend the behavior of another method. 
 * If a TemplateMethod is overwritten the subclass is not required to call 
 * super.</p>
 * <p>The method body of a TemplatMethod should always be empty. Developers 
 * should assume that super is not called by subclasses overwriting a 
 * TemplateMethod.</p>
 */
@Documented
@Retention(SOURCE)
@Target(METHOD)
public @interface TemplateMethod {}