package com.naqelexpress.naqelpointer.ContactNo;

/**
 * Created by Hasna on 10/30/18.
 */

public class Header implements ListItem {
    String name;

    public Header(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int getItemType() {
        return ListItem.TYPE_HEADER;
    }
}