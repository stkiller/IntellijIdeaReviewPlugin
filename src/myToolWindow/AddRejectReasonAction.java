package myToolWindow;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Editor;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;

/**
 * @author andrei (07/12/2012)
 */
public class AddRejectReasonAction extends AnAction {

    private final RejectReasonListener rejectReasonListener;


    public AddRejectReasonAction(final RejectReasonListener aRejectReasonListener) {
        super("Add Reject Reason");
        rejectReasonListener = aRejectReasonListener;
    }


    @Override
    public void actionPerformed(final AnActionEvent aAnActionEvent) {
        final PsiClass psiClass = getPsiClassFromContext(aAnActionEvent);
        final GenerateDialog dlg = new GenerateDialog(psiClass);
        dlg.show();
        if (dlg.isOK()) {
            rejectReasonListener.fireAddRejectReason(dlg.getComment());
        }
    }




    private PsiClass getPsiClassFromContext(final AnActionEvent e) {
        final PsiFile psiFile = e.getData(LangDataKeys.PSI_FILE);
        final Editor editor = e.getData(PlatformDataKeys.EDITOR);
        if (psiFile == null || editor == null) {
            return null;
        }
        final int offset = editor.getCaretModel().getOffset();
        final PsiElement elementAt = psiFile.findElementAt(offset);
        return PsiTreeUtil.getParentOfType(elementAt, PsiClass.class);
    }
}
