package dev.buildtool;

public class Hull {
    public static final Hull BASIC=new Hull(300);
    public static final Hull TRADING1=new Hull(600);
    public int capacity;

    public Hull(int capacity) {
        this.capacity = capacity;
    }
}
