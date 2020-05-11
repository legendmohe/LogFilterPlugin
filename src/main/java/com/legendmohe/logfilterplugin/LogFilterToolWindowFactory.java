package com.legendmohe.logfilterplugin;

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

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.event.ChangeEvent;

public class LogFilterToolWindowFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(createLogFilterComponent(), "", true);
        toolWindow.getContentManager().addContent(content);
    }

    private JComponent createLogFilterComponent() {
        return new LogFilterComponent(new LogFilterFrame.FrameInfoProvider() {
            @Override
            public JFrame getContainerFrame() {
                return null;
            }

            @Override
            public void onViewPortChanged(LogFilterComponent logFilterComponent, ChangeEvent e) {

            }

            @Override
            public void setTabTitle(LogFilterComponent filterComponent, String strTitle, String tips) {

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
    }
}
