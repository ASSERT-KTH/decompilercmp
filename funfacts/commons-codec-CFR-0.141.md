
`org/apache/commons/codec/binary/Hex:105`

```java
for (int i = 0, j = 0; j < len; i++) {
    int f = toDigit(data[j], j) << 4;
    j++;
    f = f | toDigit(data[j], j);
    j++;
    out[i] = (byte) (f & 0xFF);
}
```

becomes

```java
int i = 0;
int j = 0;
while (j < len) {
    int f = Hex.toDigit(data[j], j) << 4;
    ++j;
    out[i] = (byte)((f |= Hex.toDigit(data[++j], j)) & 255);
    ++i;
}
```

instead of `out[i] = (byte)((f |= Hex.toDigit(data[j++], j)) & 255);`
