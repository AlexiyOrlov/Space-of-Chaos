package dev.buildtool;

public class Container {
    public Stack stack;
    public float x,y;
    public float rotation;

    public Container(Stack stack, float x, float y, float rotation) {
        this.stack = stack;
        this.x = x;
        this.y = y;
        this.rotation = rotation;
    }
}
