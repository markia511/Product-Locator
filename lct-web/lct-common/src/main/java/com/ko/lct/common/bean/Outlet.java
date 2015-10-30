package com.ko.lct.common.bean;

import java.io.Serializable;

public class Outlet implements Serializable {
    private static final long serialVersionUID = -6294693809827820621L;

    private String tdlCode;
    private String nameId1;
    private String nameId2;
    private String chainName;
    private String name;
    private String phoneNumber;
    private Address address;
    private TradeChannel tradeChannel;
    private SubTradeChannel subTradeChannel;

    public String getTdlCode() {
	return tdlCode;
    }

    public void setTdlCode(String tdlCode) {
	this.tdlCode = tdlCode;
    }

    public String getNameId1() {
	return nameId1;
    }

    public void setNameId1(String nameId1) {
	this.nameId1 = nameId1;
    }

    public String getNameId2() {
	return nameId2;
    }

    public void setNameId2(String nameId2) {
	this.nameId2 = nameId2;
    }

    public String getChainName() {
	return chainName;
    }

    public void setChainName(String chainName) {
	this.chainName = chainName;
    }

    public String getName() {
	return name;
    }

    public void setName(String name) {
	this.name = name;
    }

    public String getPhoneNumber() {
	return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
	this.phoneNumber = phoneNumber;
    }

    public Address getAddress() {
	return address;
    }

    public void setAddress(Address address) {
	this.address = address;
    }

    public TradeChannel getTradeChannel() {
	return tradeChannel;
    }

    public void setTradeChannel(TradeChannel tradeChannel) {
	this.tradeChannel = tradeChannel;
    }

    public SubTradeChannel getSubTradeChannel() {
	return subTradeChannel;
    }

    public void setSubTradeChannel(SubTradeChannel subTradeChannel) {
	this.subTradeChannel = subTradeChannel;
    }

}
