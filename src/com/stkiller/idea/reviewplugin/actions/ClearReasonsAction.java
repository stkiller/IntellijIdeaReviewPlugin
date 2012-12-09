package com.stkiller.idea.reviewplugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.stkiller.idea.reviewplugin.interfaces.RejectListenerInteractor;
import com.stkiller.idea.reviewplugin.interfaces.RejectReasonListener;

/**
 * @author andrei (09/12/2012)
 */
public class ClearReasonsAction extends AnAction implements RejectListenerInteractor
{

    private RejectReasonListener reasonListener;


    public void actionPerformed(AnActionEvent e) {
        reasonListener.resetRejectReasons();

    }


    public void setRejectReasonListener(final RejectReasonListener aReasonListener) {

        reasonListener = aReasonListener;
    }
}
