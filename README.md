
# JaRpg
A simple but fun console text-based rpg coded enitrely in java.

## Installation

There are several different methods to run the app

### JAR File
    The jar runs on the JVM so if you do not have java installed, install it [here](https://adoptium.net/en-GB/temurin/releases) choose 21 and the platform you want to run the game on. Afterwards, open the terminal of your choice and paste this:
    ```bash
        curl -O -L https://github.com/Volcar144/other-projects/releases/download/latest/rpg-1.jar && java -jar rpg-1.jar
    ```
### Windows binry
    Download and run the `.exe` installer from the [latest release](https://github.com/Volcar144/other-projects/releases/latest). Once installed, launch **JaRpg** from the Start Menu — a console window will open automatically.

### MacOS
Download the `.dmg` from the [latest release](https://github.com/Volcar144/other-projects/releases/latest), open it, and drag the app to your Applications folder. Then run it from your terminal:
```bash
/Applications/JaRpg.app/Contents/MacOS/JaRpg
```

### Linux
    Download the `.deb` from the [latest release](https://github.com/Volcar144/other-projects/releases/latest) and install it:
    ```bash
    sudo dpkg -i JaRpg_*.deb
    JaRpg
    ```
    
## Features

- Player Creation
- Map Loading from file(embedded)
- Coloured text
- Shop system
- Full actions system
- Combat system
- Room-based movement


## Demo
![Untitled-design.gif](https://i.postimg.cc/Nf9dq8hq/Untitled-design.gif)


## Authors

- [@Volcar144](https://www.github.com/volcar144)

## Contributing

If you want to contribut to JaRpg, it is just written in plain java, utility types and other types are in /types, run mvn package to package back into jar format