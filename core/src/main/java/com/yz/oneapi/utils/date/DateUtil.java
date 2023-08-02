package com.yz.oneapi.utils.date;

import com.yz.oneapi.utils.CharPool;
import com.yz.oneapi.utils.NumberUtil;
import com.yz.oneapi.utils.RegexUtil;
import com.yz.oneapi.utils.StringUtil;
import com.yz.oneapi.utils.date.format.BetweenFormatter;
import com.yz.oneapi.utils.date.format.DateParser;
import com.yz.oneapi.utils.date.format.DatePattern;
import com.yz.oneapi.utils.date.format.DatePrinter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/***
 * @author zhangpeng_jpa
 * @date 2022/5/16 18:21
 * @describe
 */
public class DateUtil extends CalendarUtil {

    /**
     * java.util.Date EEE MMM zzz 缩写数组
     */
    private final static String[] wtb = { //
            "sun", "mon", "tue", "wed", "thu", "fri", "sat", // 星期
            "jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec", // 月份
            "gmt", "ut", "utc", "est", "edt", "cst", "cdt", "mst", "mdt", "pst", "pdt"// 时间标准
    };

    /**
     * 当前时间，转换为{@link }对象
     *
     * @return 当前时间
     */
    public static DateTime date() {
        return new DateTime();
    }

    /**
     * {@link Date}类型时间转为{{@link }<br>
     * 如果date本身为DateTime对象，则返回强转后的对象，否则新建一个DateTime对象
     *
     * @param date Long类型Date（Unix时间戳）
     * @return 时间对象
     * @since 3.0.7
     */
    public static DateTime date(Date date) {
        if (date instanceof DateTime) {
            return (DateTime) date;
        }
        return dateNew(date);
    }

    /**
     * 根据已有{@link Date} 产生新的{{@link }对象
     *
     * @param date Date对象
     * @return {{@link }对象
     * @since 4.3.1
     */
    public static DateTime dateNew(Date date) {
        return new DateTime(date);
    }

    /**
     * Long类型时间转为{{@link }<br>
     * 只支持毫秒级别时间戳，如果需要秒级别时间戳，请自行×1000
     *
     * @param date Long类型Date（Unix时间戳）
     * @return 时间对象
     */
    public static DateTime date(long date) {
        return new DateTime(date);
    }

    /**
     * {@link Calendar}类型时间转为{{@link }<br>
     * 始终根据已有{@link Calendar} 产生新的{{@link }对象
     *
     * @param calendar {@link Calendar}
     * @return 时间对象
     */
    public static DateTime date(Calendar calendar) {
        return new DateTime(calendar);
    }

    /**
     * {@link TemporalAccessor}类型时间转为{{@link }<br>
     * 始终根据已有{@link TemporalAccessor} 产生新的{{@link }对象
     *
     * @param temporalAccessor {@link TemporalAccessor},常用子类： {@link LocalDateTime}、 LocalDate
     * @return 时间对象
     * @since 5.0.0
     */
    public static DateTime date(TemporalAccessor temporalAccessor) {
        return new DateTime(temporalAccessor);
    }

    /**
     * 当前时间的时间戳
     *
     * @return 时间
     */
    public static long current() {
        return System.currentTimeMillis();
    }

    /**
     * 当前时间的时间戳（秒）
     *
     * @return 当前时间秒数
     * @since 4.0.0
     */
    public static long currentSeconds() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * 当前时间，格式 yyyy-MM-dd HH:mm:ss
     *
     * @return 当前时间的标准形式字符串
     */
    public static String now() {
        return formatDateTime(new DateTime());
    }

    /**
     * 当前日期，格式 yyyy-MM-dd
     *
     * @return 当前日期的标准形式字符串
     */
    public static String today() {
        return formatDate(new DateTime());
    }

    // -------------------------------------------------------------- Part of Date start

    /**
     * 获得年的部分
     *
     * @param date 日期
     * @return 年的部分
     */
    public static int year(Date date) {
        return DateTime.of(date).year();
    }

    /**
     * 获得月份，从0开始计数
     *
     * @param date 日期
     * @return 月份，从0开始计数
     */
    public static int month(Date date) {
        return DateTime.of(date).month();
    }

    /**
     * 获得指定日期的小时数部分<br>
     *
     * @param date          日期
     * @param is24HourClock 是否24小时制
     * @return 小时数
     */
    public static int hour(Date date, boolean is24HourClock) {
        return DateTime.of(date).hour(is24HourClock);
    }

