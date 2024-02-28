package tpvinh.config.util;

import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.reflection.MetaObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MybatisUtil {

    protected static Logger logger = LoggerFactory.getLogger(MybatisUtil.class);

    /**
     * parameter null ex. <if test="@tpvinh.common.MybatisUtils@isEmpty(parameter)">
     */
    @SuppressWarnings("rawtypes")
    public static boolean isEmpty(Object obj) throws IllegalArgumentException {
        if (obj == null) return true;
        if (obj instanceof String && ((String) obj).length() == 0) return true;
        else if (obj instanceof Collection && ((Collection) obj).isEmpty()) return true;
        else if (obj.getClass().isArray() && Array.getLength(obj) == 0) return true;
        else if (obj instanceof Map && ((Map) obj).isEmpty()) return true;
        else return false;
    }

    /**
     * parameter not null ex. <if test="@tpvinh.common.MybatisUtils@isNotEmpty(parameter)">
     */
    public boolean isNotEmpty(Object obj) {
        return !isEmpty(obj);
    }

    /**
     * parameter is collection and not null ex. <if test="@tpvinh.common.MybatisUtils@isExistCollection(parameter)">
     */
    @SuppressWarnings("rawtypes")
    public static boolean isExistCollection(Object obj) {
        if (obj instanceof Collection && !((Collection) obj).isEmpty()) return true;
        return false;
    }

    /**
     * parameter is collection and not null ex. <if test="@tpvinh.common.MybatisUtils@isValidDate(...parameter)">
     */
    public static boolean isValidDate(String format, String value) {
        if (StringUtils.isEmpty(format) || StringUtils.isEmpty(value))
            return false;
        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            date = sdf.parse(value);
            if (!value.equals(sdf.format(date))) {
                date = null;
            }
        } catch (ParseException ex) {
            logger.error("Excecption : {}", ExceptionUtils.getStackTrace(ex));
        }
        return date != null;
    }

    /**
     * parameter is collection and not null ex. <if test="@tpvinh.common.MybatisUtils@isInteger(parameter)">
     */
    public static boolean isInteger(Object obj) {
        if (obj instanceof Integer) {
            return true;
        } else {
            String string = obj == null ? "" : obj.toString();
            try {
                Integer.parseInt(string);
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }

    /**
     * create mybatis query
     * @param mappedStatement
     * @param parameterObject
     * @return
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws Exception
     */
    public static String makeQuery(MappedStatement mappedStatement, Object parameterObject) throws ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
        BoundSql boundSql = mappedStatement.getBoundSql(parameterObject);
        List<ParameterMapping> parameterMappingList = boundSql.getParameterMappings();

        StringBuffer stringBuffer = new StringBuffer();
        Matcher matcher = Pattern.compile("\\?").matcher(boundSql.getSql());
        int i = 0;

        if (parameterObject != null) {
            if (parameterObject instanceof String || parameterObject instanceof Integer || parameterObject instanceof Long) {
                while (matcher.find()) {
                    matcher.appendReplacement(stringBuffer, formatParameter(parameterObject));
                }
            } else if (parameterObject instanceof Map) {
                Map<?, ?> parameterMap = (Map<?, ?>) parameterObject;
                String property = "";
                String[] array = null;
                Class<?> parameterClass = null;
                Object obj = null;
                while (matcher.find()) {
                    property = parameterMappingList.get(i).getProperty();
                    if (property.contains(".")) {
                        array = property.split("\\.");
                        try {
                            parameterClass = Class.forName(parameterMap.get(array[0]).getClass().getName().toString());
                            obj = parameterClass.getMethod("get" + array[1].substring(0, 1).toUpperCase() + array[1].substring(1)).invoke(parameterMap.get(array[0]), new Object[]{});
                            matcher.appendReplacement(stringBuffer, formatParameter(obj));
                        } catch (Exception e) {
                            System.out.println("Cannot set value to query logger at property : "+property);
                        }
                    } else {
                        Object value = null;
                        try {
                            if (parameterMap.containsKey(property)) {
                                value = parameterMap.get(property);
                            } else if (boundSql.hasAdditionalParameter(property)) {
                                value = boundSql.getAdditionalParameter(property);
                            } else {
                                MetaObject metaObject = mappedStatement.getConfiguration().newMetaObject(parameterObject);
                                if (metaObject != null) value = metaObject.getValue(property);
                            }
                        } catch (Exception e) {
                            value = property;
                        }
                        matcher.appendReplacement(stringBuffer, formatParameter(value));
                    }
                    i++;
                }
            } else {
                Class<?> parameterClass = Class.forName(parameterObject.getClass().getName().toString());
                Object obj = null;
                while (matcher.find()) {
                    obj = parameterClass.getMethod("get" + parameterMappingList.get(i).getProperty().substring(0, 1).toUpperCase() + parameterMappingList.get(i).getProperty().substring(1)).invoke(parameterObject, new Object[]{});
                    matcher.appendReplacement(stringBuffer, formatParameter(obj));
                    i++;
                }
            }
        }

        matcher.appendTail(stringBuffer);

        StringTokenizer lineStripper = new StringTokenizer(stringBuffer.toString(), "\n");
        StringBuilder builder = new StringBuilder();
        String line = null;

        while (lineStripper.hasMoreTokens()) {
            line = lineStripper.nextToken();
            if (!line.trim().equals("")) builder.append(line).append("\n");
        }
        return builder.toString();
    }

    public static String formatParameter(final Object value) {
        if (value == null) return "NULL";
        if (value instanceof Date)
            return new SimpleDateFormat("'TO_DATE('''yyyyMMddHHmmss'','''YYYYMMDDHH24MISS''')").format((Date) value);
        if (value instanceof String) return "'" + Matcher.quoteReplacement((String) value) + "'";
        return Matcher.quoteReplacement(value.toString());
    }
}
