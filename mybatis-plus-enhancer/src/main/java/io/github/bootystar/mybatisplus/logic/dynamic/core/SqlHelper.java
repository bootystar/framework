package io.github.bootystar.mybatisplus.logic.dynamic.core;

import io.github.bootystar.mybatisplus.logic.dynamic.enums.SqlKeyword;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 动态sql助手
 *
 * @author bootystar
 */
@SuppressWarnings("unused")
@Data
@EqualsAndHashCode(callSuper = true)
public class SqlHelper extends ConditionTree {

    /**
     * 排序条件列表
     */
    protected List<? extends Sort> sorts;

    /**
     * 添加前置条件
     * 前置条件优先于现有条件, 必定生效
     *
     * @param entity   实体
     * @param operator 操作符号
     * @return {@link SqlHelper }
     * @author bootystar
     */
    public SqlHelper addRequiredConditions(Object entity, String operator) {
        return addRequiredConditions(conditionsFromEntity(entity, operator));
    }

    /**
     * 添加前置条件
     * 前置条件优先于现有条件, 必定生效
     * 前置条件新>前置条件旧>一般条件
     *
     * @param conditions 条件
     * @return {@link SqlHelper }
     * @author bootystar
     */
    public SqlHelper addRequiredConditions(Condition... conditions) {
        return addRequiredConditions(Arrays.asList(conditions));
    }

    /**
     * 添加前置条件
     * 前置条件优先于现有条件, 必定生效
     *
     * @param conditions 条件
     * @return {@link SqlHelper }
     * @author bootystar
     */
    public SqlHelper addRequiredConditions(List<Condition> conditions) {
        ConditionTree conditionO = getChild();
        ConditionTree conditionN = new ConditionTree();
        setChild(conditionN);
        conditionN.setChild(conditionO);
        conditionN.setConditions(conditions);
        return this;
    }

    /**
     * 添加一般条件
     * 和现有条件同等优先级
     *
     * @param entity   实体
     * @param operator 操作符 {@link SqlKeyword}
     * @return {@link SqlHelper }
     * @author bootystar
     */
    public SqlHelper addConditions(Object entity, String operator) {
        return addConditions(conditionsFromEntity(entity, operator));
    }

    /**
     * 添加一般条件
     * 和现有条件同等优先级
     *
     * @param conditions 条件
     * @return {@link SqlHelper }
     * @author bootystar
     */
    public SqlHelper addConditions(Condition... conditions) {
        return addConditions(new ArrayList<>(Arrays.asList(conditions)));
    }

    /**
     * 添加一般条件
     * 和现有条件同等优先级
     *
     * @param conditions 条件
     * @return {@link SqlHelper }
     * @author bootystar
     */
    public SqlHelper addConditions(List<Condition> conditions) {
        if (conditions == null || conditions.isEmpty()) {
            return this;
        }
        List<? extends Condition> conditionsO = getConditions();
        int size = conditionsO == null ? conditions.size() : conditionsO.size() + conditions.size();
        ArrayList<Condition> conditionsN = new ArrayList<>(size);
        conditionsN.addAll(conditions);
        if (conditionsO != null) {
            conditionsN.addAll(conditionsO);
        }
        setConditions(conditionsN);
        return this;
    }

    /**
     * 获取数据库实体类对应的不可变SQL拼接器
     *
     * @param entityClass 实体类
     * @return {@link UnmodifiableSqlHelper }<{@link T }>
     * @author bootystar
     */
    public <T> UnmodifiableSqlHelper<T> unmodifiable(Class<T> entityClass) {
        return new UnmodifiableSqlHelper<>(this, entityClass);
    }


}
