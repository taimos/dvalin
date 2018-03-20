## i18n

The `i18n` library adds a simple internationalization and localization support.
The language options can be stored in multiple xml or yaml files, as shown below.
It provides support for standart identifier, enums and translations with parameters.

### Usage
Inject the `II18nAccess` to get access to your localized strings.

If the desired language is not available for your identifier, the default language translation is used (if available).
You can set this language by setting `i18n.locale.default` to your desired language, the default is `en`.
If there is no translation available for your identifier, `II18nAccess` will return `!<ourIdentifierHere>!` instead and log an error message containing the identifier.


### Managing translations
Simply add one or more xml or yaml files with the corresponding format to `src/resources/i18n/`. They will be automatically detected.
You can freely mix between yaml and xml, if you want to (we suggest to stick to one, though).

#### XML FORMAT
```
<i18nBundle>
    <label id="textA">
        <language locale="de" value="TextAGerman" />
        <language locale="en" value="TextAEnglish"/>
    </label>
    <label id="textB">
        <language locale="de" value="TextBGerman" />
        <language locale="en" value="TextBEnglish"/>
        <language locale="it" value="TextBItalian"/>
    </label>
</i18nBundle>
```

To use the inbuild enum support, use the label id with fully qualified enum class names, 
e.g. for an enum with `de.taimos.dvalin.i18n.TestEnum` containing the two fields `FIELD_A` and `FIELD_B` use:
```
<i18nBundle>
     <label id="de.taimos.dvalin.i18n.TestEnum.FIELD_A">
        <language locale="de" value="EnumFieldAGerman" />
        <language locale="en" value="EnumFieldAEnglish"/>
     </label>
     <label id="de.taimos.dvalin.i18n.TestEnum.FIELD_B">
        <language locale="de" value="EnumFieldBGerman" />
        <language locale="en" value="EnumFieldBEnglish"/>
     </label>
</i18nBundle>
```

To use parameters add `{identifier}` to your translation:

```
<i18nBundle>
    <label id="textB">
        <language locale="de" value="TextBGerman {0} {2}" />
        <language locale="en" value="TextBEnglish {0} {2}"/>
    </label>
</i18nBundle>
``` 

#### YAML FORMAT
```
textA:
  de: TextAGerman
  en: TextAEnglish
textB:
  de: TextBGerman
  en: TextBEnglish
```

To use the inbuild enum support, use the label id with fully qualified enum class names, 
e.g. for an enum with `de.taimos.dvalin.i18n.TestEnum` containing the two fields `FIELD_A` and `FIELD_B` use:
```
de.taimos.dvalin.i18n.TestEnum.FIELD_A:
  de: EnumFieldAGerman
  en: EnumFieldAEnglish
de.taimos.dvalin.i18n.TestEnum.FIELD_B:
  de: EnumFieldBGerman
  en: EnumFieldBEnglish
```

To use parameters add `{identifier}` to your translation:

```
textB:
  de: TextBGerman {0} {2}
  en: TextBEnglish {0} {2}
``` 