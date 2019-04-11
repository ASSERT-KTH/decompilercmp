
In `org/apache/commons/lang3/time/FastDatePrinter:714`

```java
switch (nDigits) {
    case 4:
        buffer.append((char) (value / 1000 + '0'));
        value %= 1000;
    case 3:
        if (value >= 100) {
            buffer.append((char) (value / 100 + '0'));
            value %= 100;
        } else {
            buffer.append('0');
        }
    case 2:
        if (value >= 10) {
            buffer.append((char) (value / 10 + '0'));
            value %= 10;
        } else {
            buffer.append('0');
        }
    case 1:
        buffer.append((char) (value + '0'));
}
```

becomes

```java
Label_0190: {
    switch (nDigits) {
        case 4: {
            buffer.append((char)(value / 1000 + 48));
            value %= 1000;
        }
        case 3: {
            if (value >= 100) {
                buffer.append((char)(value / 100 + 48));
                value %= 100;
                break Label_0190;
            }
            buffer.append('0');
            break Label_0190;
        }
        case 2: {
            if (value >= 10) {
                buffer.append((char)(value / 10 + 48));
                value %= 10;
                break Label_0190;
            }
            buffer.append('0');
            break Label_0190;
        }
        case 1: {
            buffer.append((char)(value + 48));
            break;
        }
    }
}
```

Breaks are introduce at the end of each `case` wheras they did not exist in the original code. This leads to the execution of only two cases instead of 4 when nDigits == 4.

