package com.stkiller.idea.reviewplugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.stkiller.idea.reviewplugin.GenerateDialog;
import com.stkiller.idea.reviewplugin.interfaces.RejectListenerInteractor;
import com.stkiller.idea.reviewplugin.interfaces.RejectReasonListener;
import com.stkiller.idea.reviewplugin.service.FqnExtractor;
import com.stkiller.idea.reviewplugin.service.ReasonFormatter;

/**
 * @author Andrei Podoprigora (07/12/2012)
 */
public class AddRejectReasonAction extends AnAction implements RejectListenerInteractor {

    private final FqnExtractor fqnExtractor;
    private final ReasonFormatter reasonFormatter;
    private RejectReasonListener rejectReasonListener;


    public AddRejectReasonAction() {
        reasonFormatter = new ReasonFormatter();
        fqnExtractor = new FqnExtractor();
    }


    public void setRejectReasonListener(final RejectReasonListener aReasonListener) {
        rejectReasonListener = aReasonListener;
    }


    @Override
    public void update(final AnActionEvent aEvent) {
        aEvent.getPresentation().setEnabled(fqnExtractor.getCaretElementFQN(aEvent) != null);
    }


    @Override
    public void actionPerformed(final AnActionEvent aEvent) {
        final String elementFQN = fqnExtractor.getCaretElementFQN(aEvent);
        final GenerateDialog dlg = new GenerateDialog();
        dlg.show();
        if (dlg.isOK()) {
            rejectReasonListener.fireAddRejectReason(reasonFormatter.getFormattedReason(elementFQN, dlg.getComment()));
        }
    }


}
