package dev.buildtool.projectiles;

public interface DestructibleProjectile {
    int getIntegrity();
    void damage(int damage);
}
