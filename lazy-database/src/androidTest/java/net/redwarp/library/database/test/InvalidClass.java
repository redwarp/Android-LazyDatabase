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

import net.redwarp.library.database.annotation.PrimaryKey;

public class InvalidClass {
  public InvalidClass(String string){

  }

  public class InvalidInnerClass{
    @PrimaryKey long id;

    public InvalidInnerClass(Object osef){
      // Testing for missing constructor here.
    }
  }
}
