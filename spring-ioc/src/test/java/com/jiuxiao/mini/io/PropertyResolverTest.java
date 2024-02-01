package com.jiuxiao.mini.io;

import com.jiuxiao.mini.util.YamlUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.*;
import java.util.Map;
import java.util.Properties;


public class PropertyResolverTest {

    Properties properties;

    PropertyResolver propertyResolver;

    @Before
    public void setUp() {
        String contextPath = "./application.yaml";
        properties = new Properties();
        Map<String, Object> plainMap = YamlUtil.loadYamlAsPlainMap(contextPath);
        for (String key : plainMap.keySet()) {
            properties.put(key, plainMap.get(key));
        }
        propertyResolver = new PropertyResolver(properties);
    }

    @Test
    public void testContainsProperty() throws Exception {
        Assert.assertFalse(propertyResolver.containsProperty("app.name"));
        Assert.assertTrue(propertyResolver.containsProperty("convert.int"));
    }

    @Test
    public void testGetProperty() throws Exception {
        String intValue = propertyResolver.getProperty("convert.int");
        String floatValue = propertyResolver.getProperty("${convert.float}");
        Assert.assertEquals(intValue, "789456");
        Assert.assertEquals(floatValue, "0.458");
    }

    @Test
    public void testGetRequiredProperty() throws Exception {
        String property = propertyResolver.getRequiredProperty("${convert.byte}");
        Assert.assertEquals(Byte.valueOf(property), new Byte("-121"));
        Assert.assertEquals(Byte.parseByte(property), (byte) (-121));
    }

    @Test
    public void testGetRequiredProperty2() throws Exception {
        String str = propertyResolver.getRequiredProperty("app.title", String.class);
        Byte aByte = propertyResolver.getRequiredProperty("convert.byte", Byte.class);
        Short aShort = propertyResolver.getRequiredProperty("convert.short", Short.class);
        Integer aInteger = propertyResolver.getRequiredProperty("convert.int", Integer.class);
        Long aLong = propertyResolver.getRequiredProperty("convert.long", Long.class);
        Float aFloat = propertyResolver.getRequiredProperty("convert.float", Float.class);
        Double aDouble = propertyResolver.getRequiredProperty("convert.double", Double.class);
        Character character = propertyResolver.getRequiredProperty("convert.char", Character.class);
        Boolean aBoolean = propertyResolver.getRequiredProperty("convert.boolean", Boolean.class);
        LocalDate localDate = propertyResolver.getRequiredProperty("convert.localDate", LocalDate.class);
        LocalTime localTime = propertyResolver.getRequiredProperty("convert.localTime", LocalTime.class);
        LocalDateTime localDateTime = propertyResolver.getRequiredProperty("convert.localDateTime", LocalDateTime.class);
        ZonedDateTime zonedDateTime = propertyResolver.getRequiredProperty("convert.zonedDateTime", ZonedDateTime.class);
        Duration duration = propertyResolver.getRequiredProperty("convert.duration", Duration.class);
        ZoneId zoneId = propertyResolver.getRequiredProperty("convert.zoneId", ZoneId.class);

        Assert.assertEquals(str, "mini");
        Assert.assertEquals(aByte, new Byte("-121"));
        Assert.assertEquals(aShort, new Short("485"));
        Assert.assertEquals(aInteger, new Integer("789456"));
        Assert.assertEquals(aLong, new Long("7800000000"));
        Assert.assertEquals(aFloat, new Float("0.458"));
        Assert.assertEquals(aDouble, new Double("-78.5421"));
        Assert.assertEquals(character, new Character('c'));
        Assert.assertEquals(aBoolean, Boolean.FALSE);
        Assert.assertEquals(localDate, LocalDate.parse("2024-01-22"));
        Assert.assertEquals(localTime, LocalTime.parse("23:08:07"));
        Assert.assertEquals(localDateTime, LocalDateTime.parse("2024-01-22T23:08:07"));
        Assert.assertEquals(zonedDateTime, ZonedDateTime.parse("2024-01-22T23:08:07+08:00[Asia/Shanghai]"));
        Assert.assertEquals(duration, Duration.parse("P2DT3H4M"));
        Assert.assertEquals(zoneId, ZoneId.of("Asia/Shanghai"));
    }
}