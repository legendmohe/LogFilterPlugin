package com.legendmohe.logfilterplugin;

import com.intellij.openapi.application.ApplicationAdapter;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.legendmohe.tool.FloatingFrameInfo;
import com.legendmohe.tool.LogFilterComponent;
import com.legendmohe.tool.LogFilterFrame;

import org.jetbrains.annotations.NotNull;

import java.awt.Component;
import java.awt.Frame;

import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;

public class LogFilterToolWindowFactory implements ToolWindowFactory {

    private Content content;
    private LogFilterComponent filterComponent;

    public LogFilterToolWindowFactory() {
        ApplicationManager.getApplication().addApplicationListener(new ApplicationAdapter() {
            @Override
            public void applicationExiting() {
                if (filterComponent != null) {
                    filterComponent.exit();
                }
            }
        });
    }

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();

        if (content != null) {
            toolWindow.getContentManager().removeContent(content, true);
        }
        content = contentFactory.createContent(createLogFilterComponent(project, toolWindow), "", true);
        toolWindow.getContentManager().addContent(content);
    }

    private JComponent createLogFilterComponent(Project project, ToolWindow toolWindow) {
        filterComponent = new LogFilterComponent(new LogFilterFrame.FrameInfoProvider() {
            @Override
            public Frame getContainerFrame() {
                return null;
            }

            @Override
            public boolean enableFloatingWindow() {
                return false;
            }

            @Override
            public void onViewPortChanged(LogFilterComponent logFilterComponent, ChangeEvent e) {

            }

            @Override
            public void setTabTitle(LogFilterComponent filterComponent, String strTitle, String tips) {
                toolWindow.setTitle(strTitle);
            }

            @Override
            public boolean isFrameFocused() {
                return false;
            }

            @Override
            public FloatingFrameInfo onFilterFloating(LogFilterComponent filter, Component component, String title) {
                return null;
            }

            @Override
            public void beforeLogFileParse(String filename, LogFilterComponent filterComponent) {

            }
        });
        toolWindow.show(filterComponent::restoreSplitPane);
        return filterComponent;
    }
}
