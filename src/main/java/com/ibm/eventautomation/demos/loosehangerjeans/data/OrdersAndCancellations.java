package com.ibm.eventautomation.demos.loosehangerjeans.data;

import java.util.List;

public class OrdersAndCancellations {

    private final List<Order> orderList;
    private final List<Cancellation> cancellationList;
    
    public OrdersAndCancellations(List<Order> orderList, List<Cancellation> cancellationList) {
        this.orderList = orderList;
        this.cancellationList = cancellationList;
    }

    public List<Order> getOrderList() {
        return orderList;
    }

    public List<Cancellation> getCancellationList() {
        return cancellationList;
    }

    
    
}
