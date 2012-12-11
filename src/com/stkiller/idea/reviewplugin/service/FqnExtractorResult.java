package com.stkiller.idea.reviewplugin.service;

public class FqnExtractorResult {

    private String elementFqn;
    private String elementName;
    private String elementLine;


    public FqnExtractorResult(final String aElementFqn) {
        elementFqn = aElementFqn;
    }


    public FqnExtractorResult(final String aElementFqn, final String aElementName) {
        elementFqn = aElementFqn;
        elementName = aElementName;
    }


    public String getElementFqn() {
        return elementFqn;
    }


    public void setElementFqn(final String aElementFqn) {
        elementFqn = aElementFqn;
    }


    public String getElementName() {
        return elementName;
    }


    public void setElementName(final String aElementName) {
        elementName = aElementName;
    }


    public String getElementLine() {
        return elementLine;
    }


    public void setElementLine(final String aElementLine) {
        elementLine = aElementLine;
    }


    public boolean isValid() {
        return elementFqn != null && elementLine != null;
    }
}