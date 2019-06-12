
In `org/apache/commons/collections4/map/MultiKeyMap:714`

```java
public boolean removeAll(Object key1) {
    boolean modified = false;
    final MapIterator<MultiKey<? extends K>, V> it = mapIterator();
    while (it.hasNext()) {
	    final MultiKey<? extends K> multi = it.next();
	    if (multi.size() >= 1 &&
			    (key1 == null ? multi.getKey(0) == null : key1.equals(multi.getKey(0)))) {
		    it.remove();
		    modified = true;
	    }
    }
    return modified;
}
```

becomes

```java

public boolean removeAll(Object key1) {
    boolean modified = false;
    MapIterator<MultiKey<? extends K>, V> it = mapIterator();
    while (it.hasNext()) {
        MultiKey<? extends K> multi = (MultiKey) it.next();
        if (multi.size() >= 1) {
            if (key1 == null) {
                if (multi.getKey(0) != null) {
                }
            } else if (!key1.equals(multi.getKey(0))) {
            }
            it.remove();
            modified = true;
        }
    }
    return modified;
}
```

Two problems here:
 1. the condition `key1.equals(multi.getKey(0))` is negated in decompiled code.
 2. the body of the if is put outside of the if. (`it.remove(); modified = true;`)

Reported [here](https://github.com/skylot/jadx/issues/687)
