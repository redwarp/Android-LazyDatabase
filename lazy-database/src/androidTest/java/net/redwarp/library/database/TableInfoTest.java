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

import junit.framework.Assert;
import junit.framework.TestCase;

import net.redwarp.library.database.test.InvalidClass;
import net.redwarp.library.database.test.Test;

public class TableInfoTest extends TestCase {

  public void testTriggerRequests() {
    TableInfo<Test> tableInfo = TableInfo.getTableInfo(Test.class);

    Assert.assertEquals("Test class contains two chains", 2,
                        tableInfo.getCreateTriggerRequests().size());
  }

  public void testInvalidClass() {
    try {
      TableInfo<InvalidClass> tableInfo = TableInfo.getTableInfo(InvalidClass.class);

      Assert.fail("We should get an exception for class " + InvalidClass.class.getSimpleName());
    } catch (TableInfo.InvalidClassException e) {
      Assert.assertEquals("Testing error formating",
                          "net.redwarp.library.database.test.InvalidClass\n"
                          + " * missing primaryKey field\n"
                          + " * missing empty constructor", e.getLocalizedMessage());
    }

    try {
      TableInfo<InvalidClass.InvalidInnerClass> tableInfo = TableInfo.getTableInfo(InvalidClass.InvalidInnerClass.class);
      Assert.fail("We should get an exception for class " + InvalidClass.InvalidInnerClass.class.getSimpleName());
    } catch (TableInfo.InvalidClassException e){

      Assert.assertEquals("Testing error formating",
                          "net.redwarp.library.database.test.InvalidClass$InvalidInnerClass\n"
                          + " * missing empty constructor", e.getLocalizedMessage());
    }
  }
}
