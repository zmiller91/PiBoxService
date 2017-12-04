package com.zm.piboxservice.service;

import java.util.HashMap;

public class Catalog<T> extends HashMap<String, T> {

    @Override
    public T put(String id, T object) {
        if(this.containsKey(id)) {
            throw new AssertionError("The id '" + id + "' already exists in the catalog." );
        }
        return super.put(id, object);
    }
}
