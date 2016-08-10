<h1>
  <img src="https://github.com/flipkart-incubator/proteus/blob/master/assets/proteus-logo.png" width="150px">
  : Android Layout Engine
  <a href="https://travis-ci.org/flipkart-incubator/proteus.svg?branch=master">
    <img src="https://travis-ci.org/flipkart-incubator/proteus.svg?branch=master" alt="Build Status">
  </a>
</h1>

**Proteus** is meant to be a drop-in replacement for Android’s `LayoutInflater`; but unlike the compiled XML layouts bundled in the APK, Proteus inflates layouts at runtime.
With Proteus, you can control your Apps layout from the backend (no WebViews). Forget the boilerplate code to `findViewById`, cast it to a `TextView`, and then `setText()`. Proteus has runtime data bindings and formatters. Plugin in your own custom views and attributes and formatters.

* **[Getting started](#getting-started)**
* **[How it Works](#how-it-works)**
* **[Contributing](#contributing)**
* **[License](#license)**

## Getting Started

#### gradle

```javascript
// Add it in your root build.gradle at the end of repositories:
allprojects {
	repositories {
		...
		maven { url "https://jitpack.io" }
	}
}

// Add the dependency
dependencies {
        compile 'com.github.flipkart-incubator:proteus:4.0.0-RC2'
}
```

#### Include as a module

* Clone the **proteus** in the project folder

```javascript
git clone https://github.com/flipkart-incubator/proteus.git
```

* Include the a project in you apps `build.gradle` file

```javascript
dependencies {
  compile project('proteus:library')
}
```

## How it works

Instead of writing layouts in `XML`, in **proteus** layouts are defined in `JSON`, which can be used to inflate native android UI at runtime. The `JSON` layouts can be hosted anywhere (on the device, on servers, etc.).

Forget the boilerplate code to `findViewById`, cast it to a `TextView`, and then `setText()`. **Proteus** has built-in [runtime data bindings](https://github.com/flipkart-incubator/proteus/wiki/Data-Bindings) and formatters. Use data bindings within the `JSON` layouts itself. You can even plugin in your custom views and attributes.

#### layout

```javascript
{
  "type": "LinearLayout",
  "orientation": "vertical",
  "children": [
    {
      "type": "TextView",
      "text": "$user.profile.name"
    },
    {
      "type": "ImageView",
      "src": "$user.profile.imageUrl"
    }
  ]
}
```

#### data

```javascript
{
  "user": {
    "profile": {
      "name": "John Doe",
      "imageUrl": "https://example.com/image.jpg"
    }
  }
}
```

```java
ProteusView view = layoutbuilder.build(parent, layout, data, null, 0);
```

Head over to the [wiki](https://github.com/flipkart-incubator/proteus/wiki) for detailed documentation

## Logging Support

The library provides support to enable/disable logging anytime with a single line of code. By default, logging is **disabled** in the library.

To enable logging, call `ProteusConstants.setIsLoggingEnabled(true)`.

## Contributing

1. Fork the repo
2. Apply your changes
3. Submit your pull request

## License

[Apache v2.0](https://github.com/flipkart-incubator/proteus/blob/master/LICENSE)

## One click XML to JSON conversion plugin

Download [this plugin](https://github.com/flipkart-incubator/android-studio-proteus-plugin/blob/master/Plugin/Plugin.jar) for android studio and enable it. Once enabled, you can select any android XML layout file and go to **Tools > Convert XML to JSON**
