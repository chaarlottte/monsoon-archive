# Monsoon 3.0 Client uwu
3.0 recode best xd

## TODO
Check the trello dumbo [redacted]

## Launch/Export/etc Info
Runtime directory: `/jars`

VMOptions: `-Djava.library.path=versions/1.8.9/1.8.9-natives/`

To build a working .jar file, which later can be put to `/versions` in MC folder, you just need to run `mvn clean package` command.

Launch args: `-intentApiKey <yourapikey>` (key is at [redacted])

## Protection info
Because of the way IntentGuard works, all instantiation code MUST go in `wtf.monsoon.Wrapper.init()`. This means modules, commands, EVERYTHING.

## Misc info
PLEASE use the trello! I am using the trello's information to split up payment in relation to how much work you have done (also looking at git commits, etc).

This should go without saying, but please for the love of god NEVER share anything in this repo.

## Code expectations/requirements
Format your brackets like this:
```java
public static void main(String[] args) {
  System.out.println("Correctly formatted!");
}
```
And NOT like this:
```java
public static void main(String[] args) 
{
  System.out.println("Incorrectly formatted! You suck!");
}
```

Please use the least amount of static possible, and use lombok for getters/setters. Static should really only ever be used in utils.

Speaking of utils, please make sure they extend `wtf.monsoon.api.util.Util`.

When adding new dependencies, please notify everyone in the Monsoon Development group chat.

Try to use the logger util (`wtf.monsoon.api.util.Logger`) instead of `System.out.println();`.

Try to document your code, especially utils or anything to do with the base. It's good pratice and helps my (quick's) small brain figure shit out.

When creating a util, start the class like this:
```java
package wtf.monsoon.api.util.whatevercategorytheutilfallsunder;

import wtf.monsoon.api.util.Util;

public class ExampleUtil extends Util {

}
```
