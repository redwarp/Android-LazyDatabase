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

package net.redwarp.library.database.test;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import net.redwarp.library.database.DatabaseHelper;
import net.redwarp.library.database.TableInfo;

import java.util.List;

/**
 * Created by Benoit Vermont on 16/03/15.
 */
public class DoTest extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
  }

  public void test() {
    TableInfo<Test> testTableInfo = new TableInfo<>(Test.class);
    String create = testTableInfo.getCreateRequest();

    Test test = new Test();
    test.length = 19;
    test.randomReal = Math.PI;

    DatabaseHelper helper = new DatabaseHelper(this);
    boolean didSave = helper.save(test);

    Log.d(DoTest.class.getSimpleName(), "Did save " + didSave);

    long id;
    if (didSave) {
      id = test.id;
    } else {
      id = 1;
    }

    Test newTest = helper.getWithId(Test.class, id);
    Log.d(DoTest.class.getSimpleName(), "Did load " + newTest);

    List<Test> allTests = helper.getAll(Test.class);

    Log.d(DoTest.class.getSimpleName(), "Number of tests: " + allTests.size());

    MyClass[] myClasseArray = new MyClass[100];
    for (int i = 0; i < 100; i++) {
      myClasseArray[i] = new MyClass();
    }

    long time;
    long startTime = System.currentTimeMillis();

    helper.beginTransaction();
    try {
      for (int i = 0; i < 100; i++) {
        helper.save(myClasseArray[i]);
      }
      helper.setTransactionSuccessful();
    } finally {
      helper.endTransaction();
    }
    time = System.currentTimeMillis() - startTime;

    Log.d(DoTest.class.getSimpleName(), "Writing stuff (It took " + time + " milliseconds");

    startTime = System.currentTimeMillis();
    List<MyClass> myClasses = helper.getAll(MyClass.class);
    time = System.currentTimeMillis() - startTime;

    Log.d(DoTest.class.getSimpleName(),
          "Number of class: " + myClasses.size() + " (It took " + time + " milliseconds");
  }
}
