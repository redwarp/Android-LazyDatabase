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

package net.redwarp.library.testapplication.tools;

import java.util.Random;

/**
 * Created by Redwarp on 29/05/2015.
 */
public class NameGenerator {

  private static String[] vocals = {"a", "e", "i", "o", "u", "ou", "oi"};
  private static char[] consonants = {'b', 'c', 'd', 'f', 'g', 'h', 'k',
                                      'l', 'm', 'n', 'p', 'q', 'r', 's', 't', 'v', 'w', 'x', 'z'};

  private Random mRandom;

  public NameGenerator() {
    mRandom = new Random();
  }

  public NameGenerator(long seed) {
    mRandom = new Random(seed);
  }

  public String next() {
    int nameLength = mRandom.nextInt(7) + 4;
    StringBuilder builder = new StringBuilder(nameLength);
    int consecutiveConsonants = 1;
    int vocalsLength = vocals.length;
    int consonantsLength = consonants.length;
    for (int index = 0; index < nameLength; index++) {
      if (consecutiveConsonants > 2) {
        consecutiveConsonants = 0;
        builder.append(vocals[mRandom.nextInt(vocalsLength)]);
      } else if (consecutiveConsonants == 0) {
        consecutiveConsonants++;
        builder.append(consonants[mRandom.nextInt(consonantsLength)]);
      } else {
        if (mRandom.nextBoolean()) {
          // Add a consonant
          consecutiveConsonants++;
          builder.append(consonants[mRandom.nextInt(consonantsLength)]);
        } else {
          consecutiveConsonants = 0;
          builder.append(vocals[mRandom.nextInt(vocalsLength)]);
        }
      }
    }
    builder.replace(0, 1, builder.substring(0, 1).toUpperCase());

    return builder.toString();
  }
}
