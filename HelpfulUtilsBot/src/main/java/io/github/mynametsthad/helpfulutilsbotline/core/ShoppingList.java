package io.github.mynametsthad.helpfulutilsbotline.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class ShoppingList {
    public long createdTimestamp;
    public String name;
    public List<ShoppingListElement> elements = new ArrayList<>();
    public List<String> allowedAccess = new ArrayList<>();

    public ShoppingList(String name, List<String> allowedAccess) {
        this.name = name;
        this.createdTimestamp = new Date().getTime();
        this.allowedAccess = allowedAccess;
    }

    public void AddElements(ShoppingListElement... elements){
        this.elements.addAll(Arrays.asList(elements));
    }
    public void RemoveElement(int index){
        elements.remove(index);
    }
}
