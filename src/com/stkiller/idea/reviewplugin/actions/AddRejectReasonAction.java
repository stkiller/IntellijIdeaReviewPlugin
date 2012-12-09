package com.stkiller.idea.reviewplugin.actions;

import com.intellij.codeInsight.TargetElementUtilBase;
import com.intellij.ide.actions.CopyReferenceAction;
import com.intellij.ide.actions.QualifiedNameProvider;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.LangDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiReference;
import com.intellij.util.LogicalRoot;
import com.intellij.util.LogicalRootsManager;
import com.stkiller.idea.reviewplugin.GenerateDialog;
import com.stkiller.idea.reviewplugin.interfaces.RejectListenerInteractor;
import com.stkiller.idea.reviewplugin.interfaces.RejectReasonListener;
import org.jetbrains.annotations.Nullable;

/**
 * @author Andrei Podoprigora (07/12/2012)
 */
public class AddRejectReasonAction extends AnAction implements RejectListenerInteractor {

    private RejectReasonListener rejectReasonListener;


    public void setRejectReasonListener(final RejectReasonListener aReasonListener) {
        rejectReasonListener = aReasonListener;
    }


    @Override
    public void update(final AnActionEvent aEvent) {
        aEvent.getPresentation().setEnabled(getCaretElementFQN(aEvent) != null);
    }


    @Override
    public void actionPerformed(final AnActionEvent aEvent) {
        String elementFQN = getCaretElementFQN(aEvent);
        elementFQN = "{noformat}" + elementFQN + "{noformat}";
        final GenerateDialog dlg = new GenerateDialog();
        dlg.show();
        if (dlg.isOK()) {
            rejectReasonListener.fireAddRejectReason(elementFQN + "\n" + dlg.getComment());
        }
    }


    private String getCaretElementFQN(final AnActionEvent aEvent) {
        final DataContext dataContext = aEvent.getDataContext();

        final Project project = PlatformDataKeys.PROJECT.getData(dataContext);
        final Editor editor = PlatformDataKeys.EDITOR.getData(dataContext);
        final PsiElement elementAt = getElement(editor, dataContext);
        String elementFqn = CopyReferenceAction.elementToFqn(elementAt, editor);
        if (elementFqn == null && editor != null && project != null) {
            final Document document = editor.getDocument();
            final PsiFile file = PsiDocumentManager.getInstance(project).getCachedPsiFile(document);
            if (file != null) {
                elementFqn = getFileFqn(file) + ":" + (editor.getCaretModel().getLogicalPosition().line + 1);
            }
        }
        return elementFqn;
    }


    @Nullable
    private PsiElement getElement(@Nullable final Editor editor, final DataContext dataContext) {
        PsiElement element = null;
        if (editor != null) {
            final PsiReference reference = TargetElementUtilBase.findReference(editor);
            if (reference != null) {
                element = reference.getElement();
            }
        }

        if (element == null) {
            element = LangDataKeys.PSI_ELEMENT.getData(dataContext);
        }
        if (element == null && editor == null) {
            final VirtualFile virtualFile = PlatformDataKeys.VIRTUAL_FILE.getData(dataContext);
            final Project project = PlatformDataKeys.PROJECT.getData(dataContext);
            if (virtualFile != null && project != null) {
                element = PsiManager.getInstance(project).findFile(virtualFile);
            }
        }
        if (element instanceof PsiFile && !((PsiFile)element).getViewProvider().isPhysical()) {
            return null;
        }

        for (final QualifiedNameProvider provider : Extensions.getExtensions(QualifiedNameProvider.EP_NAME)) {
            final PsiElement adjustedElement = provider.adjustElementToCopy(element);
            if (adjustedElement != null) {
                return adjustedElement;
            }
        }
        return element;
    }


    private static String getFileFqn(final PsiFile file) {
        final VirtualFile virtualFile = file.getVirtualFile();
        if (virtualFile == null) {
            return file.getName();
        }
        final Project project = file.getProject();
        final LogicalRoot logicalRoot = LogicalRootsManager.getLogicalRootsManager(project).findLogicalRoot(virtualFile);
        if (logicalRoot != null) {
            final String logical = FileUtil.toSystemIndependentName(VfsUtil.virtualToIoFile(logicalRoot.getVirtualFile()).getPath());
            final String path = FileUtil.toSystemIndependentName(VfsUtil.virtualToIoFile(virtualFile).getPath());
            return "/" + FileUtil.getRelativePath(logical, path, '/');
        }

        final VirtualFile contentRoot = ProjectRootManager.getInstance(project).getFileIndex().getContentRootForFile(virtualFile);
        if (contentRoot != null) {
            return "/" + FileUtil.getRelativePath(VfsUtil.virtualToIoFile(contentRoot), VfsUtil.virtualToIoFile(virtualFile));
        }
        return virtualFile.getPath();
    }
}
