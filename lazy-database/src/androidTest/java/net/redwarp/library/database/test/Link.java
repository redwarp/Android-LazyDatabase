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

/**
 * Created by Redwarp on 31/05/2015.
 */
@Version(1)
public class Link {

  @PrimaryKey public long key;
  @Chain public Link nextLink;
  public String name;

  public Link() {
  }

  public Link(String name) {
    this.name = name;
  }
}
