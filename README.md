<div align="center">
  <p>
    <a href="https://chattriggers.com">
      <img src="https://chattriggers.com/assets/images/logo-final.png" width="546" alt="ChatTriggers.js" />
    </a>
  </p>
  <p>
    <a href="https://discord.gg/0fNjZyopOvBHZyG8">
      <img src="https://discordapp.com/api/guilds/119493402902528000/embed.png" alt="Discord" />
    </a>
    <a href="https://travis-ci.org/ChatTriggers/ct.js">
      <img src="https://travis-ci.org/ChatTriggers/ct.js.svg?branch=master" alt="Build Status" />
    </a>
    <a href="https://www.codacy.com/app/FalseHonesty/ct.js?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=ChatTriggers/ct.js&amp;utm_campaign=Badge_Grade">
      <img src="https://api.codacy.com/project/badge/Grade/f3bccfe6845d4f6b8733c3948314ea95" alt="Grade" />
    </a>
    <a href="https://github.com/ChatTriggers/ct.js/releases">
      <img src="https://img.shields.io/github/release/ChatTriggers/ct.js/all.svg" alt="Releases" />
    </a>
  </p>
</div>

### *Beta*
[![forthebadge](https://forthebadge.com/images/badges/60-percent-of-the-time-works-every-time.svg)](https://forthebadge.com)<br>
The GraalVM branch is unstable, and not fully tested. As such, do not expect your modules do work first try. We will be rapidly fixing issues and releasing builds, so if you are having issues with your modules, be sure to check out the [releases page](https://github.com/ChatTriggers/ct.js/releases) to make sure you're on the latest build. If so, please open an [issue](https://github.com/ChatTriggers/ct.js/issues) stating that you are on the Graal build.

See the Setup section below for instructions on installing and configuring GraalVM.

# About
ChatTriggers is a framework for Minecraft that allows for live scripting and client modification using various interpreted languages. We provide libraries, wrappers, objects and more to make your life as a modder as easy as possible. Even without the proper wrapper, you can still use exposed Minecraft methods and variables but you will need knowledge of FML mappings.

The basic premise of ChatTriggers is that everything is based around Triggers. From a modding standpoint, Triggers can be thought of as event listeners. These can range from a chat Trigger that runs on a specific chat event matching criteria to a render overlay Trigger that runs when the crosshair is being rendered. We are constantly adding more Triggers and Trigger types as the mod evolves for more integration with Minecraft.
```JavaScript
register("chat", function(name, message, event) {
  cancel(event);
  ChatLib.chat(name + ": " + message);
}).setCriteria("<${name}> ${message}");

register("renderCrosshair", function(event) {
  cancel(event);
  Renderer.drawRect(
    0x50ffffff,
    Renderer.screen.getWidth() / 2 - 1,
    Renderer.screen.getHeight() / 2 - 1,
    2, 2
  );
});
```

You can learn the basics of scripting with ChatTriggers from the [Slate tutorial](https://www.chattriggers.com/slate/) and once you get the basics, check out the [JavaDocs](https://www.chattriggers.com/javadocs/) for a more in depth look at all of the available methods.

# Languages
The GraalVM build of ct.js supports scripting in the following languages: 
- JavaScript ES2019
- Python 3.7
- Ruby 2.6.2
- R 3.5.1

You can use any mixture of the above languages in your modules, provided they have the correct file extension. In order to use objects across languages, you must use GraalVM's `Polyglot` API. 

# Releases
Releases will be released on the [GitHub releases page](https://github.com/ChatTriggers/ct.js/releases). Be sure to check out the [ChatTrigger's Discord](https://discord.gg/0fNjZyopOvBHZyG8) to be notified about new releases.

# Setup
GraalVM is a replacement for the JVM (Java Virtual Machine). As such, the setup required to use this in conjunction with a Minecraft mod is a bit more involved than normal.

The first step is to download GraalVM from their [Github release page](https://github.com/oracle/graal/releases). Down the folder and place it somewhere easily accessable on your computer. In order to install the languages listed above, you must use Graal's `gu` utility. In order to do so, add the following folder to your PATH environment variable:
- `<path/to/GraalVM/folder>/bin` for Windows/Linux
- `<path/to/GraalVM/folder>/Contents/Home/bin` for MacOS

Once the bin folder has been appended to your path, execute the following commands:
- `gu install js`
- `gu install python`
- `gu install ruby`
- `gu install R`

Graal is now fully configured, however your must configure your Minecraft launcher to use it. Open your Minecraft launcher, go to the Launcher Settings page, and make sure Advanced Settings is enabled. GraalVM is configured on a per-profile basis, so navigate to the profile you would like to use it on, check the Java Executable option, and set it to the following path:
- `<path/to/GraalVM/folder>/bin/java` for Windows/Linux
- `<path/to/GraalVM/folder>/Contents/Home/bin/java` for MacOS

Minecraft is not fully configured to use GraalVM. Note that everything else about Minecraft is exactly the same; you still install forge and drop the ct.js jar into the mods folder.

# Issues
Any issue can be opened using the normal [GitHub issue page](https://github.com/ChatTriggers/ct.js/issues). Issues can be anything from bug reports to feature requests. For us to consider an issue to be valid, it needs a simple but effective title that conveys the problem in a few words and a well thought out and well written description.
### Bug Report
- Should be reproducible
- Needs a step by step guide on how to reproduce
- Any evidence of the bug occurring (e.g. images or video) is welcome
### Feature Request
- Needs a general description of the feature requests
- Specifics on what is being requested (e.g. what class you want it in or what it should do) is highly recommended

Duplicate issues will be merged to avoid too much clutter. If an issue is moved to "next" we will usually comment on it to explain how we expect to implement or fix that issue.


## Special Thanks To

<a href="https://www.ej-technologies.com/products/jprofiler/overview.html">
  <img src="https://www.ej-technologies.com/images/product_banners/jprofiler_large.png" alt="jProfiler" />
  The Java Profiler jProfiler
</a>
