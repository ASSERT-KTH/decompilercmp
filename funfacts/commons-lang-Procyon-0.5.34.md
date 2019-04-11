
In `org/apache/commons/lang3/Conversion:922`

```java
long out = dstInit;
for (int i = 0; i < nBools; i++) {
    final int shift = i + dstPos;
    final long bits = (src[i + srcPos] ? 1L : 0) << shift;
    final long mask = 0x1L << shift;
    out = (out & ~mask) | bits;
}
```

becomes

```java
long out = dstInit;
for (int i = 0; i < nBools; ++i) {
    final int shift = i + dstPos;
    final long bits = (src[i + srcPos] ? 1 : 0) << shift;
    final long mask = 1L << shift;
    out = ((out & ~mask) | bits);
}
```

`(long) 1L << shift` becomes `(long) (1 << shift)`
