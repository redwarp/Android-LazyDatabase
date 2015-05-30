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
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import junit.framework.Assert;

import net.redwarp.library.database.test.MyClass;

/**
 * Created by Redwarp on 30/05/2015.
 */
public class BaseAdapterTest extends AndroidTestCase {

  Context context;
  BaseAdapter<MyClass> adapter;

  @Override
  protected void setUp() throws Exception {
    context = new RenamingDelegatingContext(getContext(), "test_");
    adapter = BaseAdapter.adapterForClass(mContext, MyClass.class);
  }

  @Override
  protected void tearDown() throws Exception {
    adapter.clear();
    BaseAdapter.getOpenHelper().close();
  }

  public void testVersion() {
    long storeVersion = BaseAdapter.getOpenHelper().getSavedClassVersion(adapter.getTableInfo());

    long annotatedVersion = adapter.getTableInfo().getVersion();

    Assert.assertEquals("Versions", storeVersion, annotatedVersion);
  }
}
