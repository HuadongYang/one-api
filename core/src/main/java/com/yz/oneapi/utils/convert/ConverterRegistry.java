package com.yz.oneapi.utils.convert;

import com.yz.oneapi.utils.ObjectUtil;
import com.yz.oneapi.utils.TypeUtil;
import com.yz.oneapi.utils.convert.impl.*;
import com.yz.oneapi.utils.date.DateTime;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.*;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 转换器登记中心
 * <p>
 * 将各种类型Convert对象放入登记中心，通过convert方法查找目标类型对应的转换器，将被转换对象转换之。
 * </p>
 * <p>
 * 在此类中，存放着默认转换器和自定义转换器
 * </p>
 *
 * @author Looly
 */
public class ConverterRegistry implements Serializable {
	/**
	 * 默认类型转换器
	 */
	private Map<Type, Converter<?>> defaultConverterMap;
	/**
	 * 获得单例的 ConverterRegistry
	 *
	 * @return ConverterRegistry
	 */
	public static ConverterRegistry getInstance() {
		return SingletonHolder.INSTANCE;
	}
	/**
	 * 类级的内部类，也就是静态的成员式内部类，该内部类的实例与外部类的实例 没有绑定关系，而且只有被调用到才会装载，从而实现了延迟加载
	 */
	private static class SingletonHolder {
		/**
		 * 静态初始化器，由JVM来保证线程安全
		 */
		private static final ConverterRegistry INSTANCE = new ConverterRegistry();
	}
	/**
	 * 构造
	 */
	public ConverterRegistry() {
		defaultConverter();
	}
	/**
	 * 转换值为指定类型<br>
	 * 自定义转换器优先
	 *
	 * @param <T>          转换的目标类型（转换器转换到的类型）
	 * @param type         类型
	 * @param value        值
	 * @param defaultValue 默认值
	 * @return 转换后的值
	 * @throws ConvertException 转换器不存在
	 */
	public <T> T convert(Type type, Object value, T defaultValue) {
		return convert(type, value, defaultValue, true);
	}

	/**
	 * 转换值为指定类型
	 *
	 * @param <T>           转换的目标类型（转换器转换到的类型）
	 * @param type          类型目标
	 * @param value         被转换值
	 * @param defaultValue  默认值
	 * @param isCustomFirst 是否自定义转换器优先
	 * @return 转换后的值
	 * @throws ConvertException 转换器不存在
	 */
	@SuppressWarnings("unchecked")
	public <T> T convert(Type type, Object value, T defaultValue, boolean isCustomFirst) throws ConvertException {
		if (TypeUtil.isUnknown(type) && null == defaultValue) {
			// 对于用户不指定目标类型的情况，返回原值
			return (T) value;
		}
		if (ObjectUtil.isNull(value)) {
			return defaultValue;
		}
		if (TypeUtil.isUnknown(type)) {
			type = defaultValue.getClass();
		}



		// 标准转换器
		final Converter<T> converter = getConverter(type);
		if (null != converter) {
			return converter.convert(value, defaultValue);
		}

		Class<T> rowType = (Class<T>) TypeUtil.getClass(type);
		if (null == rowType) {
			if (null != defaultValue) {
				rowType = (Class<T>) defaultValue.getClass();
			} else {
				// 无法识别的泛型类型，按照Object处理
				return (T) value;
			}
		}


		// 无法转换
		throw new ConvertException("Can not Converter from [{}] to [{}]", value.getClass().getName(), type.getTypeName());
	}
	/**
	 * 获得转换器<br>
	 *
	 * @param <T>           转换的目标类型
	 * @param type          类型
	 * @return 转换器
	 */
	public <T> Converter<T> getConverter(Type type) {
		Converter<T> converter = getDefaultConverter(type);
		return converter;
	}
	/**
	 * 获得默认转换器
	 *
	 * @param <T>  转换的目标类型（转换器转换到的类型）
	 * @param type 类型
	 * @return 转换器
	 */
	@SuppressWarnings("unchecked")
	public <T> Converter<T> getDefaultConverter(Type type) {
		return (null == defaultConverterMap) ? null : (Converter<T>) defaultConverterMap.get(type);
	}

