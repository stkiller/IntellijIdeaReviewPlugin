package com.stkiller.idea.reviewplugin.service;

import com.stkiller.idea.reviewplugin.settings.ReviewAidSettingsHolder;

public class ReasonFormatter {

    public ReasonFormatter() {
    }


    public String getFormattedReason(final String aElementFQN, final String aLineNumber, final String aComment) {
        return String.format(ReviewAidSettingsHolder.getInstance().OUTPUT_FORMAT_REGEX, aElementFQN, aLineNumber, aComment);
    }
}