package com.yz.oneapi.utils.convert.impl;

import com.yz.oneapi.utils.BooleanUtil;
import com.yz.oneapi.utils.StringUtil;
import com.yz.oneapi.utils.convert.AbstractConverter;

/**
 * 字符转换器
 *
 * @author Looly
 *
 */
public class CharacterConverter extends AbstractConverter<Character> {
	private static final long serialVersionUID = 1L;

	@Override
	protected Character convertInternal(Object value) {
		if (value instanceof Boolean) {
			return BooleanUtil.toCharacter((Boolean) value);
		} else {
			final String valueStr = convertToStr(value);
			if (StringUtil.isNotBlank(valueStr)) {
				return valueStr.charAt(0);
			}
		}
		return null;
	}

}
