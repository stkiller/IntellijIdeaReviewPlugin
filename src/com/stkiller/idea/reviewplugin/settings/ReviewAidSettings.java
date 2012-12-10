package com.stkiller.idea.reviewplugin.settings;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

/**
 * @author andrei (10/12/2012)
 */
public class ReviewAidSettings implements Configurable {

    private JTextArea textField;
    ReviewAidSettingsHolder settingsHolder;


    public ReviewAidSettings() {
        settingsHolder = ReviewAidSettingsHolder.getInstance();
    }


    @Nls
    @Override
    public String getDisplayName() {
        return "Review Aid";
    }


    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }


    @Nullable
    @Override
    public JComponent createComponent() {
        textField = new JTextArea(settingsHolder.OUTPUT_FORMAT_REGEX);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(textField, BorderLayout.NORTH);
        return panel;
    }


    @Override
    public boolean isModified() {
        return !settingsHolder.OUTPUT_FORMAT_REGEX.equals(textField.getText());
    }


    @Override
    public void apply() throws ConfigurationException {
        settingsHolder.OUTPUT_FORMAT_REGEX = textField.getText();
    }


    @Override
    public void reset() {
    }


    @Override
    public void disposeUIResources() {
    }
}
