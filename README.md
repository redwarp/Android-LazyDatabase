# Android-LazyDatabase
A fast way to store POJO in sqlite on an Android device without troubling yourself with database creation.

## What is it?
If you are working on a proof of concept app for Android (you should probably not use it for production in it's current state), and you need to store some good old POJO, this library is there for you.
As the title says, it's a database for lazy people.

## Usage

### for Maven

```
<dependency>
    <groupId>net.redwarp.android.library</groupId>
    <artifactId>lazy-database</artifactId>
    <version>{latest-version}</version>
    <type>aar</type>
</dependency>
```


### for Gradle

```
compile 'net.redwarp.android.library:lazy-database:0.1.0'
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

First, modify it by setting a primary key, like that:

```java
import net.redwarp.library.database.annotation.PrimaryKey;

public class GoodOldPojo {
  @PrimaryKey
  public long key;
  public String name;
  private int randomNumber;
  private float someValue;

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
