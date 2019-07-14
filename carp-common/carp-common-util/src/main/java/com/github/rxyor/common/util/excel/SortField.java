package com.github.rxyor.common.util.excel;

import java.lang.reflect.Field;

/**
 *<p>
 *
 *</p>
 *
 * @author liuyang
 * @date 2019/1/8 Tue 10:42:00
 * @since 1.0.0
 */
public class SortField implements Comparable<SortField> {

    private Field field;
    private int index;

    public SortField() {
    }

    public SortField(Field field, int index) {
        this.field = field;
        this.index = index;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public int compareTo(SortField sortField) {
        return this.index - sortField.index;
    }
}