    /**
     * 获得指定日期的分钟数部分<br>
     * 例如：10:04:15.250 =》 4
     *
     * @param date 日期
     * @return 分钟数
     */
    public static int minute(Date date) {
        return DateTime.of(date).minute();
    }

    /**
     * 获得指定日期的秒数部分<br>
     *
     * @param date 日期
     * @return 秒数
     */
    public static int second(Date date) {
        return DateTime.of(date).second();
    }

    /**
     * 获得指定日期的毫秒数部分<br>
     *
     * @param date 日期
     * @return 毫秒数
     */
    public static int millisecond(Date date) {
        return DateTime.of(date).millisecond();
    }



    /**
     * 根据特定格式格式化日期
     *
     * @param localDateTime 被格式化的日期
     * @param format        日期格式，常用格式见： {@link DatePattern}
     * @return 格式化后的字符串
     */
    public static String format(LocalDateTime localDateTime, String format) {
        return LocalDateTimeUtil.format(localDateTime, format);
    }

    /**
     * 根据特定格式格式化日期
     *
     * @param date   被格式化的日期
     * @param format 日期格式，常用格式见： {@link DatePattern} {@link DatePattern#NORM_DATETIME_PATTERN}
     * @return 格式化后的字符串
     */
    public static String format(Date date, String format) {
        if (null == date || StringUtil.isBlank(format)) {
            return null;
        }

        // 检查自定义格式
        if (GlobalCustomFormat.isCustomFormat(format)) {
            return GlobalCustomFormat.format(date, format);
        }

        TimeZone timeZone = null;
        if (date instanceof DateTime) {
            timeZone = ((DateTime) date).getTimeZone();
        }
        return format(date, newSimpleFormat(format, null, timeZone));
    }

    /**
     * 根据特定格式格式化日期
     *
     * @param date   被格式化的日期
     * @return 格式化后的字符串
     */
    public static String format(Date date, DatePrinter format) {
        if (null == format || null == date) {
            return null;
        }
        return format.format(date);
    }

    /**
     * 根据特定格式格式化日期
     *
     * @param date   被格式化的日期
     * @param format {@link SimpleDateFormat}
     * @return 格式化后的字符串
     */
    public static String format(Date date, DateFormat format) {
        if (null == format || null == date) {
            return null;
        }
        return format.format(date);
    }

    /**
     * 根据特定格式格式化日期
     *
     * @param date   被格式化的日期
     * @param format {@link SimpleDateFormat} {@link DatePattern#NORM_DATETIME_FORMATTER}
     * @return 格式化后的字符串
     * @since 5.0.0
     */
    public static String format(Date date, DateTimeFormatter format) {
        if (null == format || null == date) {
            return null;
        }
        // java.time.temporal.UnsupportedTemporalTypeException: Unsupported field: YearOfEra
        // 出现以上报错时，表示Instant时间戳没有时区信息，赋予默认时区
        return TemporalAccessorUtil.format(date.toInstant(), format);
    }

    /**
     * 格式化日期时间<br>
     * 格式 yyyy-MM-dd HH:mm:ss
     *
     * @param date 被格式化的日期
     * @return 格式化后的日期
     */
    public static String formatDateTime(Date date) {
        if (null == date) {
            return null;
        }
        return DatePattern.NORM_DATETIME_FORMAT.format(date);
    }

    /**
     * 格式化日期部分（不包括时间）<br>
     * 格式 yyyy-MM-dd
     *
     * @param date 被格式化的日期
     * @return 格式化后的字符串
     */
    public static String formatDate(Date date) {
        if (null == date) {
            return null;
        }
        return DatePattern.NORM_DATE_FORMAT.format(date);
    }

    // ------------------------------------ Format end ----------------------------------------------

    // ------------------------------------ Parse start ----------------------------------------------

    /**
     * 构建DateTime对象
     *
     * @param dateStr    Date字符串
     * @param dateFormat 格式化器 {@link SimpleDateFormat}
     * @return DateTime对象
     */
    public static DateTime parse(CharSequence dateStr, DateFormat dateFormat) {
        return new DateTime(dateStr, dateFormat);
    }

    /**
     * 构建DateTime对象
     *
     * @param dateStr Date字符串
     * @return DateTime对象
     */
    public static DateTime parse(CharSequence dateStr, DateParser parser) {
        return new DateTime(dateStr, parser);
    }

