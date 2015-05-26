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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A class capable of taking another class as a time, and serialize it to a database. The database
 * is never closed, as read in https://groups.google.com/forum/#!msg/android-developers/nopkaw4UZ9U/cPfPL3uW7nQJ
 */
public class BaseAdapter<T> {

  private static SharedOpenHelper openHelper = null;
  private final TableInfo<T> mTableInfo;
  private final Context mContext;

  public BaseAdapter(@NonNull Context context, TableInfo<T> tableInfo) {
    mTableInfo = tableInfo;
    mContext = context;

    initSharedOpenHelper(context);

    createTableIfNeeded();
  }

  public static void initSharedOpenHelper(Context context) {
    if (openHelper == null) {
      openHelper = new SharedOpenHelper(context, "myBase", null, 1);
    }
  }

  private void createTableIfNeeded() {
    final long version = openHelper.getSavedClassVersion(mTableInfo);

    SQLiteDatabase db = openHelper.getWritableDatabase();

    Cursor
        cursor =
        db.rawQuery(
            "select DISTINCT tbl_name from sqlite_master where tbl_name = '" + mTableInfo.getName()
            + "'", null);
    boolean tableExist;
    if (cursor != null) {
      if (cursor.getCount() > 0) {
        tableExist = true;
      } else {
        tableExist = false;
      }
      cursor.close();
    } else {
      tableExist = false;
    }
    if (!tableExist) {
      db.execSQL(mTableInfo.getCreateRequest());
    }
  }

  public long save(T object) {
    if (object == null) {
      throw new NullPointerException("Can't save null object");
    }
    SQLiteDatabase db = openHelper.getWritableDatabase();
    ContentValues values = new ContentValues();
    for (Field field : mTableInfo.getFields()) {
      if (mTableInfo.getObjectFields().contains(field)) {
        // Let's save the subobject first
        try {
          Class childClass = field.getType();
          BaseAdapter childAdapter = adapterForClass(mContext, field.getType());
          Object child = field.get(object);
          @SuppressWarnings("unchecked")
          long childId = child == null ? -1 : childAdapter.save(child);

          String fieldName = mTableInfo.getColumn(field).name;
          values.put(fieldName, childId);
        } catch (IllegalAccessException e) {
          e.printStackTrace();
        }
      } else {
        putValue(field, object, values);
      }
    }

    long insertId = -1;
    try {
      if (mTableInfo.hasPrimaryKey()) {
        insertId = mTableInfo.primaryKey.field.getLong(object);
      }
      if (insertId > 0) {
        db.update(mTableInfo.getName(), values, mTableInfo.primaryKey.name + " = ?",
                  new String[]{String.valueOf(insertId)});
      } else {
        insertId = db.insert(mTableInfo.getName(), null, values);
        if (mTableInfo.hasPrimaryKey()) {
          mTableInfo.primaryKey.field.setLong(object, insertId);
        }
      }
    } catch (IllegalAccessException e) {
      Log.e("BaseAdapter", "Couldn't set id", e);
    }

    return insertId;
  }

  private T getWithId(final long id, @Nullable final Object parent) {
    SQLiteDatabase db = openHelper.getWritableDatabase();
    Cursor
        cursor =
        db.query(mTableInfo.getName(), mTableInfo.getColumnNames(),
                 mTableInfo.primaryKey.name + " = ?", new String[]{String.valueOf(id)}, null, null,
                 null, null);
    if (cursor != null) {
      try {
        cursor.moveToFirst();

        return createObjectFromCursor(cursor, parent);
      } finally {
        cursor.close();
      }
    }

    return null;
  }

  public T getWithId(final long id) {
    return getWithId(id, null);
  }


  public List<T> getAll() {
    SQLiteDatabase db = openHelper.getWritableDatabase();
    Cursor
        cursor =
        db.query(mTableInfo.getName(), mTableInfo.getColumnNames(), null, null, null, null, null,
                 null);
    if (cursor != null) {
      if (cursor.moveToFirst()) {
        List<T> objects = new ArrayList<>(cursor.getCount());

        do {
          objects.add(createObjectFromCursor(cursor, null));
        } while (cursor.moveToNext());

        cursor.close();
        return objects;
      } else {
        cursor.close();
      }
    }

    return new ArrayList<>(0);
  }

  public boolean delete(T object) {
    SQLiteDatabase db = openHelper.getWritableDatabase();
    if (mTableInfo.hasPrimaryKey()) {
      try {
        long id = mTableInfo.primaryKey.field.getLong(object);
        if (db.delete(mTableInfo.getName(), mTableInfo.primaryKey.name + " = ?",
                      new String[]{String.valueOf(id)}) > 0) {
          mTableInfo.primaryKey.field.setLong(object, -1);
          return true;
        }
      } catch (IllegalAccessException e) {
        Log.e("BaseAdapter", "Couldn't get primary key for object " + object, e);
      }
    }
    return false;
  }

  public int clear() {
    SQLiteDatabase db = openHelper.getWritableDatabase();
    return db.delete(mTableInfo.getName(), "1", null);
  }

