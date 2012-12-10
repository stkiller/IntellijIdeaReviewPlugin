package com.stkiller.idea.reviewplugin.service;

import com.intellij.codeInsight.TargetElementUtilBase;
import com.intellij.ide.actions.QualifiedNameProvider;
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
import com.intellij.psi.PsiAnonymousClass;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiIdentifier;
import com.intellij.psi.PsiJavaCodeReferenceElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiMember;
import com.intellij.psi.PsiPackage;
import com.intellij.psi.PsiReference;
import com.intellij.util.LogicalRoot;
import com.intellij.util.LogicalRootsManager;
import org.jetbrains.annotations.Nullable;

public class FqnExtractor {

    private String elementFqn;


    private String elementLine;

    public String getElementFqn() {
        return elementFqn;
    }


    public String getElementLine() {
        return elementLine;
    }


    public boolean processCaretElementFQN(final AnActionEvent aEvent) {
        elementFqn = null;
        elementLine = null;

        final DataContext dataContext = aEvent.getDataContext();

        final Project project = PlatformDataKeys.PROJECT.getData(dataContext);
        final Editor editor = PlatformDataKeys.EDITOR.getData(dataContext);
        final PsiElement elementAt = getElement(editor, dataContext);
        elementFqn = getElementFqn(editor, elementAt);
        if (editor != null && project != null) {
            final Document document = editor.getDocument();
            final PsiFile file = PsiDocumentManager.getInstance(project).getCachedPsiFile(document);
            if (file != null) {
                if (elementFqn == null) {
                    elementFqn = getFileFqn(file);
                }
                elementLine = ""+(editor.getCaretModel().getLogicalPosition().line + 1);
            }
        }
        return elementFqn != null && elementLine != null;
    }


    @Nullable
    public PsiElement getElement(@Nullable final Editor editor, final DataContext dataContext) {
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


    private String getElementFqn(final Editor aEditor, final PsiElement aElementAt) {
        String result = getQualifiedNameFromProviders(aElementAt);
        if (result != null) {
            return result;
        }

        if (aEditor != null) { //IDEA-70346
            final PsiReference reference = TargetElementUtilBase.findReference(aEditor, aEditor.getCaretModel().getOffset());
            if (reference != null) {
                result = getQualifiedNameFromProviders(reference.resolve());
                if (result != null) {
                    return result;
                }
            }
        }

        String fqn = null;
        if (aElementAt instanceof PsiFile) {
            final PsiFile file = (PsiFile)aElementAt;
            fqn = FileUtil.toSystemIndependentName(getFileFqn(file));
        }
        return fqn;
    }


    @Nullable
    private String getQualifiedNameFromProviders(@Nullable PsiElement element) {
        if (element instanceof PsiPackage) {
            return ((PsiPackage)element).getQualifiedName();
        }
        element = getMember(element);
        if (element instanceof PsiClass) {
            return ((PsiClass)element).getQualifiedName();
        } else if (element instanceof PsiMember) {
            final PsiMember member = (PsiMember)element;
            PsiClass containingClass = member.getContainingClass();
            if (containingClass instanceof PsiAnonymousClass) {
                containingClass = ((PsiAnonymousClass)containingClass).getBaseClassType().resolve();
            }
            if (containingClass == null) {
                return null;
            }
            final String classFqn = containingClass.getQualifiedName();
            if (classFqn == null) {
                return member.getName();  // refer to member of anonymous class by simple name
            }
            return classFqn + "#" + member.getName();
        }else if (element instanceof PsiReference) {
            final PsiReference reference = (PsiReference)element;
            final PsiFile containingFile = reference.getElement().getContainingFile();
            String fileName = getFileFqn(containingFile);
            fileName = FileUtil.getNameWithoutExtension(fileName);
            return fileName + "#" + reference.getCanonicalText();
        }
        return null;
    }


    @Nullable
    private PsiElement getMember(final PsiElement element) {
        if (element instanceof PsiMember || element instanceof PsiReference) {
            return element;
        }
        if (!(element instanceof PsiIdentifier)) {
            return null;
        }
        final PsiElement parent = element.getParent();
        PsiMember member = null;
        if (parent instanceof PsiJavaCodeReferenceElement) {
            final PsiElement resolved = ((PsiJavaCodeReferenceElement)parent).resolve();
            if (resolved instanceof PsiMember) {
                member = (PsiMember)resolved;
            }
        } else if (parent instanceof PsiMember) {
            member = (PsiMember)parent;
        }
        return member;
    }


    public String getFileFqn(final PsiFile file) {
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