    /**
     * 构建DateTime对象
     *
     * @param dateStr Date字符串
     * @param lenient 是否宽容模式
     * @return DateTime对象
     * @since 5.7.14
     */
    public static DateTime parse(CharSequence dateStr, DateParser parser, boolean lenient) {
        return new DateTime(dateStr, parser, lenient);
    }

    /**
     * 构建DateTime对象
     *
     * @param dateStr   Date字符串
     * @param formatter 格式化器,{{@link Formatter}
     * @return DateTime对象
     * @since 5.0.0
     */
    public static DateTime parse(CharSequence dateStr, DateTimeFormatter formatter) {
        return new DateTime(dateStr, formatter);
    }

    /**
     * 将特定格式的日期转换为Date对象
     *
     * @param dateStr 特定格式的日期
     * @param format  格式，例如yyyy-MM-dd
     * @return 日期对象
     */
    public static DateTime parse(CharSequence dateStr, String format) {
        return new DateTime(dateStr, format);
    }

    /**
     * 将特定格式的日期转换为Date对象
     *
     * @param dateStr 特定格式的日期
     * @param format  格式，例如yyyy-MM-dd
     * @param locale  区域信息
     * @return 日期对象
     * @since 4.5.18
     */
    public static DateTime parse(CharSequence dateStr, String format, Locale locale) {
        if (GlobalCustomFormat.isCustomFormat(format)) {
            // 自定义格式化器忽略Locale
            return new DateTime(GlobalCustomFormat.parse(dateStr, format));
        }
        return new DateTime(dateStr, DateUtil.newSimpleFormat(format, locale, null));
    }

    /**
     * 通过给定的日期格式解析日期时间字符串。<br>
     * 传入的日期格式会逐个尝试，直到解析成功，返回{{@link }对象，否则抛出{@link DateException}异常。
     *
     * @param str           日期时间字符串，非空
     * @param parsePatterns 需要尝试的日期时间格式数组，非空, 见SimpleDateFormat
     * @return 解析后的Date
     * @throws IllegalArgumentException if the date string or pattern array is null
     * @throws DateException            if none of the date patterns were suitable
     * @since 5.3.11
     */
    public static DateTime parse(String str, String... parsePatterns) throws DateException {
        return new DateTime(CalendarUtil.parseByPatterns(str, parsePatterns));
    }



    /**
     * 解析时间，格式HH:mm 或 HH:mm:ss，日期默认为今天
     *
     * @param timeString 标准形式的日期字符串
     * @return 日期对象
     * @since 3.1.1
     */
    public static DateTime parseTimeToday(CharSequence timeString) {
        timeString = StringUtil.format("{} {}", today(), timeString);
        if (1 == StringUtil.count(timeString, ':')) {
            // 时间格式为 HH:mm
            return parse(timeString, DatePattern.NORM_DATETIME_MINUTE_PATTERN);
        } else {
            // 时间格式为 HH:mm:ss
            return parse(timeString, DatePattern.NORM_DATETIME_FORMAT);
        }
    }

