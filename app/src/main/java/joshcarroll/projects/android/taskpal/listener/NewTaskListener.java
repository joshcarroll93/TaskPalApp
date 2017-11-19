package joshcarroll.projects.android.taskpal.listener;

import joshcarroll.projects.android.taskpal.data.NewTask;

public interface NewTaskListener {
    void addTask(NewTask newTask);
    void removeTask(NewTask removedTask);
}
