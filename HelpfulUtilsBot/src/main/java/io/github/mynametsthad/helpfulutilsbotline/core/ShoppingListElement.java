package io.github.mynametsthad.helpfulutilsbotline.core;

import java.util.Date;

public class ShoppingListElement {
    public long addedTimestamp;
    public String name;
    public int quantity;
    public boolean crossed;

    public ShoppingListElement(String name, int quantity) {
        this.name = name;
        this.quantity = quantity;
        this.addedTimestamp = new Date().getTime();
        this.crossed = false;
    }
}
