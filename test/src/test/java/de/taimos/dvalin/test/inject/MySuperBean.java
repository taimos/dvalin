package de.taimos.dvalin.test.inject;

import org.springframework.beans.factory.annotation.Autowired;

public class MySuperBean {

    @Autowired
    private BeanA aField;

    public BeanA getaField() {
        return this.aField;
    }
}
