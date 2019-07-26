package com.naqelexpress.naqelpointer.ContactNo;

/**
 * Created by Hasna on 10/30/18.
 */

public class ContactObj implements ListItem {
    String name;
    String mobileno ;

    public ContactObj(String name,String mobileno) {
        this.name = name;
        this.mobileno = mobileno;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobileno() {
        return mobileno;
    }

    public void setMobileno(String mobileno) {
        this.mobileno = mobileno;
    }

    @Override
    public int getItemType() {
        return ListItem.TYPE_ITEM;
    }
}