    /**
     * 解析UTC时间，格式：<br>
     * <ol>
     * <li>yyyy-MM-dd'T'HH:mm:ss'Z'</li>
     * <li>yyyy-MM-dd'T'HH:mm:ss.SSS'Z'</li>
     * <li>yyyy-MM-dd'T'HH:mm:ssZ</li>
     * <li>yyyy-MM-dd'T'HH:mm:ss.SSSZ</li>
     * <li>yyyy-MM-dd'T'HH:mm:ss+0800</li>
     * <li>yyyy-MM-dd'T'HH:mm:ss+08:00</li>
     * </ol>
     *
     * @param utcString UTC时间
     * @return 日期对象
     * @since 4.1.14
     */
    public static DateTime parseUTC(String utcString) {
        if (utcString == null) {
            return null;
        }
        int length = utcString.length();
        if (StringUtil.contains(utcString, 'Z')) {
            if (length == DatePattern.UTC_PATTERN.length() - 4) {
                // 格式类似：2018-09-13T05:34:31Z，-4表示减去4个单引号的长度
                return parse(utcString, DatePattern.UTC_FORMAT);
            }

            final int patternLength = DatePattern.UTC_MS_PATTERN.length();
            // 格式类似：2018-09-13T05:34:31.999Z，-4表示减去4个单引号的长度
            // -4 ~ -6范围表示匹配毫秒1~3位的情况
            if (length <= patternLength - 4 && length >= patternLength - 6) {
                return parse(utcString, DatePattern.UTC_MS_FORMAT);
            }
        } else if (StringUtil.contains(utcString, '+')) {
            // 去除类似2019-06-01T19:45:43 +08:00加号前的空格
            utcString = utcString.replace(" +", "+");
            final String zoneOffset = StringUtil.subAfter(utcString, '+', true);
            if (StringUtil.isBlank(zoneOffset)) {
                throw new DateException("Invalid format: [{}]", utcString);
            }
            if (false == StringUtil.contains(zoneOffset, ':')) {
                // +0800转换为+08:00
                final String pre = StringUtil.subBefore(utcString, '+', true);
                utcString = pre + "+" + zoneOffset.substring(0, 2) + ":" + "00";
            }

            if (StringUtil.contains(utcString, CharPool.DOT)) {
                // 带毫秒，格式类似：2018-09-13T05:34:31.999+08:00
                return parse(utcString, DatePattern.UTC_MS_WITH_XXX_OFFSET_FORMAT);
            } else {
                // 格式类似：2018-09-13T05:34:31+08:00
                return parse(utcString, DatePattern.UTC_WITH_XXX_OFFSET_FORMAT);
            }
        } else {
            if (length == DatePattern.UTC_SIMPLE_PATTERN.length() - 2) {
                // 格式类似：2018-09-13T05:34:31
                return parse(utcString, DatePattern.UTC_SIMPLE_FORMAT);
            } else if (StringUtil.contains(utcString, CharPool.DOT)) {
                // 可能为：  2021-03-17T06:31:33.99
                return parse(utcString, DatePattern.UTC_SIMPLE_MS_FORMAT);
            }
        }
        // 没有更多匹配的时间格式
        throw new DateException("No format fit for date String [{}] !", utcString);
    }

    /**
     * 解析CST时间，格式：<br>
     * <ol>
     * <li>EEE MMM dd HH:mm:ss z yyyy（例如：Wed Aug 01 00:00:00 CST 2012）</li>
     * </ol>
     *
     * @param cstString UTC时间
     * @return 日期对象
     * @since 4.6.9
     */
    public static DateTime parseCST(CharSequence cstString) {
        if (cstString == null) {
            return null;
        }

        return parse(cstString, DatePattern.JDK_DATETIME_FORMAT);
    }

