package com.example.odootest.addons.todotask.viewmodel;
//hihi
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.odootest.addons.todotask.model.TodoTask;
import com.example.odootest.addons.todotask.repository.TodoTaskRepository;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class TodoTaskViewModel extends ViewModel {
    private final TodoTaskRepository repository = new TodoTaskRepository();
    private final CompositeDisposable disposables = new CompositeDisposable();
    private final MutableLiveData<List<TodoTask>> tasks = new MutableLiveData<>();
    private final MutableLiveData<String> error = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private final MutableLiveData<String> eventMessage = new MutableLiveData<>();

    public LiveData<String> getEventMessage() {
        return eventMessage;
    }

    public LiveData<List<TodoTask>> getTasks() {
        return tasks;
    }

    public LiveData<String> getError() {
        return error;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void fetchTasks(String url, String db, int uid, String password, String query) {
        isLoading.setValue(true);
        disposables.add(
                repository.getTasks(url, db, uid, password, query)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                result -> {
                                    isLoading.setValue(false);
                                    tasks.setValue(result);
                                },
                                throwable -> {
                                    isLoading.setValue(false);
                                    error.setValue(throwable.getMessage());
                                }
                        )
        );
    }

    public void createTask(String url, String db, int uid, String password, String name, String description, String dueDate, boolean isDone) {
        isLoading.setValue(true);
        disposables.add(
                repository.createTask(url, db, uid, password, name, description, dueDate, isDone)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                taskId -> {
                                    eventMessage.setValue("Task created successfully!");
                                    fetchTasks(url, db, uid, password, ""); // Tải lại danh sách
                                },
                                throwable -> {
                                    isLoading.setValue(false);
                                    error.setValue("Create failed: " + throwable.getMessage());
                                }
                        )
        );
    }

    public void updateTask(String url, String db, int uid, String password, int taskId, String name, String description, String dueDate, boolean done) {
        isLoading.setValue(true);
        disposables.add(
                repository.updateTask(url, db, uid, password, taskId, name, description, dueDate, done)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                success -> {
                                    eventMessage.setValue("Task updated successfully!");
                                    fetchTasks(url, db, uid, password, ""); // Tải lại danh sách
                                },
                                throwable -> {
                                    isLoading.setValue(false);
                                    error.setValue("Update failed: " + throwable.getMessage());
                                }
                        )
        );
    }

    public void deleteTask(String url, String db, int uid, String password, int taskId) {
        isLoading.setValue(true);
        disposables.add(
                repository.deleteTask(url, db, uid, password, taskId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                success -> {
                                    eventMessage.setValue("Task deleted successfully!");
                                    fetchTasks(url, db, uid, password, ""); // Tải lại danh sách
                                },
                                throwable -> {
                                    isLoading.setValue(false);
                                    error.setValue("Delete failed: " + throwable.getMessage());
                                }
                        )
        );
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposables.clear();
    }
}