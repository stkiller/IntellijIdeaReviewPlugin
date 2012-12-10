package com.stkiller.idea.reviewplugin.interfaces;

/**
 * @author andrei (07/12/2012)
 */
public interface RejectReasonListener {

    public void fireAddRejectReason(final String aRejectReason);


    public void resetRejectReasons();


    public String getGeneratedRejectReasons();

}
