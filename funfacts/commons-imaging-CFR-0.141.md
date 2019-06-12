In `org/apache/commons/imaging/formats/jpeg/JpegUtils:50`

```java
    public void traverseJFIF(final ByteSource byteSource, final Visitor visitor)
            throws ImageReadException,
            IOException {
        InputStream is = null;
        boolean canThrow = false;
        try {
            is = byteSource.getInputStream();

            readAndVerifyBytes(is, JpegConstants.SOI,
                    "Not a Valid JPEG File: doesn't begin with 0xffd8");

            int markerCount;
            for (markerCount = 0; true; markerCount++) {
                final byte[] markerBytes = new byte[2];
                do {
                    markerBytes[0] = markerBytes[1];
                    markerBytes[1] = readByte("marker", is,
                            "Could not read marker");
                } while ((0xff & markerBytes[0]) != 0xff
                        || (0xff & markerBytes[1]) == 0xff);
                final int marker = ((0xff & markerBytes[0]) << 8)
                        | (0xff & markerBytes[1]);

                if (marker == JpegConstants.EOI_MARKER || marker == JpegConstants.SOS_MARKER) {
                    if (!visitor.beginSOS()) {
                        canThrow = true;
                        return;
                    }

                    final byte[] imageData = getStreamBytes(is);
                    visitor.visitSOS(marker, markerBytes, imageData);
                    break;
                }

                final byte[] segmentLengthBytes = readBytes("segmentLengthBytes", is, 2, "segmentLengthBytes");
                final int segmentLength = ByteConversions.toUInt16(segmentLengthBytes, getByteOrder());

                final byte[] segmentData = readBytes("Segment Data",
                        is, segmentLength - 2,
                        "Invalid Segment: insufficient data");

                if (!visitor.visitSegment(marker, markerBytes, segmentLength, segmentLengthBytes, segmentData)) {
                    canThrow = true;
                    return;
                }
            }
            
            Debug.debug(Integer.toString(markerCount) + " markers");
            canThrow = true;
        } finally {
            IoUtils.closeQuietly(canThrow, is);
        }
    }
```

becomes:

```java
    public void traverseJFIF(ByteSource byteSource, Visitor visitor) throws ImageReadException, IOException {
        // This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
        // org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [4[UNCONDITIONALDOLOOP]], but top level block is 2[TRYBLOCK]
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:427)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:479)
        // org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:607)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:696)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:184)
        // org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:129)
        // org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:96)
        // org.benf.cfr.reader.entities.Method.analyse(Method.java:397)
        // org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:890)
        // org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:792)
        // org.benf.cfr.reader.Driver.doClass(Driver.java:52)
        // org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:65)
        // org.benf.cfr.reader.Main.main(Main.java:48)
        throw new IllegalStateException("Decompilation failed");
    }
```

CFR choose to replace the body of methods that it knows it cannot decompiled by a `throw new IllegalStateException("Decompilation failed");`.

Reported [here](https://github.com/leibnitz27/cfr/issues/7).
