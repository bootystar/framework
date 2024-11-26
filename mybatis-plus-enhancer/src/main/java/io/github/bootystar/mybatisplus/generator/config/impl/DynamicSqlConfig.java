package io.github.bootystar.mybatisplus.generator.config.impl;

import io.github.bootystar.mybatisplus.generator.config.base.CustomConfigEnhance;
import lombok.Getter;

/**
 * SQL拼接配置类
 *
 * @author bootystar
 */
@Getter
public class DynamicSqlConfig extends CustomConfigEnhance {

    {
        this.showServiceImplMethod = true;
        // todo SelectDTO Class
    }

    public static class Builder extends CustomConfigEnhance.Builder<DynamicSqlConfig, DynamicSqlConfig.Builder> {
        @Override
        protected DynamicSqlConfig initConfig() {
            return new DynamicSqlConfig();
        }

        @Override
        protected DynamicSqlConfig.Builder initBuilder() {
            return this;
        }
    }


}


