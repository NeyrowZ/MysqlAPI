# MysqlAPI

This project is an API that you can use in every **Spigot Plugin** project that require **Mysql Database** implentation.

## Maven:

You first have to add it into your maven dependencies.

***pom.xml***
```xml
<dependencies>
    <dependency>
        <groupId>net.neyrowz</groupId>
        <artifactId>MysqlAPI</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```

## Usage:

Then you have to create a class for the user info.

***PLAYER_CLASS.java***
```java
public class PLAYER_CLASS {

    private final UUID a;
    private final String b;
    private int c;
    @Exclude private String d;

    public TestPlayer(UUID a, String b, int c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }
}
```

> Note that a database credentials file will be created to the plugin folder. 

Last you have to do an instance of the API and a getter so you can access it from everywhere.

***Main.java***
```java
public class Main extends JavaPlugin {

    private static Mysql<PLAYER_CLASS> mysql;

    public void onEnable() {
        mysql = new Mysql<>(this, PLAYER_CLASS.class);
        mysql.setupTable();
    }

    public static Mysql<PLAYER_CLASS> getMysql() {
        return mysql;
    }
}
```
