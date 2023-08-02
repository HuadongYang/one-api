package com.yz.oneapi.utils.convert.impl;

import com.yz.oneapi.utils.convert.AbstractConverter;

import java.lang.reflect.Type;
import java.util.TimeZone;

/**
 * 字符串转换器，提供各种对象转换为字符串的逻辑封装
 *
 * @author Looly
 */
public class StringConverter extends AbstractConverter<String> {
	private static final long serialVersionUID = 1L;

	@Override
	protected String convertInternal(Object value) {
		if (value instanceof TimeZone) {
			return ((TimeZone) value).getID();
		}   else if (value instanceof Type) {
			return ((Type) value).getTypeName();
		}

		// 其它情况
		return convertToStr(value);
	}


}
