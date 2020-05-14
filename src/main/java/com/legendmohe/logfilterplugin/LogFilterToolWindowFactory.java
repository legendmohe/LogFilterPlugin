package com.legendmohe.logfilterplugin;

import com.intellij.ide.AppLifecycleListener;
import com.intellij.ide.util.PropertiesComponent;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.legendmohe.tool.LogFilterComponent;
import com.legendmohe.tool.LogFilterFrame;
import com.legendmohe.tool.annotation.UIStateSaver;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;

import javax.swing.JComponent;

public class LogFilterToolWindowFactory implements ToolWindowFactory {

    public static final String PROP_KEY = "log_filter_state";

    private Content content;

    public LogFilterToolWindowFactory() {
        final Application app = ApplicationManager.getApplication();
        app.getMessageBus().connect(app).subscribe(AppLifecycleListener.TOPIC, new AppLifecycleListener() {
            @Override
            public void appWillBeClosed(boolean isRestart) {
                if (content != null) {
                    ((LogFilterComponent) content.getComponent()).exit();
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
        LogFilterComponent filterComponent = new LogFilterComponent(new LogFilterFrame.FrameInfoProviderAdapter() {

            @Override
            public String getProjectRootPath() {
                return project.getBasePath();
            }

            @Override
            public boolean enableLogFlow() {
                return false;
            }

            @Override
            public void setTabTitle(LogFilterComponent filterComponent, String strTitle, String tips) {
                toolWindow.setTitle(strTitle);
            }
        }, new UIStateSaver.PersistenceHelper() {
            @Override
            public Map<String, Serializable> deserialize() {
                String value = PropertiesComponent.getInstance().getValue(PROP_KEY, "");
                if (value.length() > 0) {
                    try {
                        Object deserialize = LogFilterToolWindowFactory.deserialize(value);
                        return (Map<String, Serializable>) deserialize;
                    } catch (IOException | ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                return Collections.emptyMap();
            }

            @Override
            public void serialize(Map<String, Serializable> data) {
                try {
                    PropertiesComponent.getInstance().setValue(
                            PROP_KEY, LogFilterToolWindowFactory.serialize((Serializable) data)
                    );
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        toolWindow.show(filterComponent::restoreSplitPane);
        return filterComponent;
    }

    /**
     * Read the object from Base64 string.
     */
    private static Object deserialize(String s) throws IOException,
            ClassNotFoundException {
        byte[] data = Base64.getDecoder().decode(s);
        ObjectInputStream ois = new ObjectInputStream(
                new ByteArrayInputStream(data));
        Object o = ois.readObject();
        ois.close();
        return o;
    }

    /**
     * Write the object to a Base64 string.
     */
    private static String serialize(Serializable o) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(o);
        oos.close();
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }
}
