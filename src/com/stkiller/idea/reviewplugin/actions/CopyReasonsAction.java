package com.stkiller.idea.reviewplugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.openapi.wm.ex.StatusBarEx;
import com.stkiller.idea.reviewplugin.interfaces.RejectListenerInteractor;
import com.stkiller.idea.reviewplugin.interfaces.RejectReasonListener;

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

        setStatusBarText(aEvent.getProject(), "Reject reasons copied to the clipboard.");
    }


    private static void setStatusBarText(final Project project, final String message) {
        if (project != null) {
            final StatusBarEx statusBar = (StatusBarEx)WindowManager.getInstance().getStatusBar(project);
            if (statusBar != null) {
                statusBar.setInfo(message);
            }
        }
    }


    @Override
    public void setRejectReasonListener(final RejectReasonListener aReasonListener) {
        reasonListener = aReasonListener;
    }


}
