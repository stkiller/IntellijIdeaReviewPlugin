package com.stkiller.idea.reviewplugin.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author andrei (10/12/2012)
 */
@State(
        name = "ReviewAidSettingsHolder",
        storages = {@Storage(id = "other", file = StoragePathMacros.APP_CONFIG + "/review_aid.xml")}
)
public class ReviewAidSettingsHolder implements PersistentStateComponent<ReviewAidSettingsHolder>, ApplicationComponent {

    public String OUTPUT_FORMAT_REGEX = "{noformat}%1$s:%3$s[%2$s]{noformat}\n%4$s";


    @Nullable
    @Override
    public ReviewAidSettingsHolder getState() {
        return this;
    }


    @Override
    public void loadState(final ReviewAidSettingsHolder state) {
        XmlSerializerUtil.copyBean(state, this);
    }


    @Override
    public void initComponent() {
    }


    @Override
    public void disposeComponent() {
    }


    @NotNull
    @Override
    public String getComponentName() {
        return "ReviewAidSettingsHolder";
    }


    public static ReviewAidSettingsHolder getInstance() {
        return ApplicationManager.getApplication().getComponent(ReviewAidSettingsHolder.class);

    }
}