  private T createObjectFromCursor(final Cursor cursor, @Nullable final Object parent) {
    try {
      Class<?> declaringClass = mTableInfo.getInfoClass().getDeclaringClass();
      T object;
      if (declaringClass != null && !Modifier.isStatic(mTableInfo.getInfoClass().getModifiers())) {
        // We are an inner class, not static;
        try {
          // TODO need a link to the declaring instance
          object =
              mTableInfo.getInfoClass().getDeclaredConstructor(declaringClass).newInstance(parent);
        } catch (InvocationTargetException | NoSuchMethodException e) {
          e.printStackTrace();
          object = null;
        }
      } else {
        object = mTableInfo.getInfoClass().newInstance();
      }
      if (object == null) {
        return null;
      }
      String[] columnNames = mTableInfo.getColumnNames();
      Field[] fields = mTableInfo.getAllFields();
      for (int columnIndex = 0; columnIndex < columnNames.length; columnIndex++) {
        Field field = fields[columnIndex];
        TableInfo.Column column = mTableInfo.getColumn(field);
        Class<?> fieldType = field.getType();

        boolean columnIsNull = cursor.isNull(columnIndex);
        Object value = null;

        if (columnIsNull) {

        } else if (fieldType.equals(Byte.class) || fieldType.equals(byte.class)) {
          value = cursor.getInt(columnIndex);
        } else if (fieldType.equals(Short.class) || fieldType.equals(short.class)) {
          value = cursor.getInt(columnIndex);
        } else if (fieldType.equals(Integer.class) || fieldType.equals(int.class)) {
          value = cursor.getInt(columnIndex);
        } else if (fieldType.equals(Long.class) || fieldType.equals(long.class)) {
          value = cursor.getLong(columnIndex);
        } else if (fieldType.equals(Float.class) || fieldType.equals(float.class)) {
          value = cursor.getFloat(columnIndex);
        } else if (fieldType.equals(Double.class) || fieldType.equals(double.class)) {
          value = cursor.getDouble(columnIndex);
        } else if (fieldType.equals(Boolean.class) || fieldType.equals(boolean.class)) {
          value = cursor.getInt(columnIndex) != 0;
        } else if (fieldType.equals(Character.class) || fieldType.equals(char.class)) {
          value = cursor.getString(columnIndex).charAt(0);
        } else if (fieldType.equals(String.class)) {
          value = cursor.getString(columnIndex);
        } else if (fieldType.equals(Byte[].class) || fieldType.equals(byte[].class)) {
          value = cursor.getBlob(columnIndex);
        } else if (mTableInfo.getObjectFields().contains(field)) {
          long childId = cursor.getLong(columnIndex);
          if (childId != -1) {
            Object child = adapterForClass(mContext, fieldType).getWithId(childId, object);
            if (child != null) {
              value = child;
            }
          }
        }

        if (value != null) {
          field.set(object, value);
        }
      }
      return object;
    } catch (InstantiationException | IllegalAccessException e) {
      e.printStackTrace();
    }

    return null;
  }

  private void putValue(Field field, T object, ContentValues values) {
    try {
      Object value = field.get(object);
      Class<?> fieldType = field.getType();
      String fieldName = mTableInfo.getColumn(field).name;

      if (value == null) {
        values.putNull(fieldName);
      } else if (fieldType.equals(Byte.class) || fieldType.equals(byte.class)) {
        values.put(fieldName, (Byte) value);
      } else if (fieldType.equals(Short.class) || fieldType.equals(short.class)) {
        values.put(fieldName, (Short) value);
      } else if (fieldType.equals(Integer.class) || fieldType.equals(int.class)) {
        values.put(fieldName, (Integer) value);
      } else if (fieldType.equals(Long.class) || fieldType.equals(long.class)) {
        values.put(fieldName, (Long) value);
      } else if (fieldType.equals(Float.class) || fieldType.equals(float.class)) {
        values.put(fieldName, (Float) value);
      } else if (fieldType.equals(Double.class) || fieldType.equals(double.class)) {
        values.put(fieldName, (Double) value);
      } else if (fieldType.equals(Boolean.class) || fieldType.equals(boolean.class)) {
        values.put(fieldName, (Boolean) value);
      } else if (fieldType.equals(Character.class) || fieldType.equals(char.class)) {
        values.put(fieldName, value.toString());
      } else if (fieldType.equals(String.class)) {
        values.put(fieldName, value.toString());
      } else if (fieldType.equals(Byte[].class) || fieldType.equals(byte[].class)) {
        values.put(fieldName, (byte[]) value);
      } else {
        Log.d("BaseAdapter", "Unsupported type " + fieldType.toString());
      }
    } catch (IllegalArgumentException | IllegalAccessException e) {
      Log.e("BaseAdapter", "Error", e);
    }
  }


  static class SharedOpenHelper extends SQLiteOpenHelper {

    private static String
        CREATE_VERSION =
        "CREATE TABLE versions (class_name TEXT PRIMARY KEY NOT NULL, version INTEGER);";

    public SharedOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                            int version) {
      super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
      db.execSQL(CREATE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long getSavedClassVersion(TableInfo<?> tableInfo) {
      String className = tableInfo.getName();
      SQLiteDatabase db = getReadableDatabase();

      Cursor
          cursor =
          db.query("versions", new String[]{"version"}, "class_name = ?", new String[]{className},
                   null, null, null, null);

      if (cursor != null) {
        long versionNumber;
        if (cursor.getCount() > 0) {
          cursor.moveToFirst();
          versionNumber = cursor.getLong(0);
        } else {
          versionNumber = 1;
        }

        cursor.close();
        return versionNumber;
      } else {
        return 1;
      }

    }
  }

  public static SharedOpenHelper getOpenHelper() {
    return openHelper;
  }

  private final static Map<Class<?>, BaseAdapter<?>> baseAdapterMap = new HashMap<>();

  public static <T> BaseAdapter<T> adapterForClass(Context context, Class<T> tClass) {
    BaseAdapter<?> adapter = baseAdapterMap.get(tClass);
    if (adapter == null) {
      adapter = new BaseAdapter<>(context, new TableInfo<>(tClass));
      baseAdapterMap.put(tClass, adapter);
    }

    @SuppressWarnings("unchecked")
    BaseAdapter<T> returnAdapter = (BaseAdapter<T>) adapter;

    return returnAdapter;
  }


}
