package org.openhab.binding.opowerbinding.model;

import org.joda.time.DateTime;

public final class Insight {
    private DateTime billPeriodBegin;

    private DateTime billPeriodEnd;

    private Integer estimatedCost;

    private Integer projectedCost;

    private Integer estimatedUsage;

    private Integer projectedUsage;

    private String currency;

    private Boolean isActivePeakEnergyUsage;

    public DateTime getBillPeriodBegin() {
        return billPeriodBegin;
    }

    public void setBillPeriodBegin(DateTime billPeriodBegin) {
        this.billPeriodBegin = billPeriodBegin;
    }

    public DateTime getBillPeriodEnd() {
        return billPeriodEnd;
    }

    public void setBillPeriodEnd(DateTime billPeriodEnd) {
        this.billPeriodEnd = billPeriodEnd;
    }

    public Integer getEstimatedCost() {
        return estimatedCost;
    }

    public void setEstimatedCost(Integer estimatedCost) {
        this.estimatedCost = estimatedCost;
    }

    public Integer getProjectedCost() {
        return projectedCost;
    }

    public void setProjectedCost(Integer projectedCost) {
        this.projectedCost = projectedCost;
    }

    public Integer getEstimatedUsage() {
        return estimatedUsage;
    }

    public void setEstimatedUsage(Integer estimatedUsage) {
        this.estimatedUsage = estimatedUsage;
    }

    public Integer getProjectedUsage() {
        return projectedUsage;
    }

    public void setProjectedUsage(Integer projectedUsage) {
        this.projectedUsage = projectedUsage;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Boolean getIsActivePeakEnergyUsage() {
        return isActivePeakEnergyUsage;
    }

    public void setIsActivePeakEnergyUsage(Boolean isActivePeakEnergyUsage) {
        this.isActivePeakEnergyUsage = isActivePeakEnergyUsage;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((billPeriodBegin == null) ? 0 : billPeriodBegin.hashCode());
        result = prime * result + ((billPeriodEnd == null) ? 0 : billPeriodEnd.hashCode());
        result = prime * result + ((currency == null) ? 0 : currency.hashCode());
        result = prime * result + ((estimatedCost == null) ? 0 : estimatedCost.hashCode());
        result = prime * result + ((estimatedUsage == null) ? 0 : estimatedUsage.hashCode());
        result = prime * result + ((isActivePeakEnergyUsage == null) ? 0 : isActivePeakEnergyUsage.hashCode());
        result = prime * result + ((projectedCost == null) ? 0 : projectedCost.hashCode());
        result = prime * result + ((projectedUsage == null) ? 0 : projectedUsage.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Insight other = (Insight) obj;
        if (billPeriodBegin == null) {
            if (other.billPeriodBegin != null) {
                return false;
            }
        } else if (!billPeriodBegin.equals(other.billPeriodBegin)) {
            return false;
        }
        if (billPeriodEnd == null) {
            if (other.billPeriodEnd != null) {
                return false;
            }
        } else if (!billPeriodEnd.equals(other.billPeriodEnd)) {
            return false;
        }
        if (currency == null) {
            if (other.currency != null) {
                return false;
            }
        } else if (!currency.equals(other.currency)) {
            return false;
        }
        if (estimatedCost == null) {
            if (other.estimatedCost != null) {
                return false;
            }
        } else if (!estimatedCost.equals(other.estimatedCost)) {
            return false;
        }
        if (estimatedUsage == null) {
            if (other.estimatedUsage != null) {
                return false;
            }
        } else if (!estimatedUsage.equals(other.estimatedUsage)) {
            return false;
        }
        if (isActivePeakEnergyUsage == null) {
            if (other.isActivePeakEnergyUsage != null) {
                return false;
            }
        } else if (!isActivePeakEnergyUsage.equals(other.isActivePeakEnergyUsage)) {
            return false;
        }
        if (projectedCost == null) {
            if (other.projectedCost != null) {
                return false;
            }
        } else if (!projectedCost.equals(other.projectedCost)) {
            return false;
        }
        if (projectedUsage == null) {
            if (other.projectedUsage != null) {
                return false;
            }
        } else if (!projectedUsage.equals(other.projectedUsage)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "Insight [billPeriodBegin=" + billPeriodBegin + ", billPeriodEnd=" + billPeriodEnd + ", estimatedCost="
                + estimatedCost + ", projectedCost=" + projectedCost + ", estimatedUsage=" + estimatedUsage
                + ", projectedUsage=" + projectedUsage + ", currency=" + currency + ", isActivePeakEnergyUsage="
                + isActivePeakEnergyUsage + "]";
    }

}