	/**
	 * 注册默认转换器
	 *
	 * @return 转换器
	 */
	private ConverterRegistry defaultConverter() {
		defaultConverterMap = new ConcurrentHashMap<>();

		// 原始类型转换器
		defaultConverterMap.put(int.class, new PrimitiveConverter(int.class));
		defaultConverterMap.put(long.class, new PrimitiveConverter(long.class));
		defaultConverterMap.put(byte.class, new PrimitiveConverter(byte.class));
		defaultConverterMap.put(short.class, new PrimitiveConverter(short.class));
		defaultConverterMap.put(float.class, new PrimitiveConverter(float.class));
		defaultConverterMap.put(double.class, new PrimitiveConverter(double.class));
		defaultConverterMap.put(char.class, new PrimitiveConverter(char.class));
		defaultConverterMap.put(boolean.class, new PrimitiveConverter(boolean.class));

		// 包装类转换器
		defaultConverterMap.put(Number.class, new NumberConverter());
		defaultConverterMap.put(Integer.class, new NumberConverter(Integer.class));
		defaultConverterMap.put(AtomicInteger.class, new NumberConverter(AtomicInteger.class));// since 3.0.8
		defaultConverterMap.put(Long.class, new NumberConverter(Long.class));
		defaultConverterMap.put(AtomicLong.class, new NumberConverter(AtomicLong.class));// since 3.0.8
		defaultConverterMap.put(Byte.class, new NumberConverter(Byte.class));
		defaultConverterMap.put(Short.class, new NumberConverter(Short.class));
		defaultConverterMap.put(Float.class, new NumberConverter(Float.class));
		defaultConverterMap.put(Double.class, new NumberConverter(Double.class));
		defaultConverterMap.put(Character.class, new CharacterConverter());
		defaultConverterMap.put(Boolean.class, new BooleanConverter());
		defaultConverterMap.put(BigDecimal.class, new NumberConverter(BigDecimal.class));
		defaultConverterMap.put(BigInteger.class, new NumberConverter(BigInteger.class));
		defaultConverterMap.put(CharSequence.class, new StringConverter());
		defaultConverterMap.put(String.class, new StringConverter());


		// 日期时间
		defaultConverterMap.put(Calendar.class, new CalendarConverter());
		defaultConverterMap.put(java.util.Date.class, new DateConverter(java.util.Date.class));
		defaultConverterMap.put(DateTime.class, new DateConverter(DateTime.class));
		defaultConverterMap.put(java.sql.Date.class, new DateConverter(java.sql.Date.class));
		defaultConverterMap.put(java.sql.Time.class, new DateConverter(java.sql.Time.class));
		defaultConverterMap.put(java.sql.Timestamp.class, new DateConverter(java.sql.Timestamp.class));

		// 日期时间 JDK8+(since 5.0.0)
		defaultConverterMap.put(TemporalAccessor.class, new TemporalAccessorConverter(Instant.class));
		defaultConverterMap.put(Instant.class, new TemporalAccessorConverter(Instant.class));
		defaultConverterMap.put(LocalDateTime.class, new TemporalAccessorConverter(LocalDateTime.class));
		defaultConverterMap.put(LocalDate.class, new TemporalAccessorConverter(LocalDate.class));
		defaultConverterMap.put(LocalTime.class, new TemporalAccessorConverter(LocalTime.class));
		defaultConverterMap.put(ZonedDateTime.class, new TemporalAccessorConverter(ZonedDateTime.class));
		defaultConverterMap.put(OffsetDateTime.class, new TemporalAccessorConverter(OffsetDateTime.class));
		defaultConverterMap.put(OffsetTime.class, new TemporalAccessorConverter(OffsetTime.class));


		return this;
	}
}
