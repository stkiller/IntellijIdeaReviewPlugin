package com.stkiller.idea.reviewplugin.service;

public class ReasonFormatter {

    public ReasonFormatter() {
    }


    public String getFormattedReason(final String aElementFQN, final String aComment) {
        final String elementFqn = "{noformat}" + aElementFQN + "{noformat}";
        return elementFqn + "\n" + aComment;
    }
}