
In `org/apache/commons/collections4/map/MultiKeyMap:77`

```java
public class MultiKeyMap<K, V> extends AbstractMapDecorator<MultiKey<? extends K>, V>
        implements Serializable, Cloneable {
```

becomes

```java
public class MultiKeyMap extends AbstractMapDecorator implements Serializable, Cloneable {
```

The class loses its genericity in the decompiled version.
