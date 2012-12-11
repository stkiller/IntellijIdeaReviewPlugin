package com.stkiller.idea.reviewplugin.settings;

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

    private final ReviewAidSettingsHolder settingsHolder;
    private JPanel generalPanel;
    private JTextArea formatArea;


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
        reset();
        return generalPanel;
    }


    @Override
    public boolean isModified() {
        return !settingsHolder.OUTPUT_FORMAT_REGEX.equals(formatArea.getText());
    }


    @Override
    public void apply() throws ConfigurationException {
        settingsHolder.OUTPUT_FORMAT_REGEX = formatArea.getText();
    }


    @Override
    public void reset() {
        formatArea.setText(settingsHolder.OUTPUT_FORMAT_REGEX);
    }


    @Override
    public void disposeUIResources() {
    }
}
