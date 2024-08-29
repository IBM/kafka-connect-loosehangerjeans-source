/**
 * Copyright 2024 IBM Corp. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
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
