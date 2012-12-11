package com.stkiller.idea.reviewplugin.service;

import com.stkiller.idea.reviewplugin.settings.ReviewAidSettingsHolder;

public class ReasonFormatter {

    public ReasonFormatter() {
    }


    public String getFormattedReason(final FqnExtractorResult result, final String aComment) {
        return String.format(ReviewAidSettingsHolder.getInstance().OUTPUT_FORMAT_REGEX, result.getElementFqn().replace('.','/'), result.getElementName(), result.getElementLine(), aComment);
    }
}