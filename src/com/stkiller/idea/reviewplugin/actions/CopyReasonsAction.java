package com.stkiller.idea.reviewplugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ide.CopyPasteManager;
import com.stkiller.idea.reviewplugin.interfaces.RejectListenerInteractor;
import com.stkiller.idea.reviewplugin.interfaces.RejectReasonListener;
import com.stkiller.idea.reviewplugin.service.StatusBarMessenger;

/**
 * @author andrei (10/12/2012)
 */
public class CopyReasonsAction extends AnAction implements RejectListenerInteractor {

    private RejectReasonListener reasonListener;


    @Override
    public void update(final AnActionEvent aEvent) {
        final String rejectReasons = reasonListener.getGeneratedRejectReasons();
        aEvent.getPresentation().setEnabled(rejectReasons != null && !rejectReasons.isEmpty());
    }


    public void actionPerformed(final AnActionEvent aEvent) {
        final String fqn = reasonListener.getGeneratedRejectReasons();
        CopyPasteManager.getInstance().setContents(new RejectReasonTransferable(fqn));

        StatusBarMessenger.setStatusBarInfo(aEvent.getProject(), "Reject reasons copied to the clipboard.");
    }


    @Override
    public void setRejectReasonListener(final RejectReasonListener aReasonListener) {
        reasonListener = aReasonListener;
    }


}
