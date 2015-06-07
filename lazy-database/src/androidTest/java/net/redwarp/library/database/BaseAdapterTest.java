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

import net.redwarp.library.database.test.Link;
import net.redwarp.library.database.test.MyClass;
import net.redwarp.library.database.test.Test;

import java.util.List;

/**
 * Created by Redwarp on 30/05/2015.
 */
public class BaseAdapterTest extends AndroidTestCase {

  Context context;
  BaseAdapter<MyClass> adapter;
  BaseAdapter<Link> linkAdapter;

  @Override
  protected void setUp() throws Exception {
    context = new RenamingDelegatingContext(getContext(), "test_");
    adapter = BaseAdapter.adapterForClass(mContext, MyClass.class);
    linkAdapter = BaseAdapter.adapterForClass(mContext, Link.class);
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

  public void testChainCreate() {
    linkAdapter.clear();
    Link firstLink = new Link("First");
    Link secondLink = new Link("Second");
    firstLink.nextLink = secondLink;

    linkAdapter.save(firstLink);

    Assert.assertTrue("Second link key should be set", secondLink.key > 0);
  }

  public void testChainDelete() {
    linkAdapter.clear();
    Link firstLink = new Link("First");
    Link secondLink = new Link("Second");
    firstLink.nextLink = secondLink;

    linkAdapter.save(firstLink);
    linkAdapter.delete(firstLink);

    Assert.assertTrue("Second link should be deleted", secondLink.key == -1);
  }


  public void testDeleteLastItemClearsBase(){
    adapter.clear();
    MyClass test = new MyClass();
    adapter.save(test);
    test = adapter.getWithId(test.id);
    Assert.assertNotNull("Getting the \"MyClass\" object should be null", test);
    adapter.delete(test);

    List<MyClass> allTests = adapter.getAll();
    Assert.assertEquals("Should have zero items", 0, allTests.size());
  }
}
