package myToolWindow;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;

/**
 * @author Andrei Podoprigora
 */
public class RejectReasonToolWindow implements ToolWindowFactory, RejectReasonListener {

    private JTextArea textArea = new JTextArea();


    public RejectReasonToolWindow() {
        final ActionManager am = ActionManager.getInstance();
        final AddRejectReasonAction action = new AddRejectReasonAction(this);
        am.registerAction("MyPluginAction", action);
        final DefaultActionGroup actionGroup = (DefaultActionGroup)am.getAction("GenerateGroup");
        actionGroup.addSeparator();
        actionGroup.add(action);
    }


    // Create the tool window content.
    public void createToolWindowContent(final Project project, final ToolWindow toolWindow) {
        System.out.println("Tool window initialized");
        final ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        final JPanel jPanel = new JPanel(new BorderLayout());
        final JBScrollPane scrollPane = new JBScrollPane(textArea);
        jPanel.add(scrollPane, BorderLayout.CENTER);
        final Content content = contentFactory.createContent(jPanel, "", false);
        toolWindow.getContentManager().addContent(content);

    }


    @Override
    public void fireAddRejectReason(final String aRejectReason) {
        textArea.setText(textArea.getText() + aRejectReason+ "\n");

    }
}
