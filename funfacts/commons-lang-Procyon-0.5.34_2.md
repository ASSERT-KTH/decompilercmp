
In `org/apache/commons/lang3/time/FastDatePrinter:557`

```java
    protected StringBuffer applyRules(final Calendar calendar, final StringBuffer buf) {
        return (StringBuffer) applyRules(calendar, (Appendable) buf);
    }

    /**
     * <p>Performs the formatting by applying the rules to the
     * specified calendar.</p>;
     *
     * @param calendar  the calendar to format
     * @param buf  the buffer to format into
     * @param <B> the Appendable class type, usually StringBuilder or StringBuffer.
     * @return the specified string buffer
     */
    private <B extends Appendable> B applyRules(final Calendar calendar, final B buf) {
        try {
            for (final Rule rule : mRules) {
                rule.appendTo(buf, calendar);
            }
        } catch (final IOException ioe) {
            ExceptionUtils.rethrow(ioe);
        }
        return buf;
    }
```

compiled with javac and decompiled with Procyon-0.5.34 becomes

```java
    protected StringBuffer applyRules(final Calendar calendar, final StringBuffer buf) {
        return this.applyRules(calendar, buf);
    }

    private <B extends Appendable> B applyRules(final Calendar calendar, final B buf) {
        try {
            for (final Rule rule : this.mRules) {
                rule.appendTo(buf, calendar);
            }
        }
        catch (IOException ioe) {
            ExceptionUtils.rethrow(ioe);
        }
        return buf;
    }
```

Note that in the decompiled version `StringBuffer applyRules(final Calendar calendar, final StringBuffer buf)` no longer delegate to `<B extends Appendable> B applyRules(final Calendar calendar, final B buf)` and therefor cause a StackOverflow when called.

Reported [here](https://bitbucket.org/mstrobel/procyon/issues/343/incorrect-interface-down-casting).
