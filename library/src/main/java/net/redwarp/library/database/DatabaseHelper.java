/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Copyright 2015 Redwarp
 */

package net.redwarp.library.database;

import android.content.Context;

import java.util.List;

/**
 * Helper class to store and retrieve any kind of object.
 */
public class DatabaseHelper {

  private final Context mContext;

  public DatabaseHelper(Context context) {
    mContext = context;
    BaseAdapter.initSharedOpenHelper(context);
  }

  public void beginTransaction() {
    BaseAdapter.getOpenHelper().getWritableDatabase().beginTransaction();
  }

  public void endTransaction() {
    BaseAdapter.getOpenHelper().getWritableDatabase().endTransaction();
  }

  public void setTransactionSuccessful() {
    BaseAdapter.getOpenHelper().getWritableDatabase().setTransactionSuccessful();
  }

  public void registerClass(Class tClass) {
    getBaseAdapter(tClass);
  }

  public <T> boolean save(T object) {
    @SuppressWarnings("unchecked")
    Class<T> tClass = (Class<T>) object.getClass();

    BaseAdapter<T> adapter = getBaseAdapter(tClass);
    return adapter.save(object) != -1;
  }

  public <T> T getWithId(final Class<T> tClass, final long id) {
    BaseAdapter<T> adapter = getBaseAdapter(tClass);
    return adapter.getWithId(id);
  }

  public <T> List<T> getAll(final Class<T> tClass) {
    BaseAdapter<T> adapter = getBaseAdapter(tClass);
    return adapter.getAll();
  }

  private <T> BaseAdapter<T> getBaseAdapter(Class<T> tClass) {
    return BaseAdapter.adapterForClass(mContext, tClass);
  }

  public void close() {
    BaseAdapter.getOpenHelper().close();
  }

  /**
   * Delete an object from the database
   *
   * @param object the object
   * @param <T>    the object type
   * @return if the object was successfully deleted
   */
  public <T> boolean delete(T object) {
    // TODO implement
    @SuppressWarnings("unchecked")
    Class<T> tClass = (Class<T>) object.getClass();

    BaseAdapter<T> adapter = getBaseAdapter(tClass);
    return adapter.delete(object);
  }

  /**
   * Clear the table associated with the class
   *
   * @param tClass The object type to lear
   * @param <T>    The type of object
   * @return number or rows deleted
   */
  public <T> int clear(final Class<T> tClass) {
    BaseAdapter<T> adapter = getBaseAdapter(tClass);
    return adapter.clear();
  }
}
