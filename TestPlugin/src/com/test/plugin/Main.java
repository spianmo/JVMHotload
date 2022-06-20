package com.test.plugin;

import hotload.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 */
public class Main implements Plugin {

    @Override
    public void main() {
        System.out.println("Hello world!");
    }

    public List<String> testApi(Map<String, String> params) {
        List<String> list = new ArrayList<>();
        params.forEach((key, value)->{
            list.add(key);
            list.add(value);
        });
        return list;
    }
}