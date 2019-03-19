package com.dada.marsframework.serializer;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用于反射JsonObject的注解。
 * @author yuanjl
 *
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface JSONKey {
	/**
	 * JsonObject 中对应的key值。
	 * @return
	 */
	String[] keys();
	/**
	 * JsonObject 中对应的value值的类型,主要用于类型转化，例如将int类型或者string转化成enum。<br>
	 * <p>当JsonObject value 类型 与期待类型不相符 ，需要进行类型转化。
	 * @return
	 */
	Class<?> type();
}
