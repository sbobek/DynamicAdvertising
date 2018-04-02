package rl.model;

public enum AuctionResult {
    LOST(0),
    WON(1)
    ;

    private int id;

    AuctionResult(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
