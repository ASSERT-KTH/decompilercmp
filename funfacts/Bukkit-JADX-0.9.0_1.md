
In `org/bukkit/Bukkit:63`

```java
public final class Bukkit {
    private static Server server;
	[...]
    public static void setServer(Server server) {
        if (Bukkit.server != null) {
            throw new UnsupportedOperationException("Cannot redefine singleton Server");
        }
        Bukkit.server = server;
        server.getLogger().info("This server is running " + getName() + " version " + getVersion() + " (Implementing API version " + getBukkitVersion() + ")");
    }
```

becomes

```java
public final class Bukkit {
    private static Server server;
	[...]
    public static void setServer(Server server) {
        if (server != null) {
            throw new UnsupportedOperationException("Cannot redefine singleton Server");
        }
        server = server;
        server.getLogger().info("This server is running " + getName() + " version " + getVersion() + " (Implementing API version " + getBukkitVersion() + ")");
    }
```

Here the decompiler mistakenly remove the class name `Bukkit` (which would be correct if the parameter didn't have the same name), so the static field reference becomes a parameter reference which trigger the exception.

```bytecode

   public static setServer(Lorg/bukkit/Server;)V
-    ALOAD 0
+    GETSTATIC org/bukkit/Bukkit.server : Lorg/bukkit/Server;
     IFNULL L0
     NEW java/lang/UnsupportedOperationException
     DUP
@@ -810,7 +810,7 @@
     ATHROW
    L0
     ALOAD 0
-    ASTORE 0
+    PUTSTATIC org/bukkit/Bukkit.server : Lorg/bukkit/Server;
     ALOAD 0
     INVOKEINTERFACE org/bukkit/Server.getLogger ()Ljava/util/logging/Logger; (itf)
     NEW java/lang/StringBuilder
```
