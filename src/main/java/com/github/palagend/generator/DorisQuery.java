package com.github.palagend.generator;

import com.baomidou.mybatisplus.generator.config.querys.AbstractDbQuery;

public class DorisQuery extends AbstractDbQuery {


    @Override
    public String tablesSql() {
        return "show table status WHERE table_schema = DATABASE() ";
    }

    @Override
    public String tableFieldsSql() {
        // 使用 Doris 兼容的查询语句来获取表字段信息
        return "SELECT * FROM information_schema.columns WHERE table_schema = DATABASE() AND table_name = '%s'";
    }

    @Override
    public String tableName() {
        return "NAME";
    }


    @Override
    public String tableComment() {
        return "COMMENT";
    }


    @Override
    public String fieldName() {
        return "COLUMN_NAME";
    }


    @Override
    public String fieldType() {
        return "COLUMN_TYPE";
    }


    @Override
    public String fieldComment() {
        return "COLUMN_COMMENT";
    }


    @Override
    public String fieldKey() {
        return "COLUMN_KEY";
    }

}