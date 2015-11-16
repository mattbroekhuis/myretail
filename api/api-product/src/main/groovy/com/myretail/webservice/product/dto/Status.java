package com.myretail.webservice.product.dto;

public class Status {

    boolean databaseOkay;
    long dbResponseTime;

    public boolean isDatabaseOkay() {
        return databaseOkay;
    }

    public void setDatabaseOkay(boolean databaseOkay) {
        this.databaseOkay = databaseOkay;
    }

    public boolean isSystemOkay() {
        return isDatabaseOkay();
    }

    public void setSystemOkay(boolean systemOkay) {
        // for serialization
    }

    public long getDbResponseTime() {
        return dbResponseTime;
    }

    public void setDbResponseTime(long dbResponseTime) {
        this.dbResponseTime = dbResponseTime;
    }
}
