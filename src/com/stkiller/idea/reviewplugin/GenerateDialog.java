package com.stkiller.idea.reviewplugin;

import java.awt.BorderLayout;
import java.awt.Dimension;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.intellij.openapi.ui.DialogWrapper;
import org.jetbrains.annotations.Nullable;

/**
 * @author yole
 */
public class GenerateDialog extends DialogWrapper {

    private final JTextArea textField;
    private final JPanel panel;


    public GenerateDialog() {
        super(null);
        setTitle("Please Provide a Comment, if Any");

        textField = new JTextArea();
//        textField.setMinimumSize(new Dimension(300, 100));
        textField.setPreferredSize(new Dimension(300, 100));
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
