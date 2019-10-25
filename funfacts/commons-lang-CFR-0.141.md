In `org/apache/commons/lang3/LocaleUtils:292`

```java
	private static final ConcurrentMap<String, List<Locale>> cLanguagesByCountry =
        new ConcurrentHashMap<>();
	
	[...]
    public static List<Locale> languagesByCountry(final String countryCode) {
        if (countryCode == null) {
            return Collections.emptyList();
        }
        List<Locale> langs = cLanguagesByCountry.get(countryCode);
        if (langs == null) {
            langs = new ArrayList<>();
            final List<Locale> locales = availableLocaleList();
            for (final Locale locale : locales) {
                if (countryCode.equals(locale.getCountry()) &&
                    locale.getVariant().isEmpty()) {
                    langs.add(locale);
                }
            }
            langs = Collections.unmodifiableList(langs);
            cLanguagesByCountry.putIfAbsent(countryCode, langs);
            langs = cLanguagesByCountry.get(countryCode);
        }
        return langs;
    }
```

becomes

```java
    public static List<Locale> languagesByCountry(String countryCode) {
        if (countryCode == null) {
            return Collections.emptyList();
        }
        List<Locale> langs = (ArrayList)cLanguagesByCountry.get(countryCode);
        if (langs == null) {
            langs = new ArrayList();
            List<Locale> locales = LocaleUtils.availableLocaleList();
            for (Locale locale : locales) {
                if (!countryCode.equals(locale.getCountry()) || !locale.getVariant().isEmpty()) continue;
                langs.add(locale);
            }
            langs = Collections.unmodifiableList(langs);
            cLanguagesByCountry.putIfAbsent(countryCode, langs);
            langs = (List)cLanguagesByCountry.get(countryCode);
        }
        return langs;
    }
```

When `cLanguagesByCountry.get(countryCode)` is not an `ArrayList`, the cast  `(ArrayList)cLanguagesByCountry.get(countryCode)` fails. 

Reported [here](https://github.com/leibnitz27/cfr/issues/6).
