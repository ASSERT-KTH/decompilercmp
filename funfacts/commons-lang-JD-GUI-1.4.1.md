
In `org/apache/commons/lang3/mutable/MutableObject:91`


```java
@Override
public boolean equals(final Object obj) {
    if (obj == null) {
        return false;
    }
    if (this == obj) {
        return true;
    }
    if (this.getClass() == obj.getClass()) {
        final MutableObject<?> that = (MutableObject<?>) obj;
        return this.value.equals(that.value);
    }
    return false;
}
```

becomes

```java
public boolean equals(Object obj)
  {
    if (obj == null) {
      return false;
    }
    if (this == obj) {
      return true;
    }
    if (getClass() == obj.getClass()) {
      MutableObject<?> that = (MutableObject)obj;
      return value.equals(value);
    }
    return false;
  }
```

`return this.value.equals(that.value);` is replaced with `return value.equals(value);`, `that` has simply diseappeared and value is compared with itself instead of beeing compared with the field of the parameter.
