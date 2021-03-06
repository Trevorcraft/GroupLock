package net.trevorcraft.grouplock.database;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

// This database store carries out create and delete actions synchronously but queues save actions
// This store is good for entities that are frequently saved but have a long lifespan
public class AsyncSaveStore<T extends Entity> extends DatabaseStore<T> {

  protected LinkedBlockingQueue<T> saveQueue = new LinkedBlockingQueue<>();
  protected Function<T, T> duplicator;

  public AsyncSaveStore(Function<T, T> duplicator) {
    this.duplicator = duplicator;
    ScheduledExecutorService exec = Executors.newSingleThreadScheduledExecutor();
    exec.scheduleAtFixedRate(() -> {
      while (saveQueue.size() > 0) {
        try {
          T obj = saveQueue.take();
          dbi.save(obj);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }

      }
    }, 0, 5, TimeUnit.SECONDS);

  }

  @Override
  protected boolean db_save(T obj) {
    try {
      saveQueue.put(duplicator.apply(obj));
      return true;
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return false;
  }

  @Override
  protected boolean db_delete(T obj) {
    //The delete action is performed
    return dbi.delete(obj);
  }

  @Override
  protected T db_create(T obj) {
    return dbi.create(obj);
  }

}
