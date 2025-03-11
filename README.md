# MCServerAPI-Bukkit
Bukkit implementation of MCServerAPI

## Setup development environment
You need NMS to compile the project.
Make sure you ran BuildTools at least once with the Minecraft version the project needs.
It will install the required maven artifacts into you local maven repository cache.

You can use Docker for that:
```
docker build --build-arg JDK_VERSION=8 -t spigot-builder -f setup/Dockerfile setup/
docker run --rm -e MC_VERSION=1.12.2 -v ~/.m2:/root/.m2 spigot-builder
```