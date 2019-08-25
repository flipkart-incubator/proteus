<h1>
  <img src="/assets/proteus-logo.png" width="150px"/> : Android Layout Engine
</h1>

<a target="_blank" href="https://travis-ci.org/flipkart-incubator/proteus">
    <img src="https://travis-ci.org/flipkart-incubator/proteus.svg?branch=master" alt="Build Status">
  </a>
<a target="_blank" href="https://jitpack.io/#flipkart-incubator/proteus">
  <img src="https://jitpack.io/v/flipkart-incubator/proteus.svg" alt="Build Status">
</a>
<a href="https://android-arsenal.com/details/1/5105">
  <img src="https://img.shields.io/badge/Android%20Arsenal-Proteus-brightgreen.svg?style=flat" border="0" alt="Android Arsenal">
</a>

**Proteus** is meant to be a drop-in replacement for Android’s `LayoutInflater`; but unlike the compiled XML layouts bundled in the APK, Proteus inflates layouts at runtime.
With Proteus, you can control your Apps layout from the backend (no WebViews). Forget the boilerplate code to `findViewById`, cast it to a `TextView`, and then `setText()`. Proteus has runtime data bindings and formatters. Plugin in your own custom views and attributes and functions to flavour proteus to your requirements.

* **[Getting started](#getting-started)**
* **[How it Works](#how-it-works)**
* **[Resources](#resources)**
* **[Contributing](#contributing)**
* **[License](#license)**
* **[Contributors](#contributors)**
* **[StackOverflow](#stackoverflow)**

## Getting Started

#### gradle (gradle 4.10.*)

```javascript
// Add it in your root build.gradle at the end of repositories:
allprojects {
    repositories {
        google()
        jcenter()
        maven { url 'https://jitpack.io' }
    }
}
```

```
// Add in your app level dependency
dependencies {
    implementation 'com.github.flipkart-incubator.proteus:proteus-core:5.0.1'
    implementation 'com.github.flipkart-incubator.proteus:gson-adapter:5.0.1'
    implementation 'com.github.flipkart-incubator.proteus:cardview-v7:5.0.1'
    implementation 'com.github.flipkart-incubator.proteus:design:5.0.1'
    implementation 'com.github.flipkart-incubator.proteus:recyclerview-v7:5.0.1'
    implementation 'com.github.flipkart-incubator.proteus:support-v4:5.0.1'
}
```

## How it works

Instead of writing layouts in `XML`, in **proteus** layouts are described in `JSON`, which can be used to inflate native Android UI at runtime. The `JSON` layouts can be hosted anywhere (on the device, on servers, etc.).


The [**Layout**](https://github.com/flipkart-incubator/proteus/wiki/Layouts) defines the the view heirarchy, just like XML.

The [**Data**](https://github.com/flipkart-incubator/proteus/wiki/Data) (optional) defines [data bindings](https://github.com/flipkart-incubator/proteus/wiki/Data-Bindings). These data bindings are similar to Android's [Data Binding](https://developer.android.com/topic/libraries/data-binding/index.html) library.

Give the `layout` and `data` to `ProteusLayoutInflater` and get back a native view hierarchy.

Watch [this video](https://www.youtube.com/watch?v=W2Ord1oB72Q&index=1&list=PLIQ3ghGBPsqu0F-OHhKRq2s76vSkdUlJp) to see it in action.

#### Sample layout

```javascript
{
  "type": "LinearLayout",
  "orientation": "vertical",
  "padding": "16dp",
  "children": [{
    "layout_width": "200dp",
    "gravity": "center",
    "type": "TextView",
    "text": "@{user.profile.name}"
  }, {
    "type": "HorizontalProgressBar",
    "layout_width": "200dp",
    "layout_marginTop": "8dp",
    "max": 6000,
    "progress": "@{user.profile.experience}"
  }]
}
```

#### Sample data

```javascript
{
  "user": {
    "profile": {
      "name": "John Doe",
      "experience": 4192
    }
  }
}
```

### Sample Java code

```java
ProteusView view = proteusLayoutInflater.inflate(<layout>, <data>);
container.addView(view.getAsView());
```

#### Output

<img src="/assets/example-small.png" width="360px"/>


### Setting up the Demo App

The demo app will let you play around with proteus as well as help you understand the internals better.

* Install NodeJS [here](https://nodejs.org/en/download/)
* open a terminal
* cd into the project directory
* run `npm start`
* Start an AVD emulator
* Install the Demo App

**Ready to tinker**

* Tinker around with the [layout](https://github.com/adityasharat/proteus-demo/blob/develop/data/layout.json) and [data](https://github.com/adityasharat/proteus-demo/blob/develop/data/user.json)
* Hit the FAB to refresh the app.

## Resources

* [FAQ](https://github.com/flipkart-incubator/proteus/wiki/Frequently-asked-questions)
* [Detailed Guide](https://github.com/flipkart-incubator/proteus/wiki)
* [API References]() *under construction*
* [DroidCon Talk](https://www.youtube.com/watch?v=ue0ax2_18k8)
* [Demo Videos](https://www.youtube.com/playlist?list=PLIQ3ghGBPsqu0F-OHhKRq2s76vSkdUlJp)

## Supported Modules

* Native Android Widgets
* CardView v7
* Android Design Library
* RecyclerView v7
* Android Support v4

## Contributing

### How?

The easiest way to contribute is by [forking the repo](https://help.github.com/articles/fork-a-repo/), making your changes and [creating a pull request](https://help.github.com/articles/creating-a-pull-request/).

### What?

* Adding new Views and Attribute Proccessors.
* Adding new Functions.
* Adding JavaDoc and Wiki.
* Completing TODOs
* Writing unit tests.
* Finding bugs and issues. (submit [here](https://github.com/flipkart-incubator/proteus/issues))
* Fixing bugs and issues.
* Implement performance/benchmarking tools.

## License

[Apache v2.0](LICENSE)

If you are using proteus check out the [can, cannot and must](https://tldrlegal.com/license/apache-license-2.0-(apache-2.0))

## Contributors

You can check out the [contributors here](https://github.com/flipkart-incubator/proteus/graphs/contributors), but if you wish to contact us; just drop in a mail.

* [adityasharat](mailto:adityasharat@gmail.com)
* [thekirankumar](mailto:kiran.kumar@flipkart.com)
* [yasirmhd](mailto:mohammad.yasir@flipkart.com)

## StackOverflow

Find us on [StackOverflow](http://stackoverflow.com) at [proteus](http://stackoverflow.com/questions/tagged/proteus).

## Plugins

### One click XML to JSON conversion plugin

Download [this plugin (in beta)](https://github.com/flipkart-incubator/android-studio-proteus-plugin) for Android Studio. Once enabled, you can select any android XML resource file and go to **Tools > Proteus > Convert XML to JSON**
