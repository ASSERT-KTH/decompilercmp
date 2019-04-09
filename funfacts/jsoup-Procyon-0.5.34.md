
In `org/jsoup/parser/HtmlTreeBuilder:604`

```java
    void reconstructFormattingElements() {
        int size = formattingElements.size();
        if (size == 0 || formattingElements.getLast() == null || onStack(formattingElements.getLast()))
            return;

        Element entry = formattingElements.getLast();
        int pos = size - 1;
        boolean skip = false;
        while (true) {
            if (pos == 0) { // step 4. if none before, skip to 8
                skip = true;
                break;
            }
            entry = formattingElements.get(--pos); // step 5. one earlier than entry
            if (entry == null || onStack(entry)) // step 6 - neither marker nor on stack
                break; // jump to 8, else continue back to 4
        }
        while(true) {
            if (!skip) // step 7: on later than entry
                entry = formattingElements.get(++pos);
            Validate.notNull(entry); // should not occur, as we break at last element

            // 8. create new element from element, 9 insert into current node, onto stack
            skip = false; // can only skip increment from 4.
            Element newEl = insert(entry.nodeName()); // todo: avoid fostering here?
            // newEl.namespace(entry.namespace()); // todo: namespaces
            newEl.attributes().addAll(entry.attributes());

            // 10. replace entry with new entry
            formattingElements.add(pos, newEl);
            formattingElements.remove(pos + 1);

            // 11
            if (pos == size-1) // if not last entry in list, jump to 7
                break;
        }
    }
```

becomes

```java
    void reconstructFormattingElements() {
        final int size = this.formattingElements.size();
        if (size == 0 || this.formattingElements.getLast() == null || this.onStack(this.formattingElements.getLast())) {
            return;
        }
        Element entry = this.formattingElements.getLast();
        int pos = size - 1;
        boolean skip = false;
        while (true) {
            while (pos != 0) {
                entry = this.formattingElements.get(--pos);
                if (entry == null || this.onStack(entry)) {
                    do {
                        if (!skip) {
                            entry = this.formattingElements.get(++pos);
                        }
                        Validate.notNull(entry);
                        skip = false;
                        final Element newEl = this.insert(entry.nodeName());
                        newEl.attributes().addAll(entry.attributes());
                        this.formattingElements.add(pos, newEl);
                        this.formattingElements.remove(pos + 1);
                    } while (pos != size - 1);
                    return;
                }
            }
            skip = true;
            continue;
        }
    }
```
