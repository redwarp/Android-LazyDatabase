package net.redwarp.library.database;

import junit.framework.Assert;
import junit.framework.TestCase;

import net.redwarp.library.database.test.Test;

public class TableInfoTest extends TestCase {

  public void testTriggerRequests() {
    TableInfo<Test> tableInfo = TableInfo.getTableInfo(Test.class);

    Assert.assertEquals("Test class contains two chains", 2, tableInfo.getCreateTriggerRequests().size());
  }
}
