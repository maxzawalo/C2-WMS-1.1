package maxzawalo.c2.base.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.swing.JLabel;

@Target({ ElementType.FIELD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface BoField {
	String caption();

	String type1C() default "";

	String fieldName1C() default "";

	int horizontalAlignment() default JLabel.LEFT;
}