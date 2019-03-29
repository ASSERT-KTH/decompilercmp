
`org/apache/commons/codec/digest/PureJavaCrc32:53`

```java
  public long getValue() {
    return (~crc) & 0xffffffffL;
  }
```

becomes

```java
  public long getValue()
  {
    return (crc ^ 0xFFFFFFFF) & 0xFFFFFFFF;
  }
```

In particular `0xffffffffL` becomes `0xFFFFFFFF`. (`-1` and `4294967295`).
In the errored version `0xFFFFFFFF` becomes `-1` and then is cast into `long` (and stays `-1`)., 
wheras in the original `0xffffffffL` is directly interrpreted as a `long` (`4294967295`).

