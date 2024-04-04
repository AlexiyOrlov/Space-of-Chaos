package dev.buildtool;

public class NPCPurchase {
    final Ware ware;
    final int boughtFor;

    public NPCPurchase(Ware ware, int boughtFor) {
        this.ware = ware;
        this.boughtFor = boughtFor;
    }
}
