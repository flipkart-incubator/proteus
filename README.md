# Proteus : Android Layout Engine &nbsp; [![Build Status](https://travis-ci.org/flipkart-incubator/proteus.svg?branch=master)](https://travis-ci.org/flipkart-incubator/proteus)
===

**Proteus** is meant to be a drop-in replacement for Android’s `LayoutInflater`; but unlike the compiled XML layouts bundled in the APK, Proteus inflates layouts at runtime.
With Proteus, you can control your Apps layout from the backend (no WebViews). Forget the boilerplate code to `findViewById`, cast it to a `TextView`, and then `setText()`. Proteus has runtime data bindings and formatters. Plugin in your own custom views, attributes, and formatters.
     
<div style="text-align:center">
  <img alt="proteus logo" title="Proteus"
       src="https://github.com/flipkart-incubator/proteus/blob/master/assets/proteus-logo.png"/>
</div>

* ****
* ***** ****

<table>
  <tr style="border: 0px;">
    <td style="border: 0px;">
      <ul class="task-list">
        <li><a href="#how-it-works">How it works</a></li>
        <li><a href="#installation">Installation</a></li>
        <li><a href="#getting-started">Getting started</a></li>
        <li><a href="#contributing">Contributing</a></li>
        <li><a href="#license">License</a></li>
      </ul>
    </td>
    <td style="width:60%; border: 0px; text-align:right;">
      
    </td>
  </tr>
</table>

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

## Installation

### gradle

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

check it out at [jitpack](https://jitpack.io/#flipkart-incubator/proteus/4.0.0-RC1)

## Getting started

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
