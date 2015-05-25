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

import net.redwarp.library.database.annotation.Chain;
import net.redwarp.library.database.annotation.PrimaryKey;
import net.redwarp.library.database.annotation.Version;

import org.apache.commons.lang3.RandomStringUtils;

import java.io.Serializable;

@Version(2L)
public class Test implements Serializable {

  static final long serialVersionUID = 42L;

  @PrimaryKey
  public long id;
  private String mName;
  private String mValue;
  public int size;
  public long length;
  public double randomReal;

  @Chain
  public TestLink link = new TestLink();
  @Chain
  public InnerTest innerTest = new InnerTest();

  public class InnerTest {

    @PrimaryKey
    long innerId;
    private String randomString = RandomStringUtils.randomAlphabetic(24);

    public InnerTest() {
    }
  }
}
