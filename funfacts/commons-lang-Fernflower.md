
In `org/apache/commons/lang3/mutable/MutableObject:91`


```java
    public static int toIntValue(final char ch) {
        if (!isAsciiNumeric(ch)) {
            throw new IllegalArgumentException("The character " + ch + " is not in the range '0' - '9'");
        }
        return ch - 48;
    }
	[...]
    public static int toIntValue(final Character ch) {
        Validate.isTrue(ch != null, "The character must not be null");
        return toIntValue(ch.charValue());
    }
```

becomes

```java
   public static int toIntValue(char ch) {
      if (!isAsciiNumeric(ch)) {
         throw new IllegalArgumentException("The character " + ch + " is not in the range '0' - '9'");
      } else {
         return ch - 48;
      }
   }
   [...]
   public static int toIntValue(Character ch) {
      Validate.isTrue(ch != null, "The character must not be null", new Object[0]);
      return toIntValue(ch);
   }
```

`return toIntValue(ch.charValue());` has been replace with `return toIntValue(ch);`. The unboxing has been removed causing the function to become recursive, provoking a stack overflow upon calling it.
