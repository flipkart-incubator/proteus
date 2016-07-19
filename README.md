Proteus : Android Layout Engine
=====================

**Proteus** is an android library for achieving complete control over the visual appearance and data bindings on views/widgets rendered. It's meant to be a drop in replacement for androids' `LayoutInflater`. **proteus** allows inflating layouts at runtime unlike the compiled XML layouts bundled in the APK.

<table>
  <tr style="border: 0px;">
    <td style="border: 0px;">
      <ul class="task-list">
        <li><a href="#how-it-works">How it works</a></li>
        <li><a href="#getting-started">Getting started</a></li>
        <li><a href="#contributing">Contributing</a></li>
        <li><a href="#license">License</a></li>
      </ul>
    </td>
    <td style="width:60%; border: 0px; text-align:right;">
      <img alt="proteus logo" src="https://github.com/flipkart-incubator/proteus/blob/master/assets/proteus-logo.png" width="250px"/>
    </td>
  </tr>
</table>

## How it works

Instead of writing layouts in `XML`, in **proteus** layouts are defined in `JSON`, which can be used to inflate native and android UI at runtime. The `JSON` layouts can be hosted anywhere (on the deive, on servers, etc.).

Forget the boilerplate code to `findViewById`, cast it to a `TextView`, and then `setText()`. **Proteus** has built-in [runtime data bindings](https://github.com/flipkart-incubator/proteus/wiki/Data-Bindings) and formatters. Use data bindings within the `JSON` layouts itseld. You can even plugin in your custom views and attributes.

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

## Getting started

Head over to the [wiki](https://github.com/flipkart-incubator/proteus/wiki) for detailed documentation

## Contributing

1. Fork the repo
2. Apply your changes
3. Submit your pull request

## License

[The MIT License (MIT)](https://github.com/flipkart-incubator/proteus/blob/master/LICENSE)

## One click XML to JSON conversion plugin

Download [this plugin](https://github.com/flipkart-incubator/android-studio-proteus-plugin/blob/master/Plugin/Plugin.jar) for android studio and enable it. Once enabled, you can select any android XML layout file and go to **Tools > Convert XML to JSON**
