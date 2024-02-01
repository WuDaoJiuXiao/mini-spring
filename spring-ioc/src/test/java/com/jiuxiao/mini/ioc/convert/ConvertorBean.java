package com.jiuxiao.mini.ioc.convert;

import com.jiuxiao.mini.annotation.Component;
import com.jiuxiao.mini.annotation.Value;

import java.time.*;

/**
 * @Author 悟道九霄
 * @Date 2024/2/1 9:51
 * @Description
 */
@Component
public class ConvertorBean {

    @Value("${convert.byte}")
    public byte convertByte;

    @Value("${convert.byte}")
    public Byte convertByteBoxed;

    @Value("${convert.short}")
    public short convertShort;

    @Value("${convert.short}")
    public Short convertShortBoxed;

    @Value("${convert.int}")
    public int convertInt;

    @Value("${convert.int}")
    public Integer convertIntBoxed;

    @Value("${convert.long}")
    public long convertLong;

    @Value("${convert.long}")
    public Long convertLongBoxed;

    @Value("${convert.float}")
    public float convertFloat;

    @Value("${convert.float}")
    public Float convertFloatBoxed;

    @Value("${convert.double}")
    public double convertDouble;

    @Value("${convert.double}")
    public Double convertDoubleBoxed;

    @Value("${convert.boolean}")
    public boolean convertBoolean;

    @Value("${convert.boolean}")
    public Boolean convertBooleanBoxed;

    @Value("${convert.char}")
    public char convertChar;

    @Value("${convert.char}")
    public Character convertCharBoxed;

    @Value("${convert.string}")
    public String convertString;

    @Value("${convert.localDate}")
    public LocalDate convertLocalDate;

    @Value("${convert.localTime}")
    public LocalTime convertLocalTime;

    @Value("${convert.localDateTime}")
    public LocalDateTime convertLocalDateTime;

    @Value("${convert.zonedDateTime}")
    public ZonedDateTime convertZonedDateTime;

    @Value("${convert.duration}")
    public Duration convertDuration;

    @Value("${convert.zoneId}")
    public ZoneId convertZoneId;
}
