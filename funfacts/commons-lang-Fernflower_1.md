In `org/apache/commons/lang3/math/NumberUtils:680`

```java
if (hexDigits > 8 || hexDigits == 8 && firstSigDigit > '7') { // too many for an int
    return createLong(str);
}
return createInteger(str);
```

becomes

```java
return (Number)(hexDigits <= 8 && (hexDigits != 8 || lastChar <= '7') ? createInteger(str) : createLong(str));
```

`(hexDigits <= 8 && (hexDigits != 8 || lastChar <= '7') ? createInteger(str) : createLong(str))` is always Long while the original returns an Integer or a Long depending on condition.

Reported by email.
