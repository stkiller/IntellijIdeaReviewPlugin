package com.stkiller.idea.reviewplugin;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.stkiller.idea.reviewplugin.interfaces.RejectListenerInteractor;
import com.stkiller.idea.reviewplugin.interfaces.RejectReasonListener;

/**
 * @author Andrei Podoprigora
 */
public class RejectReasonToolWindow implements ToolWindowFactory, RejectReasonListener {

    private final JTextArea textArea = new JTextArea();
    private final ActionManager actionManager;


    public RejectReasonToolWindow() {
        actionManager = ActionManager.getInstance();

        registerAsRejectReasonListener("review_aid.add_reason");
        registerAsRejectReasonListener("review_aid.clear_reasons");
    }


    private void registerAsRejectReasonListener(final String aActionId) {
        final AnAction clearReviewAidScreen = actionManager.getAction(aActionId);
        if (clearReviewAidScreen instanceof RejectListenerInteractor) {
            ((RejectListenerInteractor)clearReviewAidScreen).setRejectReasonListener(this);
        }
    }


    // Create the tool window content.
    public void createToolWindowContent(final Project project, final ToolWindow toolWindow) {
        final ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        textArea.setEditable(false);
        final JPanel jPanel = new JPanel(new BorderLayout());
        final JBScrollPane scrollPane = new JBScrollPane(textArea);
        final ActionToolbar actionToolbar = ActionManager.getInstance().createActionToolbar("Test toolbar",
                                                                                            (ActionGroup)actionManager.getAction(
                                                                                                    "review_aid.toolbar_actions"), false);
        actionToolbar.setTargetComponent(jPanel);
        jPanel.add(actionToolbar.getComponent(), BorderLayout.WEST);
        jPanel.add(scrollPane, BorderLayout.CENTER);
        final Content content = contentFactory.createContent(jPanel, "", false);
        toolWindow.getContentManager().addContent(content);


    }


    @Override
    public void fireAddRejectReason(final String aRejectReason) {
        textArea.setText(textArea.getText() + aRejectReason + "\n");

    }


    @Override
    public void resetRejectReasons() {
        textArea.setText("");
    }
}
