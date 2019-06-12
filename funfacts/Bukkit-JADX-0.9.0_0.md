
`"org/bukkit/configuration/file/YamlConfiguration:25`

```java
public class YamlConfiguration extends FileConfiguration {
    [...]
    private final DumperOptions yamlOptions = new DumperOptions();
    private final Representer yamlRepresenter = new YamlRepresenter();
    private final Yaml yaml = new Yaml(new YamlConstructor(), yamlRepresenter,  yamlOptions);
    [...]
}
```

becomes

```java
public class YamlConfiguration extends FileConfiguration {
    [...]
    private final Yaml yaml = new Yaml(new YamlConstructor(), this.yamlRepresenter,  this.yamlOptions);
    private final DumperOptions yamlOptions = new DumperOptions();
    private final Representer yamlRepresenter = new YamlRepresenter();
    [...]
}
```

Feld initialization is out of order in the decompiled version leading to a NullPointerException in `new Yaml(new YamlConstructor(), this.yamlRepresenter,  this.yamlOptions)` as `this.yamlRepresenter` and `this.yamlOptions` are still null.

Already reported [here](https://github.com/skylot/jadx/issues/678)
