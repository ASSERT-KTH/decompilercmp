## Decompiler Comparator

The aim of this project is compare the different java decompiler currently available. To do so it use a java maven project for which the sources are provided and attempt to decompile recompile classes one by one.

Prerequisites:
 * run [yajta](https://github.com/castor-software/yajta) in Tie mode to collect information on which tests to run for each class. (Put the `tie-report.json` in the root of the targeted project).

Usage:

```bash
# Run decomiler -> Ast diff -> compiler -> test on each class
java -cp decompilercmp.jar se.kth.DecompilerComparator -p /path/to/test/project -d DecompilerName

# Run decomiler -> Ast diff -> compiler -> test on a single class
java -cp decompilercmp.jar se.kth.DecompilerComparator -p /path/to/test/project -d DecompilerName -c org/mypackage/MyClass
```

Output (`projectName-decompiler-report.csv`):

```csv
Class,isDecompilable,distanceToOriginal,isRecompilable,passTests
```

*Class*: Class name
*isDecompilable*: has the decompiler sucessuflly produced a java file for the given class?
*distanceToOriginal*: Number of edit operation needed on the original AST to obtain the decompiled AST. (Integer.MIN_VALUE if the evaluation failed)
*isRecompilable*: can the decompiled java code be recompiled?
*passTests*: do the tests of the project still pass after decompilation/recompilation of the class.


AST comparator is shamelessly copied from [gumtree-spoon-ast-diff](https://github.com/SpoonLabs/gumtree-spoon-ast-diff).