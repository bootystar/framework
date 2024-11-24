package io.github.bootystar.mybatisplus.util;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.reflect.GenericTypeUtils;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import io.github.bootystar.mybatisplus.logic.dynamic.DynamicEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * mybatis-plus解析工具类
 *
 * @author bootystar
 */
public abstract class ReflectHelper4MybatisPlus extends ReflectHelper {

    @Data
    @AllArgsConstructor
    public static class LambdaMethod {
        private String classPackage;
        private String classSimpleName;
        private boolean isGenericTypeClass;
        private String methodName;
        private boolean isStaticMethod;
        private boolean isConstructor;
        private String prefix;
        private String suffix;
    }

    /**
     * lambda方法信息
     *
     * @param methodReference lambda方法引用
     * @param parameterClass  参数类型
     * @return {@link LambdaMethod }
     * @author bootystar
     */
    public static LambdaMethod lambdaMethodInfo(SFunction<?, ?> methodReference, Class<?> parameterClass) {
        String methodName = "", className = "";
        try {
            Method lambdaMethod = methodReference.getClass().getDeclaredMethod("writeReplace");
            lambdaMethod.setAccessible(Boolean.TRUE);
            SerializedLambda serializedLambda = (SerializedLambda) lambdaMethod.invoke(methodReference);
            className = serializedLambda.getImplClass().replace("/", ".");
            methodName = serializedLambda.getImplMethodName();
            Class<?> clazz = Class.forName(className);
            String classSimpleName = clazz.getSimpleName();
            TypeVariable<? extends Class<?>>[] classTypeParameters = clazz.getTypeParameters();
            boolean isStaticMethod = false, isConstructor = false, isGenericClass = classTypeParameters.length > 0, isGenericMethod = false;
            try {
                Method returnMethod = clazz.getMethod(methodName, parameterClass, Modifier.STATIC);
                Class<?> returnType = returnMethod.getReturnType();
                if (!returnType.equals(clazz) || !Modifier.isPublic(returnMethod.getModifiers())) {
                    throw new NoSuchMethodException("no public method found which return instance of class itself");
                }
                isStaticMethod = Modifier.isStatic(returnMethod.getModifiers());
                TypeVariable<Method>[] methodTypeParameters = returnMethod.getTypeParameters();
                isGenericMethod = methodTypeParameters.length > 0;
            } catch (Exception e) {
                Constructor<?> constructor = clazz.getConstructor(parameterClass);
                isConstructor = true;
            }
            String prefix = "", suffix = ")";
            if (isStaticMethod) {
                prefix = String.format("%s.%s(", classSimpleName, methodName);
            } else if (isConstructor) {
                prefix = String.format("new %s%s(", classSimpleName, isGenericClass ? "<>" : "");
            } else {
                prefix = String.format("new %s%s.%s(", classSimpleName, isGenericMethod ? "<>" : "",methodName);
            }
            return new ReflectHelper4MybatisPlus.LambdaMethod(
                    clazz.getPackage().getName()
                    , clazz.getSimpleName()
                    , isGenericClass
                    , methodName
                    , isStaticMethod
                    , isConstructor
                    , prefix
                    , suffix
            );
        } catch (Exception e) {
            String msg = String.format("can't find constructor or method in class [%s] , method name [%s], parameter class [%s]", className, methodName, parameterClass.getName());
            throw new IllegalStateException(msg);
        }
    }

    /**
     * id字段属性名
     *
     * @param clazz 克拉兹
     * @return {@link String }
     * @author bootystar
     */
    public static String idFieldPropertyName(Class<?> clazz) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(clazz);
        if (tableInfo == null) {
            return null;
        }
        return tableInfo.getKeyProperty();
    }


    /**
     * 实体类与数据库字段转换映射
     *
     * @param clazz 克拉兹
     * @return {@link Map }<{@link String },{@link String }>
     * @author bootystar
     */
    public static Map<String, String> fieldConvertMap(Class<?> clazz) {
        TableInfo tableInfo = TableInfoHelper.getTableInfo(clazz);
        List<TableFieldInfo> fieldList = tableInfo.getFieldList();
        Map<String, String> result = new HashMap<>();
        for (TableFieldInfo fieldInfo : fieldList) {
            Field field = fieldInfo.getField();
            String fieldName = field.getName();
            String jdbcColumn = fieldInfo.getColumn();
            result.put(fieldName, String.format("a.`%s`", jdbcColumn));
        }
        TableFieldInfo logicDeleteFieldInfo = tableInfo.getLogicDeleteFieldInfo();
        if (logicDeleteFieldInfo != null) {
            String name = logicDeleteFieldInfo.getField().getName();
            result.remove(name);
        }

        Map<String, Field> fieldMap = fieldMap(clazz);
        for (Field field : fieldMap.values()) {
            String fieldName = field.getName();
            String jdbcColumn = fieldName;
            TableLogic tableLogic = field.getAnnotation(TableLogic.class);
            if (tableLogic != null) {
                continue;
            }
            TableId tableId = field.getAnnotation(TableId.class);
            if (tableId != null) {
                String value = tableId.value();
                if (!value.isEmpty()) {
                    jdbcColumn = value;
                    if (!value.contains(".")) {
                        jdbcColumn = String.format("a.`%s`", jdbcColumn);
                    }
                }
                result.putIfAbsent(fieldName, jdbcColumn);
                continue;
            }
            TableField tableField = field.getAnnotation(TableField.class);
            if (tableField != null) {
                boolean exist = tableField.exist();
                String value = tableField.value();
                if (!exist) {
                    if (value.isEmpty()) {
                        continue;
                    }
                    result.putIfAbsent(fieldName, value);
                    continue;
                }
                if (!value.isEmpty()) {
                    jdbcColumn = value;
                    if (!value.contains(".")) {
                        jdbcColumn = String.format("a.`%s`", jdbcColumn);
                    }
                }
                result.putIfAbsent(fieldName, jdbcColumn);
                continue;
            }
            result.putIfAbsent(fieldName, String.format("a.`%s`", jdbcColumn));
        }
        return result;
    }


    /**
     * 获取实体类字段映射
     * 当实体类实现{@link DynamicEntity }接口后,会额外添加映射字段
     *
     * @param entityClass 实体类
     * @return {@link Map }<{@link String }, {@link String }>
     * @author bootystar
     */
    @SneakyThrows
    public static Map<String, String> dynamicFieldsMap(Class<?> entityClass) {
        Map<String, String> map = fieldConvertMap(entityClass);
        if (DynamicEntity.class.isAssignableFrom(entityClass)) {
            DynamicEntity instance = (DynamicEntity) entityClass.getConstructor().newInstance();
            Map<String, String> extraMap = instance.extraMap();
            if (extraMap != null && !extraMap.isEmpty()) {
                for (Map.Entry<String, String> next : extraMap.entrySet()) {
                    String fieldName = next.getKey();
                    String jdbcColumn = next.getValue();
                    if (jdbcColumn == null || jdbcColumn.isEmpty()) {
                        continue;
                    }
                    if (!jdbcColumn.contains(".")) {
                        jdbcColumn = String.format("a.`%s`", jdbcColumn);
                    }
                    map.put(fieldName, jdbcColumn);
                }
            }
        }
        return map;
    }


    /**
     * 解析超类泛型参数
     *
     * @param clazz      指定类
     * @param superClass 超类
     * @return {@link Class }
     * @author bootystar
     */
    public static Class<?>[] resolveTypeArguments(Class<?> clazz, Class<?> superClass) {
        return GenericTypeUtils.resolveTypeArguments(clazz, superClass);
    }
}
