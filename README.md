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
With Proteus, you can control your Apps layout from the backend (no WebViews). Forget the boilerplate code to `findViewById`, cast it to a `TextView`, and then `setText()`. Proteus has runtime data bindings and formatters. Plugin in your own custom views and attributes and formatters.

* **[Getting started](#getting-started)**
* **[How it Works](#how-it-works)**
* **[Resources](#resources)**
* **[Contributing](#contributing)**
* **[License](#license)**
* **[Contributors](#contributors)**

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
        compile 'com.github.flipkart-incubator:proteus:4.2.0'
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


[The "Layout"](https://github.com/flipkart-incubator/proteus/wiki/Layouts) defines the the view heirarchy, just like XML. 

[The "Data"](https://github.com/flipkart-incubator/proteus/wiki/Data) (optional) defines [data bindings](https://github.com/flipkart-incubator/proteus/wiki/Data-Bindings).

You give the layout and data to LayoutBuilder, you get back a native view. 

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
    "text": "~{{user.profile.name}} ({{user.profile.experience}}$(number))"
  }, {
    "type": "HorizontalProgressBar",
    "layout_width": "200dp",
    "layout_marginTop": "8dp",
    "max": 6000,
    "progress": "$user.profile.experience"
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
ProteusView view = layoutBuilder.build(container, <layout>, <data>, 0, styles);
container.addView((View) view);
```

#### Sample screenshot

<img src="/assets/example-full.png" width="300px"/>


### Setting up the Demo App

The demo app will let you play around with Proteus as well as help you understand the internals better.

* Install NodeJS [here](https://nodejs.org/en/download/)
* open a terminal
* cd into the project directory
* run `npm start`
* Start an AVD emulator
* Install the Demo App

**Ready to tinker**

* Tinker around with the [layout](https://github.com/adityasharat/proteus-demo/blob/master/data/layout.json) and [data](https://github.com/adityasharat/proteus-demo/blob/master/data/user.json)
* Hit the FAB to refresh the app.

## Resources

* [FAQ](https://github.com/flipkart-incubator/proteus/wiki/Frequently-asked-questions)
* [Detailed Guide](https://github.com/flipkart-incubator/proteus/wiki)
* [API References]() *under construction*
* [DroidCon Talk](https://www.youtube.com/watch?v=ue0ax2_18k8)
* [Demo Videos](https://www.youtube.com/playlist?list=PLIQ3ghGBPsqu0F-OHhKRq2s76vSkdUlJp)

## Contributing

1. Fork the repo
2. Apply your changes
3. Submit your pull request

## License

[Apache v2.0](LICENSE)

## Contributors

* [adityasharat](mailto:adityasharat@gmail.com)
* [thekirankumar](mailto:kiran.kumar@flipkart.com)
* [yasirmhd](mailto:mohammad.yasir@flipkart.com)

### One click XML to JSON conversion plugin

Download [this plugin (in beta)](https://github.com/flipkart-incubator/android-studio-proteus-plugin) for Android Studio. Once enabled, you can select any android XML resource file and go to **Tools > Proteus > Convert XML to JSON**
