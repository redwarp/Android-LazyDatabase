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

import java.lang.reflect.Field;
import java.util.HashMap;

/**
 * Class used to convert primitive type to Object, and vice versa.
 */
public class SQLiteUtils {

  public static SQLiteType getSqlLiteTypeForField(Field field) {
    return TYPE_MAP.get(field.getType());
  }

  public enum SQLiteType {
    INTEGER, REAL, TEXT, BLOB
  }

  private static final HashMap<Class<?>, SQLiteType>
      TYPE_MAP =
      new HashMap<Class<?>, SQLiteType>() {
        {
          put(byte.class, SQLiteType.INTEGER);
          put(short.class, SQLiteType.INTEGER);
          put(int.class, SQLiteType.INTEGER);
          put(long.class, SQLiteType.INTEGER);
          put(float.class, SQLiteType.REAL);
          put(double.class, SQLiteType.REAL);
          put(boolean.class, SQLiteType.INTEGER);
          put(char.class, SQLiteType.TEXT);
          put(byte[].class, SQLiteType.BLOB);
          put(Byte.class, SQLiteType.INTEGER);
          put(Short.class, SQLiteType.INTEGER);
          put(Integer.class, SQLiteType.INTEGER);
          put(Long.class, SQLiteType.INTEGER);
          put(Float.class, SQLiteType.REAL);
          put(Double.class, SQLiteType.REAL);
          put(Boolean.class, SQLiteType.INTEGER);
          put(Character.class, SQLiteType.TEXT);
          put(String.class, SQLiteType.TEXT);
          put(Byte[].class, SQLiteType.BLOB);
        }
      };
}
