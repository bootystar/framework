package io.github.bootystar.mybatisplus.core.param;

import io.github.bootystar.mybatisplus.core.enums.SqlKeyword;
import io.github.bootystar.mybatisplus.util.MybatisPlusReflectHelper;
import lombok.Data;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.util.*;

/**
 * 条件树
 *
 * @author bootystar
 */
@Data
public class ConditionTree implements Iterable<ConditionTree> {

    /**
     * 条件
     */
    protected List<? extends Condition> conditions;

    /**
     * 子条件
     * (满足父条件后的值才会筛选子条件)
     */
    protected ConditionTree child;

    /**
     * 根据实体类生成条件
     *
     * @param entity   实体类
     * @param operator SQL操作符号 参考{@link SqlKeyword }
     * @return {@link List }<{@link Condition }>
     * @author bootystar
     */
    @SneakyThrows
    public static List<Condition> conditionsFromEntity(Object entity, String operator) {
        if (entity == null) {
            throw new IllegalStateException("entity is null");
        }
        Map<String, Field> fieldMap = MybatisPlusReflectHelper.fieldMap(entity.getClass());
        List<Condition> conditions = new ArrayList<>();
        for (Field field : fieldMap.values()) {
            Object value = field.get(entity);
            if (value == null) continue;
            Condition condition = new Condition();
            condition.setField(field.getName());
            condition.setOperator(operator);
            condition.setValue(value);
            conditions.add(condition);
        }
        return conditions;
    }

    @Override
    @SuppressWarnings("all")
    public Iterator<ConditionTree> iterator() {
        return new TreeIterator();
    }

    /**
     * 树迭代器
     *
     * @author bootystar
     */
    private class TreeIterator implements Iterator<ConditionTree> {

        private final Stack<ConditionTree> stack = new Stack<>();

        public TreeIterator() {
            stack.push(ConditionTree.this);
        }

        @Override
        public boolean hasNext() {
            return !stack.isEmpty();
        }

        @Override
        public ConditionTree next() {
            boolean empty = stack.isEmpty();
            if (empty) {
                throw new NoSuchElementException();
            }
            ConditionTree pop = stack.pop();
            if (pop.getChild() != null) {
                stack.push(pop.getChild());
            }
            return pop;
        }

    }
}
