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


    public FqnExtractorResult processCaretElementFQN(final AnActionEvent aEvent) {

        final DataContext dataContext = aEvent.getDataContext();

        final Project project = PlatformDataKeys.PROJECT.getData(dataContext);
        final Editor editor = PlatformDataKeys.EDITOR.getData(dataContext);
        final PsiElement elementAt = getElement(editor, dataContext);
        final FqnExtractorResult fqnExtractorResult = getElementFqn(editor, elementAt);
        if (editor != null && project != null) {
            final Document document = editor.getDocument();
            final PsiFile file = PsiDocumentManager.getInstance(project).getCachedPsiFile(document);
            if (file != null) {
                fqnExtractorResult.setElementFqn(getFileFqn(file));
                fqnExtractorResult.setElementLine(getLineNumber(editor));
                fqnExtractorResult.setElementColumn(getColumn(editor));
            }
        }
        return fqnExtractorResult;
    }


    private String getColumn(final Editor aEditor) {
        return "" + (aEditor.getCaretModel().getOffset());
    }


    private String getLineNumber(final Editor aEditor) {
        return "" + (aEditor.getCaretModel().getLogicalPosition().line + 1);
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


    private FqnExtractorResult getElementFqn(final Editor aEditor, final PsiElement aElementAt) {
        FqnExtractorResult result = getQualifiedNameFromProviders(aElementAt);
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
        if (aElementAt instanceof PsiFile) {
            final PsiFile file = (PsiFile)aElementAt;
            result = new FqnExtractorResult(FileUtil.toSystemIndependentName(getFileFqn(file)));
        }
        return result == null ? new FqnExtractorResult() : result;
    }


    @Nullable
    private FqnExtractorResult getQualifiedNameFromProviders(@Nullable PsiElement element) {
        if (element instanceof PsiPackage) {
            return new FqnExtractorResult(((PsiPackage)element).getQualifiedName());
        }
        element = getMember(element);
        if (element instanceof PsiClass) {
            return new FqnExtractorResult(((PsiClass)element).getQualifiedName());
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
                return new FqnExtractorResult(member.getName());  // refer to member of anonymous class by simple name
            }
            return new FqnExtractorResult(classFqn, member.getName());
        } else if (element instanceof PsiReference) {
            final PsiReference reference = (PsiReference)element;
            final PsiFile containingFile = reference.getElement().getContainingFile();
            String fileName = getFileFqn(containingFile);
            fileName = FileUtil.getNameWithoutExtension(fileName);
            return new FqnExtractorResult(fileName, reference.getCanonicalText());
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
            return FileUtil.toSystemIndependentName(VfsUtil.virtualToIoFile(virtualFile).getPath());
        }

        final VirtualFile contentRoot = ProjectRootManager.getInstance(project).getFileIndex().getContentRootForFile(virtualFile);
        if (contentRoot != null) {
            return FileUtil.toSystemIndependentName(VfsUtil.virtualToIoFile(virtualFile).getAbsolutePath());
        }
        return FileUtil.toSystemIndependentName(virtualFile.getPath());
    }
}