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

import net.redwarp.library.database.annotation.Chain;
import net.redwarp.library.database.annotation.PrimaryKey;
import net.redwarp.library.database.annotation.Version;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @param <T> The type of the object to be stored
 */
public class TableInfo<T> {

  private Class<T> mClass;
  //    private Map<Field, SQLiteUtils.SQLiteType> mFieldMap;
  private Map<Field, Column> mColumns;
  private List<Field> mObjectFields;
  public Column primaryKey = null;
  private String[] mColumnNames;
  private Field[] mFields;
  private long mVersion;

  @SuppressWarnings("unchecked")
  public TableInfo(Class<T> c) {
    mClass = c;

    if (c.isAnnotationPresent(Version.class)) {
      final Version version = c.getAnnotation(Version.class);
      mVersion = version.value();
    } else {
      mVersion = 1;
    }

    Field[] fields = mClass.getDeclaredFields();
//        mFieldMap = new HashMap<>(fields.length);
    mColumns = new HashMap<>(fields.length);
    mObjectFields = new ArrayList<>();

    List<String> columnNames = new ArrayList<>(fields.length);
    List<Field> finalFields = new ArrayList<>(fields.length);

    for (Field field : fields) {
      if (!Modifier.isStatic(field.getModifiers())) {
        SQLiteUtils.SQLiteType type = SQLiteUtils.getSqlLiteTypeForField(field);
        if (type != null) {
          field.setAccessible(true);
          if (field.isAnnotationPresent(PrimaryKey.class)) {
            if (primaryKey != null) {
              throw new RuntimeException("There can be only one primary key");
            }
            final PrimaryKey primaryKeyAnnotation = field.getAnnotation(PrimaryKey.class);
            String name = primaryKeyAnnotation.name();
            if ("".equals(name)) {
              name = getColumnName(field);
            }
            columnNames.add(name);
            primaryKey = new Column(name, field, SQLiteUtils.SQLiteType.INTEGER);
          } else {
            final String name = getColumnName(field);
            columnNames.add(name);
            mColumns.put(field, new Column(name, field, type));
          }

          finalFields.add(field);
        } else {
          // Not a basic field;
          if (field.isAnnotationPresent(Chain.class)) {
            // We must serialize/unserialize this as well
            final String name = getColumnName(field);
            columnNames.add(name);
            Column column = new Column(name, field, SQLiteUtils.SQLiteType.INTEGER);
            mColumns.put(field, column);
            mObjectFields.add(field);
            finalFields.add(field);
          }
        }
      }
    }
    mColumnNames = columnNames.toArray(new String[columnNames.size()]);
    mFields = finalFields.toArray(new Field[finalFields.size()]);
  }

  public String getName() {
    return mClass.getCanonicalName().replace('.', '_');
  }

  public long getVersion() {
    return mVersion;
  }

  public String getCreateRequest() {
    final StringBuilder builder = new StringBuilder("CREATE TABLE ").append(getName()).append(" (");
    List<String> columnList = new ArrayList<>(mColumns.size());

    if (primaryKey != null) {
      columnList.add(getColumnDefinition(primaryKey) + " PRIMARY KEY AUTOINCREMENT");
    }

    for (Field field : mColumns.keySet()) {
      columnList.add(getColumnDefinition(mColumns.get(field)));
    }
    builder.append(StringUtils.join(columnList, ", "));

    builder.append(" );");

    return builder.toString();
  }

  private String getColumnDefinition(Column column) {
    return column.name + " " + column.type;
  }

  private String getColumnName(Field field) {
    return "key_" + field.getName();
  }

  public String getUpdateRequest() {
    return null;
  }

  public boolean hasPrimaryKey() {
    return primaryKey != null;
  }

  public static class Column {

    public Field field;
    public SQLiteUtils.SQLiteType type;
    public String name;


    public Column(String name, Field field, SQLiteUtils.SQLiteType type) {
      this.name = name;
      this.field = field;
      this.type = type;
    }
  }

  public Set<Field> getFields() {
    return mColumns.keySet();
  }

  public List<Field> getObjectFields() {
    return mObjectFields;
  }

  public String[] getColumnNames() {
    return mColumnNames;
  }

  public Field[] getAllFields() {
    return mFields;
  }

  public Column getColumn(Field field) {
    return mColumns.get(field);
  }

  public Class<T> getInfoClass() {
    return mClass;
  }

}
