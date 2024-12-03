package io.github.bootystar.mybatisplus.generate.config.impl;

import io.github.bootystar.mybatisplus.enhance.builder.FieldSuffixBuilder;
import io.github.bootystar.mybatisplus.enhance.helper.unmodifiable.DynamicFieldSqlHelper;
import io.github.bootystar.mybatisplus.generate.config.base.CustomConfig;
import io.github.bootystar.mybatisplus.generate.info.ClassInfo;
import lombok.Getter;

import java.util.function.Consumer;


/**
 * @author bootystar
 */
@Getter
public class DynamicFieldConfig extends CustomConfig {

    {
        mapperDTO = new ClassInfo(DynamicFieldSqlHelper.class);
    }

    public static class Builder extends CustomConfig.Builder<DynamicFieldConfig, Builder> {
        private final DynamicFieldConfig config = new DynamicFieldConfig();

        @Override
        protected DynamicFieldConfig getConfig() {
            return config;
        }

        @Override
        protected Builder getBuilder() {
            return this;
        }

        /**
         * 获取字段后缀生成器
         *
         * @return {@link FieldSuffixBuilder }
         * @author bootystar
         */
        public FieldSuffixBuilder getFieldSuffixBuilder() {
            return this.getConfig().extraFieldSuffixBuilder;
        }

        /**
         * 指定后缀
         *
         * @param builderConsumer builder消费者
         * @return {@link Builder }
         * @author bootystar
         */
        public Builder fieldSuffixBuilder(Consumer<FieldSuffixBuilder> builderConsumer) {
            builderConsumer.accept(this.getConfig().extraFieldSuffixBuilder);
            return this.getBuilder();
        }


        /**
         * 不生成重写的方法
         *
         * @return {@code U }
         * @author bootystar
         */
        public Builder disableOverrideMethods() {
            this.getConfig().overrideMethods = false;
            return this.getBuilder();
        }


    }
}

