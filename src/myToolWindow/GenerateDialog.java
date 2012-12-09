package myToolWindow;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

/**
 * @author yole
 */
public class GenerateDialog extends DialogWrapper {

    private final JTextField textField;
    private final JPanel panel;

    public GenerateDialog(final Project aProject) {
        super(aProject);
        setTitle("Please Provide a Comment, if Any");

        textField = new JTextField();
        panel = new JPanel(new BorderLayout());
        panel.add(textField, BorderLayout.CENTER);

        init();
        pack();
    }


    @Nullable
    @Override
    public JComponent getPreferredFocusedComponent() {
        return textField;
    }


    @Nullable
    @Override
    protected JComponent createCenterPanel() {
        return panel;
    }

    public String getComment() {
        return textField.getText();
    }
}
