package com.womensafety.dto;

import java.util.List;

public class ReorderContactsRequest {
    private List<Long> contactIds;

    public List<Long> getContactIds() { return contactIds; }
    public void setContactIds(List<Long> contactIds) { this.contactIds = contactIds; }
}