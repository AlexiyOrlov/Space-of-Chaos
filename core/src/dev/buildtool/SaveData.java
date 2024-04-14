package dev.buildtool;

import java.util.Map;

public interface SaveData {
    Map<String,Object> getData();
    void load(Map<String,Object> data);
}
