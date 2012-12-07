package myToolWindow;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;

/**
 * Created by IntelliJ IDEA. User: Alexey.Chursin Date: Aug 25, 2010 Time: 2:09:00 PM
 */
public class RejectReasonToolWindow implements ToolWindowFactory, RejectReasonListener {

    private ToolWindow myToolWindow;
    private JLabel dataLabel = new JLabel("This is a text label");
    private JPanel jPanel;


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
        myToolWindow = toolWindow;
        final ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        jPanel = new JPanel();
        jPanel.add(dataLabel);
        final Content content = contentFactory.createContent(jPanel, "", false);
        toolWindow.getContentManager().addContent(content);

    }


    @Override
    public void fireAddRejectReason(final String aRejectReason) {
        System.out.println("dialog finished "  + aRejectReason);
        System.out.println("Panel is " + jPanel);
        System.out.println("Tool window is " + myToolWindow);
        System.out.println("dialog is ok");
        dataLabel.setText(aRejectReason);

    }
}