    /**
     * 将日期字符串转换为{{@link }对象，格式：<br>
     * <ol>
     * <li>yyyy-MM-dd HH:mm:ss</li>
     * <li>yyyy/MM/dd HH:mm:ss</li>
     * <li>yyyy.MM.dd HH:mm:ss</li>
     * <li>yyyy年MM月dd日 HH时mm分ss秒</li>
     * <li>yyyy-MM-dd</li>
     * <li>yyyy/MM/dd</li>
     * <li>yyyy.MM.dd</li>
     * <li>HH:mm:ss</li>
     * <li>HH时mm分ss秒</li>
     * <li>yyyy-MM-dd HH:mm</li>
     * <li>yyyy-MM-dd HH:mm:ss.SSS</li>
     * <li>yyyy-MM-dd HH:mm:ss.SSSSSS</li>
     * <li>yyyyMMddHHmmss</li>
     * <li>yyyyMMddHHmmssSSS</li>
     * <li>yyyyMMdd</li>
     * <li>EEE, dd MMM yyyy HH:mm:ss z</li>
     * <li>EEE MMM dd HH:mm:ss zzz yyyy</li>
     * <li>yyyy-MM-dd'T'HH:mm:ss'Z'</li>
     * <li>yyyy-MM-dd'T'HH:mm:ss.SSS'Z'</li>
     * <li>yyyy-MM-dd'T'HH:mm:ssZ</li>
     * <li>yyyy-MM-dd'T'HH:mm:ss.SSSZ</li>
     * </ol>
     *
     * @param dateCharSequence 日期字符串
     * @return 日期
     */
    public static DateTime parse(CharSequence dateCharSequence) {
        if (StringUtil.isBlank(dateCharSequence)) {
            return null;
        }
        String dateStr = dateCharSequence.toString();
        // 去掉两边空格并去掉中文日期中的“日”和“秒”，以规范长度
        dateStr = StringUtil.removeAll(dateStr.trim(), '日', '秒');
        int length = dateStr.length();

        if (NumberUtil.isNumber(dateStr)) {
            // 纯数字形式
            if (length == DatePattern.PURE_DATETIME_PATTERN.length()) {
                return parse(dateStr, DatePattern.PURE_DATETIME_FORMAT);
            } else if (length == DatePattern.PURE_DATETIME_MS_PATTERN.length()) {
                return parse(dateStr, DatePattern.PURE_DATETIME_MS_FORMAT);
            } else if (length == DatePattern.PURE_DATE_PATTERN.length()) {
                return parse(dateStr, DatePattern.PURE_DATE_FORMAT);
            } else if (length == DatePattern.PURE_TIME_PATTERN.length()) {
                return parse(dateStr, DatePattern.PURE_TIME_FORMAT);
            }
        } else if (RegexUtil.isMatch(Pattern.compile("\\d{1,2}:\\d{1,2}(:\\d{1,2})?"), dateStr)) {
            // HH:mm:ss 或者 HH:mm 时间格式匹配单独解析
            return parseTimeToday(dateStr);
        } else if (StringUtil.containsAnyIgnoreCase(dateStr, wtb)) {
            // JDK的Date对象toString默认格式，类似于：
            // Tue Jun 4 16:25:15 +0800 2019
            // Thu May 16 17:57:18 GMT+08:00 2019
            // Wed Aug 01 00:00:00 CST 2012
            return parseCST(dateStr);
        } else if (StringUtil.contains(dateStr, 'T')) {
            // UTC时间
            return parseUTC(dateStr);
        }

        //标准日期格式（包括单个数字的日期时间）
        dateStr = normalize(dateStr);
        if (RegexUtil.isMatch(DatePattern.REGEX_NORM, dateStr)) {
            final int colonCount = StringUtil.count(dateStr, CharPool.COLON);
            switch (colonCount) {
                case 0:
                    // yyyy-MM-dd
                    return parse(dateStr, DatePattern.NORM_DATE_FORMAT);
                case 1:
                    // yyyy-MM-dd HH:mm
                    return parse(dateStr, DatePattern.NORM_DATETIME_MINUTE_FORMAT);
                case 2:
                    final int indexOfDot = StringUtil.indexOf(dateStr, CharPool.DOT);
                    if (indexOfDot > 0) {
                        final int length1 = dateStr.length();
                        // yyyy-MM-dd HH:mm:ss.SSS 或者 yyyy-MM-dd HH:mm:ss.SSSSSS
                        if (length1 - indexOfDot > 4) {
                            // 类似yyyy-MM-dd HH:mm:ss.SSSSSS，采取截断操作
                            dateStr = StringUtil.subPre(dateStr, indexOfDot + 4);
                        }
                        return parse(dateStr, DatePattern.NORM_DATETIME_MS_FORMAT);
                    }
                    // yyyy-MM-dd HH:mm:ss
                    return parse(dateStr, DatePattern.NORM_DATETIME_FORMAT);
            }
        }

        // 没有更多匹配的时间格式
        throw new DateException("No format fit for date String [{}] !", dateStr);
    }

    // ------------------------------------ Parse end ----------------------------------------------

    // ------------------------------------ Offset start ----------------------------------------------





    /**
     * 下个月
     *
     * @return 下个月
     * @since 3.0.1
     */
    public static DateTime nextMonth() {
        return offsetMonth(new DateTime(), 1);
    }

    /**
     * 偏移天
     *
     * @param date   日期
     * @param offset 偏移天数，正数向未来偏移，负数向历史偏移
     * @return 偏移后的日期
     */
    public static DateTime offsetDay(Date date, int offset) {
        return offset(date, DateField.DAY_OF_YEAR, offset);
    }


    /**
     * 偏移月
     *
     * @param date   日期
     * @param offset 偏移月数，正数向未来偏移，负数向历史偏移
     * @return 偏移后的日期
     */
    public static DateTime offsetMonth(Date date, int offset) {
        return offset(date, DateField.MONTH, offset);
    }

    /**
     * 获取指定日期偏移指定时间后的时间，生成的偏移日期不影响原日期
     *
     * @param date      基准日期
     * @param dateField 偏移的粒度大小（小时、天、月等）{@link DateField}
     * @param offset    偏移量，正数为向后偏移，负数为向前偏移
     * @return 偏移后的日期
     */
    public static DateTime offset(Date date, DateField dateField, int offset) {
        return dateNew(date).offset(dateField, offset);
    }

