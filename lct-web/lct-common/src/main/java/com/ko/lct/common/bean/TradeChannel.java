package com.ko.lct.common.bean;

import java.util.List;

public class TradeChannel extends BaseDictionaryItem {
    private static final long serialVersionUID = -3560500224958432631L;

    private boolean foodServiceInd;
    private List<SubTradeChannel> subTradeChannel;

    public boolean isFoodServiceInd() {
	return foodServiceInd;
    }

    public void setFoodServiceInd(boolean foodServiceInd) {
	this.foodServiceInd = foodServiceInd;
    }

    public List<SubTradeChannel> getSubTradeChannel() {
	return subTradeChannel;
    }

    public void setSubTradeChannel(List<SubTradeChannel> subTradeChannel) {
	this.subTradeChannel = subTradeChannel;
    }

}
