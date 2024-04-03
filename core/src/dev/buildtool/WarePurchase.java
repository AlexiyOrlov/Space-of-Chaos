package dev.buildtool;

public class WarePurchase {
    final Ware ware;
    final int amountBought;
    final int pricePerUnit;
    final int moneySpent;

    public WarePurchase(Ware ware, int amountBought, int pricePerUnit, int moneySpent) {
        this.ware = ware;
        this.amountBought = amountBought;
        this.pricePerUnit = pricePerUnit;
        this.moneySpent = moneySpent;
    }
}
