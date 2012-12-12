package com.stkiller.idea.reviewplugin;

import java.awt.BorderLayout;

import javax.swing.JPanel;

import com.intellij.execution.filters.RegexpFilter;
import com.intellij.execution.impl.ConsoleViewImpl;
import com.intellij.execution.ui.ConsoleViewContentType;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionPlaces;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.stkiller.idea.reviewplugin.interfaces.RejectListenerInteractor;
import com.stkiller.idea.reviewplugin.interfaces.RejectReasonListener;

/**
 * @author Andrei Podoprigora
 */
public class RejectReasonToolWindow implements ToolWindowFactory, RejectReasonListener {

    private final ActionManager actionManager;
    private ConsoleViewImpl console;
    private Project project;


    public RejectReasonToolWindow() {
        actionManager = ActionManager.getInstance();

        registerAsRejectReasonListener("review_aid.add_reason");
        registerAsRejectReasonListener("review_aid.clear_reasons");
        registerAsRejectReasonListener("review_aid.copy_reasons");
    }


    private void registerAsRejectReasonListener(final String aActionId) {
        final AnAction clearReviewAidScreen = actionManager.getAction(aActionId);
        if (clearReviewAidScreen instanceof RejectListenerInteractor) {
            ((RejectListenerInteractor)clearReviewAidScreen).setRejectReasonListener(this);
        }
    }


    // Create the tool window content.
    public void createToolWindowContent(final Project project, final ToolWindow toolWindow) {
        this.project = project;
        final ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        final JPanel jPanel = new JPanel(new BorderLayout());
        console = new ConsoleViewImpl(project, GlobalSearchScope.allScope(project), false, null);
        console.addMessageFilter(new RegexpFilter(project, RegexpFilter.FILE_PATH_MACROS + ":" + RegexpFilter.LINE_MACROS + ".*"));
        jPanel.add(initActionBar(jPanel).getComponent(), BorderLayout.WEST);
        jPanel.add(console.getComponent(), BorderLayout.CENTER);
        final Content content = contentFactory.createContent(jPanel, "", false);
        toolWindow.getContentManager().addContent(content);


    }


    private ActionToolbar initActionBar(final JPanel aJPanel) {
        final ActionToolbar actionToolbar = actionManager.createActionToolbar(ActionPlaces.UNKNOWN, (ActionGroup)actionManager.getAction(
                "review_aid.toolbar_actions"), false);
        actionToolbar.setTargetComponent(aJPanel);
        return actionToolbar;
    }


    @Override
    public void fireAddRejectReason(final String aRejectReason) {
        console.print(aRejectReason + "\n", ConsoleViewContentType.NORMAL_OUTPUT);

    }


    @Override
    public void resetRejectReasons() {
        console.clear();
    }


    @Override
    public String getGeneratedRejectReasons() {
        final Object editor = console.getData(PlatformDataKeys.EDITOR.getName());
        if ((editor instanceof Editor)) {
            final String text = ((Editor)editor).getDocument().getText();
            if (text != null) {
                return text.replaceAll(FileUtil.toSystemIndependentName(project.getBasePath()), "");
            }
        }
        return null;
    }
}
