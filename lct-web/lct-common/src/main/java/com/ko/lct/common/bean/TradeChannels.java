package com.ko.lct.common.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TradeChannels implements Serializable {

    private static final long serialVersionUID = -1364798670465912771L;
	
    private List<TradeChannel> tradeChannels = new ArrayList<TradeChannel>();

    public List<TradeChannel> getTradeChannels() {
        return tradeChannels;
    }

    public void setTradeChannels(List<TradeChannel> tradeChannels) {
        this.tradeChannels = tradeChannels;
    }
	
}
