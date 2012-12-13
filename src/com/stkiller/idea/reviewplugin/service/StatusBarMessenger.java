package com.stkiller.idea.reviewplugin.service;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.openapi.wm.ex.StatusBarEx;

/**
 * @author andrei (12/12/2012)
 */
public class StatusBarMessenger {


    public static void setStatusBarInfo(final Project project, final String message) {
        if (project != null) {
            final StatusBarEx statusBar = (StatusBarEx)WindowManager.getInstance().getStatusBar(project);
            if (statusBar != null) {
                statusBar.setInfo(message);
            }
        }
    }
}
