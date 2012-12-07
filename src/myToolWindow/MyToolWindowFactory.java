package myToolWindow;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;

/**
 * Created by IntelliJ IDEA. User: Alexey.Chursin Date: Aug 25, 2010 Time: 2:09:00 PM
 */
public class MyToolWindowFactory extends AnAction implements ToolWindowFactory {

    // private JButton refreshToolWindowButton;
    // private JButton hideToolWindowButton;
    private ToolWindow myToolWindow;
    private JLabel dataLabel = new JLabel("This is a text label");
    private JPanel jPanel;


    public MyToolWindowFactory() {
        super("Do something !");
        com.intellij.openapi.actionSystem.ActionManager am = com.intellij.openapi.actionSystem.ActionManager.getInstance().getInstance();
        // Passes an instance of your custom TextBoxes class to the registerAction method of the ActionManager class.
        am.registerAction("MyPluginAction", this);
        // Gets an instance of the WindowMenu action group.
        DefaultActionGroup windowM = (DefaultActionGroup) am.getAction("GenerateGroup");
        // Adds a separator and a new menu command to the WindowMenu group on the main menu.
        windowM.addSeparator();
        windowM.add(this);
        // hideToolWindowButton.addActionListener(new ActionListener() {
        // public void actionPerformed(ActionEvent e) {
        // myToolWindow.hide(null);
        // }
        // });
        // refreshToolWindowButton.addActionListener(new ActionListener() {
        // public void actionPerformed(ActionEvent e) {
        //
        // }
        // });
    }


    @Override
    public void actionPerformed(final AnActionEvent aAnActionEvent) {
        PsiClass psiClass = getPsiClassFromContext(aAnActionEvent);
        GenerateDialog dlg = new GenerateDialog(psiClass);
        dlg.show();
        System.out.println("dialog finished "+dlg.isOK() + " "+dlg.getComment());
        System.out.println("Panel is " +jPanel);
        System.out.println("Tool window is " + myToolWindow);
        if (dlg.isOK() && jPanel != null) {
            System.out.println("dialog is ok");
            jPanel.remove(dataLabel);
            dataLabel = new JLabel(dlg.getComment());
            jPanel.add(dataLabel);
            jPanel.invalidate();
//            myToolWindow.getComponent().invalidate();
        }
    }


    // Create the tool window content.
    public void createToolWindowContent(Project project, ToolWindow toolWindow) {
        System.out.println("Tool window initialized");
        myToolWindow = toolWindow;
        // this.currentDateTime();
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        jPanel = new JPanel();
        jPanel.add(dataLabel);
        Content content = contentFactory.createContent(jPanel, "", false);
        toolWindow.getContentManager().addContent(content);

    }


    private PsiClass getPsiClassFromContext(AnActionEvent e) {
        PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        Editor editor = e.getData(PlatformDataKeys.EDITOR);
        if (psiFile == null || editor == null) {
            return null;
        }
        int offset = editor.getCaretModel().getOffset();
        PsiElement elementAt = psiFile.findElementAt(offset);
        PsiClass psiClass = PsiTreeUtil.getParentOfType(elementAt, PsiClass.class);
        return psiClass;
    }



}
