# Annotation Based Configuration Manager (ABCM)

ABCM is an annotation based configuration manager I made for fun. It is pretty easy to understand and to manage.

## Installation

Latest version (replace **@TAG@** with this):
<br>
[![](https://jitpack.io/v/JustRed23/ABCM.svg)](https://jitpack.io/#JustRed23/ABCM)

Using gradle:
```gradle
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    implementation 'com.github.JustRed23:ABCM:@TAG@'
}
```

Using maven:
```xml
<repositories>
	<repository>
	    <id>jitpack.io</id>
	    <url>https://jitpack.io</url>
	</repository>
</repositories>

<dependency>
    <groupId>com.github.JustRed23</groupId>
    <artifactId>ABCM</artifactId>
    <version>@TAG@</version>
</dependency>
```

## Usage

Create a class in your desired package where values should be stored:
```java
package your.packagename.here;

import dev.JustRed23.abcm.ConfigField;
import dev.JustRed23.abcm.Configurable;

@Configurable
public final class TestConfig {

    @ConfigField(defaultValue = "hello, world")
    public static String GREETING;

    @ConfigField(defaultValue = "2022", optional = true)
    public static int YEAR;

    @ConfigField(defaultValue = "true", optional = true)
    public static boolean SPECIAL;
}
```
When a class is created and annotated with @Configurable, the class will be picked up in the scanning process and all the respective values will be set, you can simply call TestConfig.GREETING and it will return a value. But we first have to initialize the configuration manager.

Let's create our main class:
```java
package some.other.pkg;

import dev.JustRed23.abcm.Config;
import dev.JustRed23.abcm.exception.ConfigInitException;

import your.packagename.here.TestConfig;

public class HelloWorld {

    public static void main(String[] args) throws ConfigInitException {
        //We could enable debug messages if you want to see what is happening in the background.
        //Make sure that you have a SLF4J extension installed (ex. Logback classic)
        Config.setDebug(true);

        //Now we add a package that will be scanned by the manager.
        //NOTE: a package will be scanned recursively, you do not need to add every subpackage
        Config.addScannable("your.packagename.here"); //This package was used in the TestConfig example
        Config.addScannable(TestConfig.class.getPackage()); //We can also scan by mentioning the Package instance
        Config.addScannable(TestConfig.class); //We can also just get the package by mentioning the class

        //And then we init.
        Config.init();

        //Let's see what we get when we call a value
        System.out.println(TestConfig.GREETING); //hello, world - unless specified by another value in testconfig.cfg

        //Let's say that I want to add another package to scan, but I already initialized the manager.
        Config.addScannable("interesting.package.name");

        //If the manager was initialized already, and you wanted to add another package, we can call a rescan.
        //This will rescan all packages added by addScannable(String);
        Config.rescan(true);

        //And finally, when we are done we obviously tidy up the place!

        Config.destroy();
        //This will remove all references of parsers, configured items and will then set every setting back to their default value.
        //This will NOT set class fields to null, the @ConfigField annotated fields will still exist.
    }
}
```

Now that our configuration manager is initialized, we should notice that a new folder was created in the current working directory called `config` (we can change this behaviour by setting `Config.setConfigDir(File)`). There should now be one file in that directory, called `testconfig.cfg` and it should look like this:
<br>
![](https://github.com/JustRed23/ABCM/blob/master/DOCS/example.png?raw=true)

The configuration manager always makes config files with the name of the annotated class in all lowercase letters. When a config file exists already the manager will read all values from it and apply them to the @Configurable class.

## Contributing

Pull requests are welcome. For major changes, please open an issue first
to discuss what you would like to change.

Please make sure to update tests as appropriate.

## License

[MIT](https://choosealicense.com/licenses/mit/)