package com.yz.oneapi.utils.date;

import com.yz.oneapi.utils.ObjectUtil;
import com.yz.oneapi.utils.StringUtil;
import com.yz.oneapi.utils.date.format.DateParser;
import com.yz.oneapi.utils.date.format.FastDateParser;

import java.text.ParsePosition;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/***
 * @author zhangpeng_jpa
 * @date 2022/5/16 18:19
 * @describe
 */
public class CalendarUtil {
    public static Calendar calendar() {
        return Calendar.getInstance();
    }

    /**
     * 转换为Calendar对象
     *
     * @param date 日期对象
     * @return Calendar对象
     */
    public static Calendar calendar(Date date) {
        if (date instanceof DateTime) {
            return ((DateTime) date).toCalendar();
        } else {
            return calendar(date.getTime());
        }
    }

    /**
     * 转换为Calendar对象
     *
     * @param millis 时间戳
     * @return Calendar对象
     */
    public static Calendar calendar(long millis) {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(millis);
        return cal;
    }

    /**
     * 是否为上午
     *
     * @param calendar {@link Calendar}
     * @return 是否为上午
     */
    public static boolean isAM(Calendar calendar) {
        return Calendar.AM == calendar.get(Calendar.AM_PM);
    }











    /**
     * 获取指定日期字段的最小值，例如分钟的最小值是0
     *
     * @param calendar  {@link Calendar}
     * @param dateField {@link DateField}
     * @return 字段最小值
     * @see Calendar#getActualMinimum(int)
     * @since 4.5.7
     */
    public static int getBeginValue(Calendar calendar, int dateField) {
        if (Calendar.DAY_OF_WEEK == dateField) {
            return calendar.getFirstDayOfWeek();
        }
        return calendar.getActualMinimum(dateField);
    }



    /**
     * 获取指定日期字段的最大值，例如分钟的最大值是59
     *
     * @param calendar  {@link Calendar}
     * @param dateField {@link DateField}
     * @return 字段最大值
     * @see Calendar#getActualMaximum(int)
     * @since 4.5.7
     */
    public static int getEndValue(Calendar calendar, int dateField) {
        if (Calendar.DAY_OF_WEEK == dateField) {
            return (calendar.getFirstDayOfWeek() + 6) % 7;
        }
        return calendar.getActualMaximum(dateField);
    }






    /**
     * 通过给定的日期格式解析日期时间字符串。<br>
     * 传入的日期格式会逐个尝试，直到解析成功，返回{@link Calendar}对象，否则抛出{@link DateException}异常。
     * 方法来自：Apache Commons-Lang3
     *
     * @param str           日期时间字符串，非空
     * @param parsePatterns 需要尝试的日期时间格式数组，非空, 见SimpleDateFormat
     * @return 解析后的Calendar
     * @throws IllegalArgumentException if the date string or pattern array is null
     * @throws DateException            if none of the date patterns were suitable
     * @since 5.3.11
     */
    public static Calendar parseByPatterns(String str, String... parsePatterns) throws DateException {
        return parseByPatterns(str, null, parsePatterns);
    }

    /**
     * 通过给定的日期格式解析日期时间字符串。<br>
     * 传入的日期格式会逐个尝试，直到解析成功，返回{@link Calendar}对象，否则抛出{@link DateException}异常。
     * 方法来自：Apache Commons-Lang3
     *
     * @param str           日期时间字符串，非空
     * @param locale        地区，当为{@code null}时使用{@link Locale#getDefault()}
     * @param parsePatterns 需要尝试的日期时间格式数组，非空, 见SimpleDateFormat
     * @return 解析后的Calendar
     * @throws IllegalArgumentException if the date string or pattern array is null
     * @throws DateException            if none of the date patterns were suitable
     * @since 5.3.11
     */
    public static Calendar parseByPatterns(String str, Locale locale, String... parsePatterns) throws DateException {
        return parseByPatterns(str, locale, true, parsePatterns);
    }

    /**
     * 通过给定的日期格式解析日期时间字符串。<br>
     * 传入的日期格式会逐个尝试，直到解析成功，返回{@link Calendar}对象，否则抛出{@link DateException}异常。
     * 方法来自：Apache Commons-Lang3
     *
     * @param str           日期时间字符串，非空
     * @param locale        地区，当为{@code null}时使用{@link Locale#getDefault()}
     * @param lenient       日期时间解析是否使用严格模式
     * @param parsePatterns 需要尝试的日期时间格式数组，非空, 见SimpleDateFormat
     * @return 解析后的Calendar
     * @throws IllegalArgumentException if the date string or pattern array is null
     * @throws DateException            if none of the date patterns were suitable
     * @see Calendar#isLenient()
     * @since 5.3.11
     */
    public static Calendar parseByPatterns(String str, Locale locale, boolean lenient, String... parsePatterns) throws DateException {
        if (str == null || parsePatterns == null) {
            throw new IllegalArgumentException("Date and Patterns must not be null");
        }

        final TimeZone tz = TimeZone.getDefault();
        final Locale lcl = ObjectUtil.defaultIfNull(locale, Locale.getDefault());
        final ParsePosition pos = new ParsePosition(0);
        final Calendar calendar = Calendar.getInstance(tz, lcl);
        calendar.setLenient(lenient);

        for (final String parsePattern : parsePatterns) {
            if (GlobalCustomFormat.isCustomFormat(parsePattern)) {
                final Date parse = GlobalCustomFormat.parse(str, parsePattern);
                if (null == parse) {
                    continue;
                }
                calendar.setTime(parse);
                return calendar;
            }

            final FastDateParser fdp = new FastDateParser(parsePattern, tz, lcl);
            calendar.clear();
            try {
                if (fdp.parse(str, pos, calendar) && pos.getIndex() == str.length()) {
                    return calendar;
                }
            } catch (final IllegalArgumentException ignore) {
                // leniency is preventing calendar from being set
            }
            pos.setIndex(0);
        }

        throw new DateException("Unable to parse the date: {}", str);
    }

    /**
     * 使用指定{@link DateParser}解析字符串为{@link Calendar}
     *
     * @param str     日期字符串
     * @param lenient 是否宽容模式
     * @param parser  {@link DateParser}
     * @return 解析后的 {@link Calendar}，解析失败返回{@code null}
     * @since 5.7.14
     */
    public static Calendar parse(CharSequence str, boolean lenient, DateParser parser) {
        final Calendar calendar = Calendar.getInstance(parser.getTimeZone(), parser.getLocale());
        calendar.clear();
        calendar.setLenient(lenient);

        return parser.parse(StringUtil.str(str), new ParsePosition(0), calendar) ? calendar : null;
    }
}