    // ------------------------------------ Offset end ----------------------------------------------

    /**
     * 判断两个日期相差的时长，只保留绝对值
     *
     * @param beginDate 起始日期
     * @param endDate   结束日期
     * @return 日期差
     */
    public static long between(Date beginDate, Date endDate, DateUnit unit) {
        return between(beginDate, endDate, unit, true);
    }

    /**
     * 判断两个日期相差的时长
     *
     * @param beginDate 起始日期
     * @param endDate   结束日期
     * @param isAbs     日期间隔是否只保留绝对值正数
     * @return 日期差
     * @since 3.3.1
     */
    public static long between(Date beginDate, Date endDate, DateUnit unit, boolean isAbs) {
        return new DateBetween(beginDate, endDate, isAbs).between(unit);
    }

    /**
     * 判断两个日期相差的毫秒数
     *
     * @param beginDate 起始日期
     * @param endDate   结束日期
     * @return 日期差
     * @since 3.0.1
     */
    public static long betweenMs(Date beginDate, Date endDate) {
        return new DateBetween(beginDate, endDate).between(DateUnit.MS);
    }


    /**
     * 格式化日期间隔输出
     *
     * @param betweenMs 日期间隔
     * @param level     级别，按照天、小时、分、秒、毫秒分为5个等级
     * @return XX天XX小时XX分XX秒XX毫秒
     */
    public static String formatBetween(long betweenMs, BetweenFormatter.Level level) {
        return new BetweenFormatter(betweenMs, level).format();
    }

    /**
     * 格式化日期间隔输出，精确到毫秒
     *
     * @param betweenMs 日期间隔
     * @return XX天XX小时XX分XX秒XX毫秒
     * @since 3.0.1
     */
    public static String formatBetween(long betweenMs) {
        return new BetweenFormatter(betweenMs, BetweenFormatter.Level.MILLISECOND).format();
    }


    /**
     * 格式化成yyMMddHHmm后转换为int型
     *
     * @param date 日期
     * @return int
     * @deprecated 2022年后结果溢出，此方法废弃
     */
    @Deprecated
    public static int toIntSecond(Date date) {
        return Integer.parseInt(DateUtil.format(date, "yyMMddHHmm"));
    }

    /**
     * 计时器<br>
     * 计算某个过程花费的时间，精确到毫秒
     *
     * @return Timer
     */
    public static TimeInterval timer() {
        return new TimeInterval();

    }

    /**
     * 计时器<br>
     * 计算某个过程花费的时间，精确到毫秒
     *
     * @param isNano 是否使用纳秒计数，false则使用毫秒
     * @return Timer
     * @since 5.2.3
     */
    public static TimeInterval timer(boolean isNano) {
        return new TimeInterval(isNano);
    }


    /**
     * 判定给定开始时间经过某段时间后是否过期
     *
     * @param startDate  开始时间
     * @param dateField  时间单位
     * @param timeLength 实际经过时长
     * @param endDate    被比较的时间，即有效期的截止时间。如果经过时长后的时间晚于截止时间，就表示过期
     * @return 是否过期
     * @since 3.1.1
     * @deprecated 此方法存在一定的歧义，容易产生误导，废弃。
     */
    @Deprecated
    public static boolean isExpired(Date startDate, DateField dateField, int timeLength, Date endDate) {
        final Date offsetDate = offset(startDate, dateField, timeLength);
        return offsetDate.after(endDate);
    }

    /**
     * 判定在指定检查时间是否过期。
     *
     * <p>
     * 以商品为例，startDate即生产日期，endDate即保质期的截止日期，checkDate表示在何时检查是否过期（一般为当前时间）<br>
     * endDate和startDate的差值即为保质期（按照毫秒计），checkDate和startDate的差值即为实际经过的时长，实际时长大于保质期表示超时。
     * </p>
     *
     * @param startDate 开始时间
     * @param endDate   被比较的时间，即有效期的截止时间。如果经过时长后的时间晚于被检查的时间，就表示过期
     * @param checkDate 检查时间，可以是当前时间，既
     * @return 是否过期
     * @since 5.1.1
     * @deprecated 使用isIn方法
     */
    @Deprecated
    public static boolean isExpired(Date startDate, Date endDate, Date checkDate) {
        return betweenMs(startDate, checkDate) > betweenMs(startDate, endDate);
    }






