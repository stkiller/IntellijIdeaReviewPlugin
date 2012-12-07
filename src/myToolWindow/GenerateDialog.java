package myToolWindow;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiField;
import com.intellij.ui.CollectionListModel;
import org.jetbrains.annotations.Nullable;

/**
 * @author yole
 */
public class GenerateDialog extends DialogWrapper {

    private final JTextField label;
    private CollectionListModel<PsiField> myFields;
    private final LabeledComponent<JPanel> myComponent;

    public GenerateDialog(PsiClass psiClass) {
        super(psiClass.getProject());
        setTitle("Please provide a comment, if any");

        label = new JTextField("This is a comment");
        JPanel panel = new JPanel();
        panel.add(label);
        myComponent = LabeledComponent.create(panel, "Fields to include in compareTo():");

        init();
    }

    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return myComponent;
    }

    public String getComment() {
        return label.getText();
    }
}
