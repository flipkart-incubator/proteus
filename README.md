Proteus : Android Layout Engine
=====================

An android library for achieving granular control over the visual appearance and data persistence over views/widgets displayed in any mobile app. Meant to be a drop in replacement for androids LayoutInflater which allows usage of layouts sent from server instead of using the bundled xml layouts.

Usage:

```java
JsonObject layout = new JsonObject();   // this layout is the layout sent from server
LayoutBuilder builder = new DefaultLayoutBuilder().createSimpleLayoutBuilder(this);

ViewGroup parentViewGroup = (ViewGroup)this.getWindow().getDecorView();
ProteusView proteusView = builder.build(parentViewGroup, layout, null);

// To get the View call the ProteusView.getView()
// now you have a dynamic view which can be added to decorview
View view = proteusView.getView();
```

Builder types
=============
By default 3 types of layout builders are bundled.

 1. Simple
 2. DataParsing
 3. DataAndViewParsing

SimpleLayoutBuilder
-------------------
This is a layout builder which can parse json to construct an android view out of it. It uses the registered handlers to convert the json string to a view and then assign attributes. You can also assign a callback to get callbacks for unknown views and unknown attributes.

Example :

```java
LayoutBuilder builder = new DefaultLayoutBuilder().createSimpleLayoutBuilder(this);
ProteusView proteusView = builder.build(parentViewGroup, layoutJsonObject, null);
View view = proteusView.getView();
```

DataParsingLayoutBuilder
------------------------
A layout builder built on top of simple layout builder which can additionally parse data blocks. What this does is that any attribute value starting with "$" as the prefix will be considered as a data block and will be retrieved from the Data Provider.

Example :

```java
LayoutBuilder builder = new DefaultLayoutBuilderFactory().createDataParsingLayoutBuilder(MainActivity.this, new GsonProvider(dataJsonObject));

ProteusView proteusView = builder.build(parentViewGroup, layoutJsonObject, null);
// or
ProteusView proteusView = builder.build(parentViewGroup, layoutJsonObject, newDataJsonObject);

View view = proteusView.getView();
```

DataAndViewParsingLayoutBuilder
------------------------
A layout builder built on top of data parsing layout builder which can make views reusable. What this means is that any view type which is not present in the built in list of views will be queried in the View Provider (third param in the constructor) and will be inserted accordingly. This is primarily useful when using a "childView" property combined with "children" being a data block which means that the children are dynamic and have to be fetched from data provider and the type of the every child will be specified by "childView" and fetched from view provider.

Example :

```java
LayoutBuilder builder = new DefaultLayoutBuilderFactory().createDataParsingLayoutBuilder(MainActivity.this,
    new GsonProvider(dataJsonObject),
    new GsonProvider(layoutJsonObject));

ProteusView proteusView = builder.build(parentViewGroup, layoutJsonObject, null);
// or
ProteusView proteusView = builder.build(parentViewGroup, layoutJsonObject, newDataJsonObject);

View view = proteusView.getView();
```

One click XML to JSON conversion
--------------------------------
Download [this plugin](https://github.com/Flipkart/android-studio-layoutengine-plugin/blob/master/Plugin/Plugin.jar) for android studio and enable it. Once enabled, you can select any android XML layout file and go to **Tools > Convert XML to JSON**

Readme TODO :
-------------

 1. Example JSON for each builder
 2. Example for Callback usage, unknown view handling
 3. Example use cases with screenshots of views
 4. Primary purpose of the library
 5. Sample project compiling

