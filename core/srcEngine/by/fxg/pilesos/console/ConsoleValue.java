package by.fxg.pilesos.console;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.FIELD})
public @interface ConsoleValue {
	public String name();
	public ValueType type();
	public String desc();
	public boolean args() default false;
}
