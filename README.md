# Happy Cat 🐈🌞
A commandline utility to control your [Elgato Key Light](https://www.elgato.com/de/key-light) and [Key Light Air](https://www.elgato.com/de/key-light-air).  
Change your light settings easily inside scripts or use configuration files to automatically apply your preferred setup over the course of the day, possibly even exploring the use as a [light therapy](https://en.wikipedia.org/wiki/Light_therapy) device.

## Usage
Happy cat is split up into multiple subcommands, each of which uses its own parameters.
To find out more about each command, check out the help pages by appending `--help` after the command.

*Important: Although the Elgato API uses its own values for light temperature internally, the happy cat command line is intended to use Kelvin. However, the conversion is currently broken and might not work properly.*

#### Apply
Reads the provided configuration file, determines the currently active state and applies the state to the given light once.
It's basically a oneshot version of the `daemon` command. For examples of the configuration file, check the corresponding paragraph below.  

*Example*  
```shell
hc apply ./elgato.config elgato.local
```

#### Daemon
Reads the given configuration file, then determines and applies the currently valid configuration every minute.  

*Example*
```shell
hc daemon ./elgato.config elgato.local
```

#### Get
Reads and prints the current configuration of the specified light.  

*Example*
```shell
hc get elgato.local
```

#### Set
Sets the given values of powerstatus, brightness and temperature to the specified light.
```shell
hc set -p ON -b 70 -t 4200 elgato.local
```

## Configuration
To automatically apply settings, you can create your own configuration file which defines the settings for each timeframe.
All values inside `status` are optional, if they are not defined, those parameters will remain unchanged.
Timeframes can cross midnight, but *must not* overlap. If there are overlapping timeframes, the first valid timeframe will be chosen.
```json
{
  "config": [
    {
      "start": "8:30",
      "end": "11:00",
      "status": {
        "power": "ON",
        "brightness": 100,
        "temperature": 6500
      }
    },
    {
      "start": "11:00",
      "end": "20:00",
      "status": {
        "brightness": 70,
        "temperature": 4300
      }
    },
    {
      "start": "20:00",
      "end": "8:30",
      "status": {
        "temperature": 3000
      }
    }
  ]
}
```

## Automation
Probably one could automate the setup using systemd and/or cronjobs, but so far I didn't get to that.