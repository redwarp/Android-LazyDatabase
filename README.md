# Android-LazyDatabase
A fast way to store POJO in sqlite on an Android device without troubling yourself with database creation.

[![Build Status](https://travis-ci.org/redwarp/Android-LazyDatabase.svg?branch=develop)](https://travis-ci.org/redwarp/Android-LazyDatabase) [![Download](https://api.bintray.com/packages/redwarp/android/lazy-database/images/download.svg) ](https://bintray.com/redwarp/android/lazy-database/_latestVersion)

## What is it?
If you are working on a proof of concept app for Android (you should probably not use it for production in it's current state), and you need to store some good old POJO, this library is there for you.
As the title says, it's a database for lazy people.

## ![Warning](readme-assets/warning.png) Warning
Seriously, **don't use it for production**, only for proof of concept or stuff like that. I mean, really, I might break everything with the next release. It's far from being *future proof* yet.

## Usage

### For Maven

```
<dependency>
    <groupId>net.redwarp.android.library</groupId>
    <artifactId>lazy-database</artifactId>
    <version>0.2.2</version>
    <type>aar</type>
</dependency>
```


### For Gradle

```
compile 'net.redwarp.android.library:lazy-database:0.2.2'
```

## How to?
Let's say you have a good old POJO class, like this:

```java
public class GoodOldPojo {
  public String name;
  private int randomNumber;
  private float someValue;

  public GoodOldPojo(float someValue){
    this.someValue = someValue;
  }

  public float getSomeValue() {
    return someValue;
  }
}
```

First, modify it by setting a **primary key**, like that, and add an **empty constructor**:

```java
import net.redwarp.library.database.annotation.PrimaryKey;

public class GoodOldPojo {
  @PrimaryKey
  public long key;
  public String name;
  private int randomNumber;
  private float someValue;

  public GoodOldPojo(){}

  (...)
}

```
You will then have to create a DatabaseHelper object, using a [`Context`](http://developer.android.com/reference/android/content/Context.html), that you will use to save and retrieve all your objects.

```java
import net.redwarp.library.database.DatabaseHelper;

(...)

GoodOldPojo pojo = new GoodOldPojo(0.45f);
DatabaseHelper helper = new DatabaseHelper(context);
```

### Saving
Saving is then straightforward:
```java
helper.save(pojo);
```
It will create the database if it doesn't exist yet, and then save the object, and setting the long `key` to the inserted row value.

### Saving in batch
```java
helper.beginTransaction();
helper.save(pojo1);
helper.save(pojo2);
helper.save(pojo3);
helper.save(pojo4);
helper.setTransactionSuccessful();
helper.endTransaction();
```

### Retrieving all data
```java
List<GoodOldPojo> allPojos = helper.getAll(GoodOldPojo.class);
```

### Retrieving one single POJO

```java
GoodOldPojo retrievedPojo = helper.getWithId(GoodOldPojo.class, 2);
```

### Object count
```java
long count = helper.getCount(GoodOldPojo.class);
```

### Chaining stuff
Let's say you have one POJO containing another POJO, like that:
```java
public class GoodOldPojo {
  private OtherPojo object;
}
```
By default, it won't be saved. If you want it to be saved, you have to add the annotation `@Chain` to the field, like that:
```java
public class GoodOldPojo {
  @Chain private OtherPojo otherPojo;
}
```
And voila, the otherPojo will be saved as well. By default, deleting the first one will also delete the second one. If you don't want the first
item deletion to cascade on the second one, modify your class this way:

```java
public class GoodOldPojo {
  @Chain(delete = false) private OtherPojo otherPojo;
}
```

## What's left to do?

 * [ ] A shit load
 * [x] Clear should also deleted chain elements
 * [ ] Relations of type one to many
 * [ ] Benchmarking (I mean, how fast is it compared to an hand written database)
 * [ ] Search
 * [ ] Unique keyword, etc...
