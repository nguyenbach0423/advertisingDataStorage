package dev.model;

public class Ad {
    private long AdID;
    private String AdName;
    private String AdStatus;
    private String AdType;
    private double BidModifier;

    public Ad () {}

    public Ad (int AdID, String AdName, String AdStatus, String AdType, double BidModifier) {
        super();
        this.AdID = AdID;
        this.AdName = AdName;
        this.AdStatus = AdStatus;
        this.AdType = AdType;
        this.BidModifier = BidModifier;
    }

    public long getAdID() {
        return AdID;
    }

    public void setAdID(long adID) {
        AdID = adID;
    }

    public String getAdName() {
        return AdName;
    }

    public void setAdName(String adName) {
        AdName = adName;
    }

    public String getAdStatus() {
        return AdStatus;
    }

    public void setAdStatus(String adStatus) {
        AdStatus = adStatus;
    }

    public String getAdType() {
        return AdType;
    }

    public void setAdType(String adType) {
        AdType = adType;
    }

    public double getBidModifier() {
        return BidModifier;
    }

    public void setBidModifier(double bidModifier) {
        BidModifier = bidModifier;
    }
}
