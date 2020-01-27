## Decompiler Comparator

The aim of this project is compare java decompilers, see [The Strengths and Behavioral Quirks of Java Bytecode Decompilers](http://arxiv.org/pdf/1908.06895) ([doi:10.1109/scam.2019.00019](https://doi.org/10.1109/scam.2019.00019)).

```bibtex
@inproceedings{arXiv-1908.06895,
 title = {The Strengths and Behavioral Quirks of Java Bytecode Decompilers},
 booktitle = {Proceedings of the 19th IEEE International Working Conference on
  Source Code Analysis and Manipulation (SCAM )},
 year = {2019},
 author = {Nicolas Harrand and César Soto-Valero and Martin Monperrus and Benoit Baudry},
 doi = {10.1109/scam.2019.00019},
 url = {http://arxiv.org/pdf/1908.06895},
}
```

## Usage

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

 * **Class**: Class name
 * **isDecompilable**: has the decompiler sucessuflly produced a java file for the given class?
 * **distanceToOriginal**: Number of edit operation needed on the original AST to obtain the decompiled AST. (Integer.MIN_VALUE if the evaluation failed)
 * **nbNodesOriginal**: Number of AST node in the original java source file.
 * **isRecompilable**: can the decompiled java code be recompiled?
 * **bytecodeDistance**: Minimum number of atomic edit to go from the original bytecode to the decompiled/recompiled one. (Computed with [jardiff](https://github.com/scala/jardiff))
 * **passTests**: do the tests of the project still pass after decompilation/recompilation of the class.


AST comparator is shamelessly copied from [gumtree-spoon-ast-diff](https://github.com/SpoonLabs/gumtree-spoon-ast-diff).

Note that if the targeted project supports it, eclipse compiler can be used instead of javac with

```bash
java -cp decompilercmp.jar se.kth.DecompilerComparator -p /path/to/test/project -d DecompilerName -j eclipse
```

Optional: run [yajta](https://github.com/castor-software/yajta) in Tie mode to collect information on which tests to run for each class. (Put the `tie-report.json` in the root of the targeted project).


## Complete command line help

```
Usage: <main class> [options]
  Options:
    --debug-class, -c       Optional. Run a single class
    --decompiler-name, -d   Name of the decompiler to test. See
                            se.kth.Decompiler for more details. Default CFR-0.141
                            Default: CFR-0.141
    --help, -h              Display this message.
                            Default: false
    --java-compiler, -j     Path to output directory. Default: default
                            Default: javac
    --output-dir, -o        Path to output directory. Default: report
                            Default: report
    --project, -p           Directory containing test reports
    --with-tests, -t        Meta decompile with tests
                            Default: false

Available decompilers: 
		Dava-3.3.0
		Procyon-0.5.34
		JADX-0.9.0
		Jode-1.1.2-pre1
		Fernflower
		CFR-0.141
		Krakatau
		JD-GUI-1.4.1
		Arlecchino

```

# Run test on Meta-Decompiler

Flag `-t` can be used to stop only when a decompilation that passes tests is found.

```bash
java -cp decompilercmp.jar se.kth.DecompilerComparator -p /path/to/test/project -d Meta -j eclipse [-t]
```

