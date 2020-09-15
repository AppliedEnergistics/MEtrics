[![Build master](https://img.shields.io/github/workflow/status/AppliedEnergistics/MEtrics/Build%20master?style=flat-square)](https://github.com/AppliedEnergistics/Applied-Energistics-2/actions?query=workflow%3A%22Build+master%22)
[![Latest Release](https://img.shields.io/github/v/release/AppliedEnergistics/MEtrics?style=flat-square&label=Release)](https://github.com/AppliedEnergistics/Applied-Energistics-2/releases)

# MEtrics

## About

The goal is to provide a simple mod to acquire metrics about a running modded minecraft server or client including more detailed ones about mods using our API.

The idea was to have a companion mod for [Applied Energistics 2](https://github.com/AppliedEnergistics/Applied-Energistics-2/) and allow monitoring certain metrics like the amount of networks, cache statistics, or timings for crafting requests and similar tasks.

## Contacts

* [GitHub](https://github.com/AppliedEnergistics/MEtrics)
* [Discord](https://discord.gg/GygKjjm) (AE2)
* [Applied Energistics 2](https://github.com/AppliedEnergistics/Applied-Energistics-2/)

## License

* MEtrics API
  - (c) 2020 Team AppliedEnergistics et al
  - [![License](https://img.shields.io/badge/license-MIT-97ca00?style=flat-square)](http://opensource.org/licenses/MIT)
* MEtrics
  - (c) 2020 Team AppliedEnergistics et al
  - [![License](https://img.shields.io/github/license/AppliedEnergistics/MEtrics?style=flat-square)](https://github.com/AppliedEnergistics/MEtrics/blob/master/LICENSE)
* Micrometer
  - The metrics API we use internally.
  - [![License](https://img.shields.io/github/license/micrometer-metrics/micrometer?style=flat-square)](https://github.com/micrometer-metrics/micrometer/blob/master/LICENSE)

## Supported Mincraft Modding APIs

* **[Fabric](https://fabricmc.net/)**
* **[Forge](https://github.com/MinecraftForge/MinecraftForge)**

Please refer to our [Feature Matrix](https://github.com/AppliedEnergistics/MEtrics/wiki/Feature-Matrix) for more details.
Not all metrics might be supported in every case.

Currently there are no plans to support any other API.

## Supported Monitoring Backends

* **[InfluxDB](https://www.influxdata.com/)**
* **[Prometheus](https://prometheus.io/)**

Micrometer supports various other backends. But for now we limit it to these two as these will the most accessible ones within the modded minecraft community. However we will gladly consider supporting others when they see enough support.

### Recommended Additions

#### [Chronograf](https://www.influxdata.com/time-series-platform/chronograf/) 

A UI to visualize data pushed to an InfluxDB. A bit easier to setup a whole TICK stack with [Telegraf](https://www.influxdata.com/time-series-platform/telegraf/), [InfluxDB](https://www.influxdata.com/products/influxdb-overview/), [Chronograf](https://www.influxdata.com/time-series-platform/chronograf/) and [Kapacitor](https://www.influxdata.com/time-series-platform/kapacitor/). Especially within a existing docker system.

InfluxDB v2 is untested and potentially not working currently.


#### [Grafana](https://grafana.com/) 

More capabable than Chronograf and supports various other backends like Prometheus or InfluxDB. At the cost of a more complex setup