    /**
     * 纳秒转秒，保留小数
     *
     * @param duration 时长
     * @return 秒
     * @since 4.6.6
     */
    public static double nanosToSeconds(long duration) {
        return duration / 1_000_000_000.0;
    }


    /**
     * Date对象转换为{@link Instant}对象
     *
     * @param temporalAccessor Date对象
     * @return {@link Instant}对象
     * @since 5.0.2
     */
    public static Instant toInstant(TemporalAccessor temporalAccessor) {
        return TemporalAccessorUtil.toInstant(temporalAccessor);
    }



    /**
     * 创建{@link SimpleDateFormat}，注意此对象非线程安全！<br>
     * 此对象默认为严格格式模式，即parse时如果格式不正确会报错。
     *
     * @param pattern 表达式
     * @return {@link SimpleDateFormat}
     * @since 5.5.5
     */
    public static SimpleDateFormat newSimpleFormat(String pattern) {
        return newSimpleFormat(pattern, null, null);
    }

    /**
     * 创建{@link SimpleDateFormat}，注意此对象非线程安全！<br>
     * 此对象默认为严格格式模式，即parse时如果格式不正确会报错。
     *
     * @param pattern  表达式
     * @param locale   {@link Locale}，{@code null}表示默认
     * @param timeZone {@link TimeZone}，{@code null}表示默认
     * @return {@link SimpleDateFormat}
     * @since 5.5.5
     */
    public static SimpleDateFormat newSimpleFormat(String pattern, Locale locale, TimeZone timeZone) {
        if (null == locale) {
            locale = Locale.getDefault(Locale.Category.FORMAT);
        }
        final SimpleDateFormat format = new SimpleDateFormat(pattern, locale);
        if (null != timeZone) {
            format.setTimeZone(timeZone);
        }
        format.setLenient(false);
        return format;
    }

    /**
     * 获取时长单位简写
     *
     * @param unit 单位
     * @return 单位简写名称
     * @since 5.7.16
     */
    public static String getShotName(TimeUnit unit) {
        switch (unit) {
            case NANOSECONDS:
                return "ns";
            case MICROSECONDS:
                return "μs";
            case MILLISECONDS:
                return "ms";
            case SECONDS:
                return "s";
            case MINUTES:
                return "min";
            case HOURS:
                return "h";
            default:
                return unit.name().toLowerCase();
        }
    }

    // ------------------------------------------------------------------------ Private method start

    /**
     * 标准化日期，默认处理以空格区分的日期时间格式，空格前为日期，空格后为时间：<br>
     * 将以下字符替换为"-"
     *
     * <pre>
     * "."
     * "/"
     * "年"
     * "月"
     * </pre>
     * <p>
     * 将以下字符去除
     *
     * <pre>
     * "日"
     * </pre>
     * <p>
     * 将以下字符替换为":"
     *
     * <pre>
     * "时"
     * "分"
     * "秒"
     * </pre>
     * <p>
     * 当末位是":"时去除之（不存在毫秒时）
     *
     * @param dateStr 日期时间字符串
     * @return 格式化后的日期字符串
     */
    private static String normalize(CharSequence dateStr) {
        if (StringUtil.isBlank(dateStr)) {
            return StringUtil.str(dateStr);
        }

        // 日期时间分开处理
        final List<String> dateAndTime = StringUtil.splitTrim(dateStr, ' ');
        final int size = dateAndTime.size();
        if (size < 1 || size > 2) {
            // 非可被标准处理的格式
            return StringUtil.str(dateStr);
        }

        final StringBuilder builder = new StringBuilder();

        // 日期部分（"\"、"/"、"."、"年"、"月"都替换为"-"）
        String datePart = dateAndTime.get(0).replaceAll("[/.年月]", "-");
        datePart = StringUtil.removeSuffix(datePart, "日");
        builder.append(datePart);

        // 时间部分
        if (size == 2) {
            builder.append(' ');
            String timePart = dateAndTime.get(1).replaceAll("[时分秒]", ":");
            timePart = StringUtil.removeSuffix(timePart, ":");
            //将ISO8601中的逗号替换为.
            timePart = timePart.replace(',', '.');
            builder.append(timePart);
        }

        return builder.toString();
    }
    // ------------------------------------------------------------------------ Private method end
}
