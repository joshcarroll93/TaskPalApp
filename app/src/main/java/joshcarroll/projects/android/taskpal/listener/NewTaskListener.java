package joshcarroll.projects.android.taskpal.listener;

import joshcarroll.projects.android.taskpal.data.NewTask;

/**
 * Created by Josh on 22/10/2017.
 */

public interface NewTaskListener {
    void addTask(NewTask newTask);
    void removeTask(NewTask removedTask);
}
