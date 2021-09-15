package dev.model;

import java.util.Date;

public class Campaign {
    private long CampaignID;
    private String CampaignName;
    private String CampaignStatus;
    private Date StartDate;
    private Date EndDate;
    private double Budget;

    public Campaign() {}

    public Campaign(int CampaignID, String CampaignName, String CampaignStatus, Date StartDate, Date EndDate, double Budget) {
        super();
        this.CampaignID = CampaignID;
        this.CampaignName = CampaignName;
        this.CampaignStatus = CampaignStatus;
        this.StartDate = StartDate;
        this.EndDate = EndDate;
        this.Budget = Budget;
    }

    public long getCampaignID() {
        return CampaignID;
    }

    public void setCampaignID(long campaignID) {
        CampaignID = campaignID;
    }

    public String getCampaignName() {
        return CampaignName;
    }

    public void setCampaignName(String campaignName) {
        CampaignName = campaignName;
    }

    public String getCampaignStatus() {
        return CampaignStatus;
    }

    public void setCampaignStatus(String campaignStatus) {
        CampaignStatus = campaignStatus;
    }

    public Date getStartDate() {
        return StartDate;
    }

    public void setStartDate(Date startDate) {
        StartDate = startDate;
    }

    public Date getEndDate() {
        return EndDate;
    }

    public void setEndDate(Date endDate) {
        EndDate = endDate;
    }

    public double getBudget() {
        return Budget;
    }

    public void setBudget(double budget) {
        Budget = budget;
    }
}